package tes.common.tileentity;

import com.mojang.authlib.GameProfile;
import tes.TES;
import tes.common.TESCommonFactory;
import tes.common.block.table.TESBlockCraftingTable;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import tes.common.database.TESMaterial;
import tes.common.inventory.TESContainerCraftingTable;
import tes.common.item.other.TESItemMountArmor;
import tes.common.item.weapon.TESItemCrossbow;
import tes.common.item.weapon.TESItemThrowingAxe;
import tes.common.recipe.TESRecipe;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class TESTileEntityUnsmeltery extends TESTileEntityAlloyForge {
	private static final Random UNSMELTING_RAND = new Random();
	private static final Map<Pair<Item, Integer>, Integer> UNSMELTABLE_CRAFTING_COUNTS = new HashMap<>();

	private static final Map<ItemStack, ItemStack> UNSMELT_CUSTOM = new HashMap<>();

	static {
		UNSMELT_CUSTOM.put(new ItemStack(Items.bucket), new ItemStack(Items.iron_ingot));

		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.birdCage, 1, 2), new ItemStack(TESItems.silverNugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESItems.gobletSilver), new ItemStack(TESItems.silverNugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.silverBars), new ItemStack(TESItems.silverNugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.chandelier, 1, 2), new ItemStack(TESItems.silverNugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESItems.silverRing), new ItemStack(TESItems.silverNugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.gateSilver), new ItemStack(TESItems.silverNugget));

		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.birdCage, 1, 3), new ItemStack(Items.gold_nugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESItems.gobletGold), new ItemStack(Items.gold_nugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.goldBars), new ItemStack(Items.gold_nugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.chandelier, 1, 3), new ItemStack(Items.gold_nugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESItems.goldRing), new ItemStack(Items.gold_nugget));
		UNSMELT_CUSTOM.put(new ItemStack(TESBlocks.gateGold), new ItemStack(Items.gold_nugget));
	}

	private float prevRocking;
	private float rocking;
	private float prevRockingPhase;
	private float rockingPhase = UNSMELTING_RAND.nextFloat() * 3.1415927F * 2.0F;
	private boolean serverActive;
	private boolean clientActive;

	public static ItemStack getEquipmentMaterial(ItemStack itemstack) {
		if (itemstack == null) {
			return null;
		}
		Item item = itemstack.getItem();
		ItemStack material = null;
		if (item instanceof ItemTool) {
			material = ((ItemTool) item).func_150913_i().getRepairItemStack();
		} else if (item instanceof ItemSword) {
			material = TESMaterial.getToolMaterialByName(((ItemSword) item).getToolMaterialName()).getRepairItemStack();
		} else if (item instanceof ItemHoe) {
			material = TESMaterial.getToolMaterialByName(((ItemHoe) item).getToolMaterialName()).getRepairItemStack();
		} else if (item instanceof TESItemCrossbow) {
			material = ((TESItemCrossbow) item).getCrossbowMaterial().getRepairItemStack();
		} else if (item instanceof TESItemThrowingAxe) {
			material = ((TESItemThrowingAxe) item).getAxeMaterial().getRepairItemStack();
		} else if (item instanceof ItemArmor) {
			material = new ItemStack(((ItemArmor) item).getArmorMaterial().func_151685_b());
		} else if (item instanceof TESItemMountArmor) {
			material = new ItemStack(((TESItemMountArmor) item).getMountArmorMaterial().func_151685_b());
		}
		if (material != null) {
			if (item.getIsRepairable(itemstack, material)) {
				return material;
			}
		} else {
			for (Map.Entry<ItemStack, ItemStack> entry : UNSMELT_CUSTOM.entrySet()) {
				ItemStack key = entry.getKey();
				ItemStack value = entry.getValue();
				if (itemStackEquals(itemstack, key)) {
					return value;
				}
			}
		}
		return null;
	}

	private static boolean itemStackEquals(ItemStack itemStack1, ItemStack itemStack2) {
		return itemStack1.getItem() == itemStack2.getItem() && itemStack1.getItemDamage() == itemStack2.getItemDamage();
	}

	public boolean canBeUnsmelted(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		ItemStack material = getEquipmentMaterial(itemstack);
		return material != null && TileEntityFurnace.getItemBurnTime(material) == 0 && (!(itemstack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem(itemstack.getItem()).getMaterial().getCanBurn()) && determineResourcesUsed(itemstack, material) > 0;
	}

	@Override
	protected boolean canDoSmelting() {
		ItemStack input = inventory[inputSlots[0]];
		if (input == null) {
			return false;
		}
		ItemStack result = getLargestUnsmeltingResult(input);
		if (result == null) {
			return false;
		}
		ItemStack output = inventory[outputSlots[0]];
		if (output == null) {
			return true;
		}
		if (!output.isItemEqual(result)) {
			return false;
		}
		int resultSize = output.stackSize + result.stackSize;
		return resultSize <= getInventoryStackLimit() && resultSize <= result.getMaxStackSize();
	}

	@Override
	public boolean canMachineInsertInput(ItemStack itemstack) {
		return itemstack != null && getLargestUnsmeltingResult(itemstack) != null;
	}

	private int countMatchingIngredients(ItemStack material, Iterable<?> ingredientList, List<IRecipe> recursiveCheckedRecipes) {
		int i = 0;
		for (Object obj : ingredientList) {
			if (obj instanceof ItemStack) {
				ItemStack ingredient = (ItemStack) obj;
				if (OreDictionary.itemMatches(material, ingredient, false)) {
					i++;
					continue;
				}
				int sub = determineResourcesUsed(ingredient, material, recursiveCheckedRecipes);
				if (sub > 0) {
					i += sub;
				}
				continue;
			}
			if (obj instanceof List) {
				Iterable<ItemStack> oreIngredients = (Iterable<ItemStack>) obj;
				boolean matched = false;
				for (ItemStack ingredient : oreIngredients) {
					if (OreDictionary.itemMatches(material, ingredient, false)) {
						matched = true;
						break;
					}
				}
				if (matched) {
					i++;
					continue;
				}
				for (ItemStack ingredient : oreIngredients) {
					int sub = determineResourcesUsed(ingredient, material, recursiveCheckedRecipes);
					if (sub > 0) {
						i += sub;
					}
				}
			}
		}
		return i;
	}

	private int determineResourcesUsed(ItemStack itemstack, ItemStack material) {
		return determineResourcesUsed(itemstack, material, null);
	}

	private int determineResourcesUsed(ItemStack itemstack, ItemStack material, List<IRecipe> recursiveCheckedRecipes) {
		List<IRecipe> recursiveCheckedRecipes1 = recursiveCheckedRecipes;
		if (itemstack == null) {
			return 0;
		}
		Pair<Item, Integer> key = Pair.of(itemstack.getItem(), itemstack.getItemDamage());
		if (UNSMELTABLE_CRAFTING_COUNTS.containsKey(key)) {
			return UNSMELTABLE_CRAFTING_COUNTS.get(key);
		}
		int count = 0;
		Collection<List<IRecipe>> allRecipeLists = new ArrayList<>();
		allRecipeLists.add(CraftingManager.getInstance().getRecipeList());
		EntityPlayer player = getProxyPlayer();
		for (TESBlockCraftingTable table : TESBlockCraftingTable.ALL_CRAFTING_TABLES) {
			Object container = TESCommonFactory.getGuiHandler().getServerGuiElement(table.getGuiId().ordinal(), player, worldObj, 0, 0, 0);
			if (container instanceof TESContainerCraftingTable) {
				TESContainerCraftingTable containerTable = (TESContainerCraftingTable) container;
				allRecipeLists.add(containerTable.getRecipeList());
			}
		}
		if (recursiveCheckedRecipes1 == null) {
			recursiveCheckedRecipes1 = new ArrayList<>();
		}
		label63:
		for (List<IRecipe> recipes : allRecipeLists) {
			for (IRecipe recipesObj : recipes) {
				if (!recursiveCheckedRecipes1.contains(recipesObj)) {
					ItemStack result = recipesObj.getRecipeOutput();
					if (result != null && result.getItem() == itemstack.getItem() && (itemstack.isItemStackDamageable() || result.getItemDamage() == itemstack.getItemDamage())) {
						recursiveCheckedRecipes1.add(recipesObj);
						if (recipesObj instanceof ShapedRecipes) {
							ShapedRecipes shaped = (ShapedRecipes) recipesObj;
							ItemStack[] ingredients = shaped.recipeItems;
							int i = countMatchingIngredients(material, Arrays.asList(ingredients), recursiveCheckedRecipes1);
							i /= result.stackSize;
							if (i > 0) {
								count = i;
								break label63;
							}
						}
						if (recipesObj instanceof ShapelessRecipes) {
							ShapelessRecipes shapeless = (ShapelessRecipes) recipesObj;
							List<?> ingredients = shapeless.recipeItems;
							int i = countMatchingIngredients(material, ingredients, recursiveCheckedRecipes1);
							i /= result.stackSize;
							if (i > 0) {
								count = i;
								break label63;
							}
						}
						if (recipesObj instanceof ShapedOreRecipe) {
							ShapedOreRecipe shaped = (ShapedOreRecipe) recipesObj;
							Object[] ingredients = shaped.getInput();
							int i = countMatchingIngredients(material, Arrays.asList(ingredients), recursiveCheckedRecipes1);
							i /= result.stackSize;
							if (i > 0) {
								count = i;
								break label63;
							}
						}
						if (recipesObj instanceof ShapelessOreRecipe) {
							ShapelessOreRecipe shapeless = (ShapelessOreRecipe) recipesObj;
							List<Object> ingredients = shapeless.getInput();
							int i = countMatchingIngredients(material, ingredients, recursiveCheckedRecipes1);
							i /= result.stackSize;
							if (i > 0) {
								count = i;
								break label63;
							}
						}
					}
				}
			}
		}
		UNSMELTABLE_CRAFTING_COUNTS.put(key, count);
		return count;
	}

	@Override
	public void doSmelt() {
		if (canDoSmelting()) {
			ItemStack input = inventory[inputSlots[0]];
			ItemStack result = getRandomUnsmeltingResult(input);
			if (result != null) {
				if (inventory[outputSlots[0]] == null) {
					inventory[outputSlots[0]] = result.copy();
				} else if (inventory[outputSlots[0]].isItemEqual(result)) {
					inventory[outputSlots[0]].stackSize += result.stackSize;
				}
			}
			inventory[inputSlots[0]].stackSize--;
			if (inventory[inputSlots[0]].stackSize <= 0) {
				inventory[inputSlots[0]] = null;
			}
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		data.setBoolean("Active", serverActive);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
	}

	@Override
	public int getForgeInvSize() {
		return 3;
	}

	@Override
	public String getForgeName() {
		return StatCollector.translateToLocal("tes.container.unsmeltery");
	}

	private ItemStack getLargestUnsmeltingResult(ItemStack itemstack) {
		if (itemstack == null || !canBeUnsmelted(itemstack)) {
			return null;
		}
		ItemStack material = getEquipmentMaterial(itemstack);
		int items = determineResourcesUsed(itemstack, material);
		int meta = material.getItemDamage();
		if (meta == 32767) {
			meta = 0;
		}
		return new ItemStack(material.getItem(), items, meta);
	}

	private EntityPlayer getProxyPlayer() {
		if (!worldObj.isRemote) {
			return FakePlayerFactory.get((WorldServer) worldObj, new GameProfile(null, "TESUnsmeltery"));
		}
		return TES.proxy.getClientPlayer();
	}

	public ItemStack getRandomUnsmeltingResult(ItemStack itemstack) {
		ItemStack result = getLargestUnsmeltingResult(itemstack);
		if (result == null) {
			return null;
		}
		float items = result.stackSize;
		items *= 0.8f;
		if (itemstack.isItemStackDamageable()) {
			items *= (float) (itemstack.getMaxDamage() - itemstack.getItemDamage()) / itemstack.getMaxDamage();
		}
		int items_int = Math.round(items * MathHelper.randomFloatClamp(UNSMELTING_RAND, 0.7f, 1.0f));
		if (items_int <= 0) {
			return null;
		}
		return new ItemStack(result.getItem(), items_int, result.getItemDamage());
	}

	public float getRockingAmount(float tick) {
		float mag = prevRocking + (rocking - prevRocking) * tick;
		float phase = prevRockingPhase + (rockingPhase - prevRockingPhase) * tick;
		return mag * MathHelper.sin(phase);
	}

	@Override
	public int getSmeltingDuration() {
		return 400;
	}

	@Override
	public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
		NBTTagCompound data = packet.func_148857_g();
		clientActive = data.getBoolean("Active");
	}

	@Override
	public void setupForgeSlots() {
		inputSlots = new int[]{0};
		fuelSlot = 1;
		outputSlots = new int[]{2};
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			prevRocking = rocking;
			prevRockingPhase = rockingPhase;
			rockingPhase += 0.1F;
			if (clientActive) {
				rocking += 0.05F;
			} else {
				rocking -= 0.01F;
			}
			rocking = MathHelper.clamp_float(rocking, 0.0F, 1.0F);
		} else {
			boolean prevServerActive = serverActive;
			serverActive = isSmelting();
			if (serverActive != prevServerActive) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}
}