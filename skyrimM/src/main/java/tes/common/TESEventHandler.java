package tes.common;

import codechicken.nei.NEIModContainer;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.ItemInfo;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import tes.TES;
import tes.common.block.TESVanillaSaplings;
import tes.common.block.other.*;
import tes.common.block.sapling.TESBlockSaplingBase;
import tes.common.block.table.TESBlockCraftingTable;
import tes.common.database.*;
import tes.common.enchant.TESEnchantment;
import tes.common.enchant.TESEnchantmentHelper;
import tes.common.enchant.TESEnchantmentWeaponSpecial;
import tes.common.entity.animal.TESEntityButterfly;
import tes.common.entity.animal.TESEntityJungleScorpion;
import tes.common.entity.animal.TESEntityZebra;
import tes.common.entity.dragon.TESDragonLifeStage;
import tes.common.entity.dragon.TESEntityDragon;
import tes.common.entity.other.TESEntityHumanBase;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.TESEntityProstitute;
import tes.common.entity.other.iface.*;
import tes.common.entity.other.inanimate.*;
import tes.common.entity.other.utils.TESPlateFallingInfo;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionBounties;
import tes.common.faction.TESFactionRelations;
import tes.common.item.TESPoisonedDrinks;
import tes.common.item.TESWeaponStats;
import tes.common.item.other.*;
import tes.common.item.weapon.TESItemBow;
import tes.common.item.weapon.TESItemCrossbow;
import tes.common.item.weapon.TESItemLance;
import tes.common.item.weapon.TESItemSword;
import tes.common.network.*;
import tes.common.quest.TESMiniQuest;
import tes.common.tileentity.TESTileEntityPlate;
import tes.common.util.TESCrashHandler;
import tes.common.util.TESEnumDyeColor;
import tes.common.util.TESModChecker;
import tes.common.world.TESTeleporter;
import tes.common.world.TESWorldProvider;
import tes.common.world.TESWorldType;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.variant.TESBiomeVariantStorage;
import integrator.NEITESIntegratorConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TESEventHandler {
	public static final TESEventHandler INSTANCE = new TESEventHandler();

	private TESItemBow proxyBowItemServer;
	private TESItemBow proxyBowItemClient;

	private TESEventHandler() {
	}

	private static void dechant(ItemStack itemstack, EntityPlayer entityplayer) {
		if (!entityplayer.capabilities.isCreativeMode && itemstack != null && itemstack.isItemEnchanted()) {
			Item item = itemstack.getItem();
			if (!(item instanceof ItemFishingRod)) {
				itemstack.getTagCompound().removeTag("ench");
			}
		}
	}

	private static String getUsernameWithoutWebservice(UUID player) {
		GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(player);
		if (profile != null && !StringUtils.isBlank(profile.getName())) {
			return profile.getName();
		}
		String cachedName = UsernameCache.getLastKnownUsername(player);
		if (cachedName != null && !StringUtils.isBlank(cachedName)) {
			return cachedName;
		}
		return player.toString();
	}

	private static void cancelAttackEvent(LivingAttackEvent event) {
		event.setCanceled(true);
		DamageSource source = event.source;
		if (source instanceof EntityDamageSourceIndirect) {
			source.getSourceOfDamage();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void getBlockDrops(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer entityplayer = event.harvester;
		Block block = event.block;
		if (entityplayer != null) {
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
			if (itemstack != null && block instanceof BlockWeb && itemstack.getItem() instanceof ItemShears) {
				int meta = 0;
				Item item = Item.getItemFromBlock(block);
				if (item != null && item.getHasSubtypes()) {
					meta = event.blockMetadata;
				}
				ItemStack silkDrop = new ItemStack(item, 1, meta);
				event.drops.clear();
				event.drops.add(silkDrop);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		if (!TESConfig.enchantingVanilla) {
			if (event.left != null && event.left.getItem() instanceof ItemEnchantedBook || event.right != null && event.right.getItem() instanceof ItemEnchantedBook) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onArrowNock(ArrowNockEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = entityplayer.worldObj;
		ItemStack itemstack = event.result;
		if (itemstack != null && itemstack.getItem() instanceof ItemBow && !(itemstack.getItem() instanceof TESItemBow) && !(itemstack.getItem() instanceof TESItemCrossbow)) {
			if (!world.isRemote) {
				if (proxyBowItemServer == null) {
					proxyBowItemServer = new TESItemBow(Item.ToolMaterial.WOOD);
					event.result = proxyBowItemServer.onItemRightClick(itemstack, world, entityplayer);
					proxyBowItemServer = null;
					event.setCanceled(true);
				}
			} else if (proxyBowItemClient == null) {
				proxyBowItemClient = new TESItemBow(Item.ToolMaterial.WOOD);
				event.result = proxyBowItemClient.onItemRightClick(itemstack, world, entityplayer);
				proxyBowItemClient = null;
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer entityplayer = event.getPlayer();
		Block block = event.block;
		int meta = event.blockMetadata;
		World world = event.world;
		int i = event.x;
		int j = event.y;
		int k = event.z;
		if (!world.isRemote && TESBannerProtection.isProtected(world, i, j, k, TESBannerProtection.forPlayer(entityplayer, TESBannerProtection.Permission.FULL), true)) {
			event.setCanceled(true);
			return;
		}
		if (!world.isRemote && entityplayer != null) {
				boolean grapesAbove = false;
				for (int j1 = 1; j1 <= 3; j1++) {
					int j2 = j + j1;
					Block above = world.getBlock(i, j2, k);
					int aboveMeta = world.getBlockMetadata(i, j2, k);
					if (TESBlockGrapevine.isFullGrownGrapes(above, aboveMeta)) {
						grapesAbove = true;
					}
				}
			}
		}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onBlockInteract(PlayerInteractEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = entityplayer.worldObj;
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		int i = event.x;
		int j = event.y;
		int k = event.z;
		int side = event.face;
		if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			Block block = world.getBlock(i, j, k);
			int meta = world.getBlockMetadata(i, j, k);
			TESBannerProtection.Permission perm = TESBannerProtection.Permission.FULL;
			boolean mightBeAbleToAlterWorld = entityplayer.isSneaking() && itemstack != null;
			if (!mightBeAbleToAlterWorld) {
				if (block instanceof BlockDoor || block instanceof BlockTrapDoor || block instanceof BlockFenceGate || block instanceof TESBlockGate) {
					perm = TESBannerProtection.Permission.DOORS;
				} else if (block instanceof BlockWorkbench || block instanceof TESBlockCraftingTable || block instanceof BlockAnvil || block instanceof TESBlockCommandTable) {
					perm = TESBannerProtection.Permission.TABLES;
				} else if (world.getTileEntity(i, j, k) instanceof IInventory) {
					if (block instanceof TESBlockBarrel || block instanceof TESBlockKebabStand) {
						perm = TESBannerProtection.Permission.FOOD;
					} else {
						perm = TESBannerProtection.Permission.CONTAINERS;
					}
				} else if (block instanceof TESBlockArmorStand || block instanceof TESBlockWeaponRack || block == Blocks.bookshelf || block instanceof BlockJukebox) {
					perm = TESBannerProtection.Permission.CONTAINERS;
				} else if (block instanceof BlockEnderChest) {
					perm = TESBannerProtection.Permission.PERSONAL_CONTAINERS;
				} else if (block instanceof TESBlockPlate || block instanceof BlockCake || block instanceof TESBlockPlaceableFood || block instanceof TESBlockMug) {
					perm = TESBannerProtection.Permission.FOOD;
				} else if (block instanceof BlockBed) {
					perm = TESBannerProtection.Permission.BEDS;
				} else if (block instanceof BlockButton || block instanceof BlockLever) {
					perm = TESBannerProtection.Permission.SWITCHES;
				}
			}
			if (!world.isRemote && TESBannerProtection.isProtected(world, i, j, k, TESBannerProtection.forPlayer(entityplayer, perm), true)) {
				event.setCanceled(true);
				if (block instanceof BlockDoor) {
					world.markBlockForUpdate(i, j - 1, k);
					world.markBlockForUpdate(i, j, k);
					world.markBlockForUpdate(i, j + 1, k);
				} else if (block instanceof TESBlockPlate && TESBlockPlate.getFoodItem(world, i, j, k) != null) {
					world.markBlockForUpdate(i, j, k);
				}
				return;
			}
			if (block == Blocks.flower_pot && meta == 0 && itemstack != null && TESBlockFlowerPot.canAcceptPlant(itemstack)) {
				TES.proxy.placeFlowerInPot(world, i, j, k, side, itemstack);
				if (!entityplayer.capabilities.isCreativeMode) {
					itemstack.stackSize--;
				}
				event.setCanceled(true);
				return;
			}
			if (itemstack != null && block == Blocks.cauldron && meta > 0) {
				TESItemMug.Vessel drinkVessel = null;
				for (TESItemMug.Vessel v : TESItemMug.Vessel.values()) {
					if (v.getEmptyVesselItem() == itemstack.getItem()) {
						drinkVessel = v;
						break;
					}
				}
				if (drinkVessel != null) {
					TES.proxy.fillMugFromCauldron(world, i, j, k, side, itemstack);
					itemstack.stackSize--;
					ItemStack mugFill = new ItemStack(TESItems.mugWater);
					TESItemMug.setVessel(mugFill, drinkVessel, true);
					if (itemstack.stackSize <= 0) {
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, mugFill);
					} else if (!entityplayer.inventory.addItemStackToInventory(mugFill)) {
						entityplayer.dropPlayerItemWithRandomChoice(mugFill, false);
					}
					event.setCanceled(true);
					return;
				}
			}
			if (!world.isRemote && block instanceof TESBlockPlate && entityplayer.isSneaking()) {
				TileEntity tileentity = world.getTileEntity(i, j, k);
				if (tileentity instanceof TESTileEntityPlate) {
					TESTileEntityPlate plate = (TESTileEntityPlate) tileentity;
					ItemStack plateItem = plate.getFoodItem();
					if (plateItem != null) {
						((TESBlockPlate) block).dropOnePlateItem(plate);
						plateItem.stackSize--;
						plate.setFoodItem(plateItem);
						event.setCanceled(true);
						return;
					}
				}
			}
			if (!world.isRemote && block instanceof BlockCauldron && itemstack != null) {
				int cauldronMeta = BlockCauldron.func_150027_b(meta);
				if (cauldronMeta > 0) {
					boolean undyed = false;
					Item item = itemstack.getItem();
					if (item instanceof TESItemPouch && TESItemPouch.isPouchDyed(itemstack)) {
						TESItemPouch.removePouchDye(itemstack);
						undyed = true;
					} else if (item instanceof TESItemPipe && TESItemPipe.isPipeDyed(itemstack)) {
						TESItemPipe.removePipeDye(itemstack);
						undyed = true;
					} else if (item instanceof TESItemLeatherHat && (TESItemLeatherHat.isHatDyed(itemstack) || TESItemLeatherHat.isFeatherDyed(itemstack))) {
						TESItemLeatherHat.removeHatAndFeatherDye(itemstack);
						undyed = true;
					} else if (item instanceof TESItemFeatherDyed && TESItemFeatherDyed.isFeatherDyed(itemstack)) {
						TESItemFeatherDyed.removeFeatherDye(itemstack);
						undyed = true;
					} else if (item instanceof TESItemRobes && TESItemRobes.areRobesDyed(itemstack)) {
						TESItemRobes.removeRobeDye(itemstack);
						undyed = true;
					}
					if (undyed) {
						((BlockCauldron) block).func_150024_a(world, i, j, k, cauldronMeta - 1);
						event.setCanceled(true);
						return;
					}
				}
			}
			if (!world.isRemote && itemstack != null && itemstack.getItem() == Items.dye && itemstack.getItemDamage() == 15) {
				if (block instanceof BlockLog) {
					int logFacing = meta & 0xC;
					if (logFacing != 12) {
						boolean onInnerFace = false;
						switch (logFacing) {
							case 0:
								onInnerFace = side == 0 || side == 1;
								break;
							case 4:
								onInnerFace = side == 4 || side == 5;
								break;
							case 8:
								onInnerFace = side == 2 || side == 3;
								break;
						}
						if (onInnerFace) {
							meta |= 0xC;
							world.setBlockMetadataWithNotify(i, j, k, meta, 3);
							world.playAuxSFX(2005, i, j, k, 0);
							if (!entityplayer.capabilities.isCreativeMode) {
								itemstack.stackSize--;
							}
							event.setCanceled(true);
							return;
						}
					}
				}
			}
			if (block == Blocks.bookshelf && !entityplayer.isSneaking() && TESBlockBookshelfStorage.canOpenBookshelf(entityplayer) && !world.isRemote) {
				world.setBlock(i, j, k, TESBlocks.bookshelfStorage, 0, 3);
				boolean flag = TESBlocks.bookshelfStorage.onBlockActivated(world, i, j, k, entityplayer, side, 0.5F, 0.5F, 0.5F);
				if (!flag) {
					world.setBlock(i, j, k, Blocks.bookshelf, 0, 3);
				}
				event.setCanceled(true);
				return;
			}
			if (block == Blocks.enchanting_table && !TESConfig.isEnchantingEnabled(world) && !world.isRemote) {
				TESLevelData.getData(entityplayer).sendMessageIfNotReceived(TESGuiMessageTypes.ENCHANTING);
				event.setCanceled(true);
				return;
			}
			if (block == Blocks.anvil && (TESConfig.isTESEnchantingEnabled(world) || !TESConfig.isEnchantingEnabled(world)) && !world.isRemote) {
				entityplayer.openGui(TES.instance, TESGuiId.ANVIL.ordinal(), world, i, j, k);
				event.setCanceled(true);
				return;
			}
		}
		if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
			world.getBlock(i, j, k);
			world.getBlockMetadata(i, j, k);
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int i1 = i + dir.offsetX;
			int j1 = j + dir.offsetY;
			int k1 = k + dir.offsetZ;
			Block block1 = world.getBlock(i1, j1, k1);
			if (!world.isRemote && TESBannerProtection.isProtected(world, i1, j1, k1, TESBannerProtection.forPlayer(entityplayer, TESBannerProtection.Permission.FULL), true) && block1 instanceof BlockFire) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onBreakingSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
		EntityPlayer entityplayer = event.entityPlayer;
		Block block = event.block;
		int meta = event.metadata;
		float speed = event.newSpeed;
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		if (itemstack != null) {
			float baseDigSpeed = itemstack.getItem().getDigSpeed(itemstack, block, meta);
			if (baseDigSpeed > 1.0F) {
				speed *= TESEnchantmentHelper.calcToolEfficiency(itemstack);
			}
		}
		event.newSpeed = speed;
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onChunkDataLoad(ChunkDataEvent.Load event) {
		World world = event.world;
		Chunk chunk = event.getChunk();
		NBTTagCompound data = event.getData();
		if (!world.isRemote && world.provider instanceof TESWorldProvider) {
			TESBiomeVariantStorage.loadChunkVariants(world, chunk, data);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onChunkDataSave(ChunkDataEvent.Save event) {
		World world = event.world;
		Chunk chunk = event.getChunk();
		NBTTagCompound data = event.getData();
		if (!world.isRemote && world.provider instanceof TESWorldProvider) {
			TESBiomeVariantStorage.saveChunkVariants(world, chunk, data);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onChunkStartWatching(ChunkWatchEvent.Watch event) {
		EntityPlayerMP entityplayer = event.player;
		World world = entityplayer.worldObj;
		ChunkCoordIntPair chunkCoords = event.chunk;
		Chunk chunk = world.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPos);
		if (!world.isRemote && world.provider instanceof TESWorldProvider) {
			TESBiomeVariantStorage.sendChunkVariantsToPlayer(world, chunk, entityplayer);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onChunkStopWatching(ChunkWatchEvent.UnWatch event) {
		EntityPlayerMP entityplayer = event.player;
		World world = entityplayer.worldObj;
		ChunkCoordIntPair chunkCoords = event.chunk;
		Chunk chunk = world.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPos);
		if (!world.isRemote && world.provider instanceof TESWorldProvider) {
			TESBiomeVariantStorage.sendUnwatchToPlayer(chunk, entityplayer);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if ("tes".equals(event.modID)) {
			TESConfig.load();
		}
		if (TESModChecker.hasNEI() && TES.proxy.isClient()) {
			for (IConfigureNEI plugin : NEIModContainer.plugins) {
				if (plugin.getClass() == NEITESIntegratorConfig.class) {
					for (ItemStack itemStack : NEITESIntegratorConfig.HIDDEN_ITEMS) {
						if (ItemInfo.hiddenItems.contains(itemStack)) {
							ItemInfo.hiddenItems.remove(itemStack);
						}
					}
					plugin.loadConfig();
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		EntityPlayer entityplayer = event.player;
		ItemStack itemstack = event.crafting;
		if (!entityplayer.worldObj.isRemote) {
			if (itemstack.getItem() == Items.saddle) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.craftSaddle);
			}
			if (itemstack.getItem() == TESItems.bronzeIngot) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.craftBronze);
			}
			if (itemstack.getItem() == Item.getItemFromBlock(TESBlocks.bomb)) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.craftBomb);
			}
			if (itemstack.getItem() == Item.getItemFromBlock(TESBlocks.wildFireJar)) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.craftWildFire);
			}
			for (TESEnumDyeColor color : TESEnumDyeColor.values()) {
				if (itemstack.getItem() == Item.getItemFromBlock(TESBlocks.CONCRETE_POWDER.get(color))) {
					TESLevelData.getData(entityplayer).addAchievement(TESAchievement.getConcrete);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onEntityAttackedByPlayer(AttackEntityEvent event) {
		Entity entity = event.target;
		World world = entity.worldObj;
		EntityPlayer entityplayer = event.entityPlayer;
		if (!world.isRemote && (entity instanceof EntityHanging || entity instanceof TESEntityRugBase) && TESBannerProtection.isProtected(world, entity, TESBannerProtection.forPlayer(entityplayer, TESBannerProtection.Permission.FULL), true)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SuppressWarnings({"MethodMayBeStatic", "CastConflictsWithInstanceof"})
	public void onEntityInteract(EntityInteractEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = entityplayer.worldObj;
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		Entity entity = event.target;
		if (!world.isRemote && (entity instanceof EntityHanging || entity instanceof TESEntityRugBase) && TESBannerProtection.isProtected(world, entity, TESBannerProtection.forPlayer(entityplayer, TESBannerProtection.Permission.FULL), true)) {
			event.setCanceled(true);
			return;
		}
		if ((entity instanceof EntityCow || entity instanceof TESEntityZebra) && TESItemMug.isItemEmptyDrink(itemstack)) {
			TESItemMug.Vessel vessel = TESItemMug.getVessel(itemstack);
			ItemStack milkItem = new ItemStack(TESItems.mugMilk);
			TESItemMug.setVessel(milkItem, vessel, true);
			if (!entityplayer.capabilities.isCreativeMode) {
				itemstack.stackSize--;
			}
			if (itemstack.stackSize <= 0 || entityplayer.capabilities.isCreativeMode) {
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, milkItem);
			} else if (!entityplayer.inventory.addItemStackToInventory(milkItem)) {
				entityplayer.dropPlayerItemWithRandomChoice(milkItem, false);
			}
			event.setCanceled(true);
			return;
		}
		if (entity instanceof EntityWolf) {
			EntityWolf wolf = (EntityWolf) entity;
			if (itemstack != null && TES.isOreNameEqual(itemstack, "bone") && itemstack.getItem() != Items.bone) {
				Item prevItem = itemstack.getItem();
				itemstack.func_150996_a(Items.bone);
				boolean flag = wolf.interact(entityplayer);
				itemstack.func_150996_a(prevItem);
				if (flag) {
					event.setCanceled(true);
					return;
				}
			}
			if (itemstack != null) {
				int dyeType = TESItemDye.isItemDye(itemstack);
				if (dyeType >= 0 && itemstack.getItem() != Items.dye) {
					Item prevItem = itemstack.getItem();
					int prevMeta = itemstack.getItemDamage();
					itemstack.func_150996_a(Items.dye);
					itemstack.setItemDamage(dyeType);
					boolean flag = wolf.interact(entityplayer);
					itemstack.func_150996_a(prevItem);
					itemstack.setItemDamage(prevMeta);
					if (flag) {
						event.setCanceled(true);
						return;
					}
				}
			}
		}
		if (entity instanceof TESTradeable && ((TESTradeable) entity).canTradeWith(entityplayer)) {
			if (entity instanceof TESUnitTradeable) {
				entityplayer.openGui(TES.instance, TESGuiId.TRADE_UNIT_TRADE_INTERACT.ordinal(), world, entity.getEntityId(), 0, 0);
			} else {
				entityplayer.openGui(TES.instance, TESGuiId.TRADE_INTERACT.ordinal(), world, entity.getEntityId(), 0, 0);
			}
			event.setCanceled(true);
			return;
		}
		if (entity instanceof TESUnitTradeable && ((TESHireableBase) entity).canTradeWith(entityplayer)) {
			entityplayer.openGui(TES.instance, TESGuiId.UNIT_TRADE_INTERACT.ordinal(), world, entity.getEntityId(), 0, 0);
			event.setCanceled(true);
			return;
		}
		if (entity instanceof TESMercenary && ((TESHireableBase) entity).canTradeWith(entityplayer)) {
			if (((TESEntityNPC) entity).getHireableInfo().getHiringPlayerUUID() == null) {
				entityplayer.openGui(TES.instance, TESGuiId.MERCENARY_INTERACT.ordinal(), world, entity.getEntityId(), 0, 0);
				event.setCanceled(true);
				return;
			}
		}
		if (entity instanceof TESEntityNPC) {
			TESEntityNPC npc = (TESEntityNPC) entity;
			if (npc.getHireableInfo().getHiringPlayer() == entityplayer) {
				if (entity instanceof TESEntityProstitute) {
					entityplayer.openGui(TES.instance, TESGuiId.HIRED_INTERACT_NO_FUNC.ordinal(), world, entity.getEntityId(), 0, 0);
				} else {
					entityplayer.openGui(TES.instance, TESGuiId.HIRED_INTERACT.ordinal(), world, entity.getEntityId(), 0, 0);
				}
				event.setCanceled(true);
				return;
			}
			if (npc.getHireableInfo().isActive() && entityplayer.capabilities.isCreativeMode && itemstack != null && itemstack.getItem() == Items.clock) {
				if (!world.isRemote && MinecraftServer.getServer().getConfigurationManager().func_152596_g(entityplayer.getGameProfile())) {
					UUID hiringUUID = npc.getHireableInfo().getHiringPlayerUUID();
					if (hiringUUID != null) {
						String playerName = getUsernameWithoutWebservice(hiringUUID);
						if (playerName != null) {
							IChatComponent chatComponentText = new ChatComponentText("Hired unit belongs to " + playerName);
							chatComponentText.getChatStyle().setColor(EnumChatFormatting.GREEN);
							entityplayer.addChatMessage(chatComponentText);
						}
					}
				}
				event.setCanceled(true);
				return;
			}
		}
		if (!world.isRemote && entityplayer.capabilities.isCreativeMode && MinecraftServer.getServer().getConfigurationManager().func_152596_g(entityplayer.getGameProfile())) {
			if (itemstack != null && itemstack.getItem() == Items.clock && entity instanceof EntityLiving) {
				UUID brandingPlayer = TESItemBrandingIron.getBrandingPlayer(entity);
				if (brandingPlayer != null) {
					String playerName = getUsernameWithoutWebservice(brandingPlayer);
					if (playerName != null) {
						IChatComponent chatComponentText = new ChatComponentText("Entity was branded by " + playerName);
						chatComponentText.getChatStyle().setColor(EnumChatFormatting.GREEN);
						entityplayer.addChatMessage(chatComponentText);
						event.setCanceled(true);
						return;
					}
				}
			}
		}
		if (entity instanceof EntityVillager && !TESConfig.enableVillagerTrading) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.entity;
		World world = entity.worldObj;
		if (!world.isRemote && entity instanceof EntityXPOrb && !TESConfig.enchantingVanilla && world.provider instanceof TESWorldProvider) {
			event.setCanceled(true);
			return;
		}
		if (!world.isRemote && entity.getClass() == EntityFishHook.class && world.provider instanceof TESWorldProvider) {
			EntityFishHook oldFish = (EntityFishHook) entity;
			NBTTagCompound fishData = new NBTTagCompound();
			oldFish.writeToNBT(fishData);
			oldFish.setDead();
			TESEntityFishHook newFish = new TESEntityFishHook(world);
			newFish.readFromNBT(fishData);
			newFish.field_146042_b = oldFish.field_146042_b;
			if (newFish.field_146042_b != null) {
				newFish.field_146042_b.fishEntity = newFish;
				newFish.setPlayerID(newFish.field_146042_b.getEntityId());
			}
			world.spawnEntityInWorld(newFish);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onEntitySpawnAttempt(LivingSpawnEvent.CheckSpawn event) {
		EntityLivingBase entity = event.entityLiving;
		World world = entity.worldObj;
		if (!world.isRemote && entity instanceof EntityMob && TESBannerProtection.isProtected(world, entity, TESBannerProtection.anyBanner(), false)) {
			event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onExplosionDetonate(ExplosionEvent.Detonate event) {
		Explosion expl = event.explosion;
		World world = event.world;
		Entity exploder = expl.exploder;
		if (!world.isRemote && exploder != null) {
			TESBannerProtection.IFilter protectFilter = null;
			if (exploder instanceof TESEntityNPC || exploder instanceof EntityMob) {
				protectFilter = TESBannerProtection.anyBanner();
			} else if (exploder instanceof EntityThrowable) {
				protectFilter = TESBannerProtection.forThrown((EntityThrowable) exploder);
			} else if (exploder instanceof EntityTNTPrimed) {
				protectFilter = TESBannerProtection.forTNT((EntityTNTPrimed) exploder);
			} else if (exploder instanceof EntityMinecartTNT) {
				protectFilter = TESBannerProtection.forTNTMinecart();
			}
			if (protectFilter != null) {
				List<ChunkPosition> blockList = expl.affectedBlockPositions;
				Collection<ChunkPosition> removes = new ArrayList<>();
				for (ChunkPosition blockPos : blockList) {
					int i = blockPos.chunkPosX;
					int j = blockPos.chunkPosY;
					int k = blockPos.chunkPosZ;
					if (TESBannerProtection.isProtected(world, i, j, k, protectFilter, false)) {
						removes.add(blockPos);
					}
				}
				blockList.removeAll(removes);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onFillBucket(FillBucketEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = event.world;
		MovingObjectPosition target = event.target;
		if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			int i = target.blockX;
			int j = target.blockY;
			int k = target.blockZ;
			if (!world.isRemote && TESBannerProtection.isProtected(world, i, j, k, TESBannerProtection.forPlayer(entityplayer, TESBannerProtection.Permission.FULL), true)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onHarvestCheck(net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck event) {
		EntityPlayer entityplayer = event.entityPlayer;
		Block block = event.block;
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		if (itemstack != null && block instanceof BlockWeb && itemstack.getItem() instanceof ItemShears) {
			event.success = true;
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onItemPickup(EntityItemPickupEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		ItemStack itemstack = event.item.getEntityItem();
		if (!entityplayer.worldObj.isRemote) {
			if (itemstack.stackSize > 0) {
				for (int i = 0; i < entityplayer.inventory.getSizeInventory(); i++) {
					ItemStack itemInSlot = entityplayer.inventory.getStackInSlot(i);
					if (itemInSlot != null && itemInSlot.getItem() == TESItems.pouch) {
						TESItemPouch.tryAddItemToPouch(itemInSlot, itemstack, true);
						if (itemstack.stackSize <= 0) {
							break;
						}
					}
				}
				if (itemstack.stackSize <= 0) {
					event.setResult(Event.Result.ALLOW);
				}
			}
			if (itemstack.getItem() == Item.getItemFromBlock(TESBlocks.plantain)) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.findPlantain);
			}
			if (itemstack.getItem() == Item.getItemFromBlock(TESBlocks.clover) && itemstack.getItemDamage() == 1) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.findFourLeafClover);
			}
			if (TESConfig.enchantingAutoRemoveVanilla) {
				dechant(itemstack, entityplayer);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onItemUseFinish(PlayerUseItemEvent.Finish event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = entityplayer.worldObj;
		ItemStack itemstack = event.item;
		if (!world.isRemote && TESPoisonedDrinks.isDrinkPoisoned(itemstack)) {
			TESPoisonedDrinks.addPoisonEffect(entityplayer);
		}
	}

	@SubscribeEvent
	public void onItemUseStop(PlayerUseItemEvent.Stop event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = entityplayer.worldObj;
		ItemStack itemstack = event.item;
		int usingTick = event.duration;
		if (itemstack != null && itemstack.getItem() instanceof ItemBow && !(itemstack.getItem() instanceof TESItemBow) && !(itemstack.getItem() instanceof TESItemCrossbow)) {
			if (!world.isRemote) {
				if (proxyBowItemServer == null) {
					proxyBowItemServer = new TESItemBow(Item.ToolMaterial.WOOD);
				}
				proxyBowItemServer.onPlayerStoppedUsing(itemstack, world, entityplayer, usingTick);
				proxyBowItemServer = null;
				event.setCanceled(true);
				return;
			}
			if (proxyBowItemClient == null) {
				proxyBowItemClient = new TESItemBow(Item.ToolMaterial.WOOD);
			}
			proxyBowItemClient.onPlayerStoppedUsing(itemstack, world, entityplayer, usingTick);
			proxyBowItemClient = null;
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onLivingAttacked(LivingAttackEvent event) {
		EntityLivingBase entity = event.entityLiving;
		EntityLivingBase attacker;
		if (event.source.getEntity() instanceof EntityLivingBase) {
			attacker = (EntityLivingBase) event.source.getEntity();
		} else {
			attacker = null;
		}
		World world = entity.worldObj;
		if (entity instanceof TESNPCMount && entity.riddenByEntity != null && attacker == entity.riddenByEntity) {
			cancelAttackEvent(event);
		}
		if (attacker instanceof EntityPlayer && !TES.canPlayerAttackEntity((EntityPlayer) attacker, entity, true)) {
			cancelAttackEvent(event);
		}
		if (attacker instanceof EntityCreature && !TES.canNPCAttackEntity((EntityCreature) attacker, entity, false)) {
			cancelAttackEvent(event);
		}
		if (event.source instanceof EntityDamageSourceIndirect) {
			Entity projectile = event.source.getSourceOfDamage();
			if (projectile instanceof EntityArrow || projectile instanceof TESEntityCrossbowBolt || projectile instanceof TESEntityDart) {
				boolean wearingAllRoyce = true;
				for (int i = 0; i < 4; i++) {
					ItemStack armour = entity.getEquipmentInSlot(i + 1);
					if (armour == null || !(armour.getItem() instanceof ItemArmor) || ((ItemArmor) armour.getItem()).getArmorMaterial() != TESMaterial.ROYCE) {
						wearingAllRoyce = false;
						break;
					}
				}
				if (wearingAllRoyce) {
					if (!world.isRemote && entity instanceof EntityPlayer) {
						((EntityPlayer) entity).inventory.damageArmor(event.ammount);
					}
					cancelAttackEvent(event);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onLivingDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.entityLiving;
		World world = entity.worldObj;
		DamageSource source = event.source;
		if (!world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			int i = MathHelper.floor_double(entityplayer.posX);
			int j = MathHelper.floor_double(entityplayer.posY);
			int k = MathHelper.floor_double(entityplayer.posZ);
			TESLevelData.getData(entityplayer).setDeathPoint(i, j, k);
			TESLevelData.getData(entityplayer).setDeathDimension(entityplayer.dimension);
		}
		if (!world.isRemote) {
			EntityPlayer entityplayer = null;
			boolean creditHiredUnit = false;
			boolean byNearbyUnit = false;
			if (source.getEntity() instanceof EntityPlayer) {
				entityplayer = (EntityPlayer) source.getEntity();
			} else if (entity.func_94060_bK() instanceof EntityPlayer) {
				entityplayer = (EntityPlayer) entity.func_94060_bK();
			} else if (source.getEntity() instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) source.getEntity();
				if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer() != null) {
					entityplayer = npc.getHireableInfo().getHiringPlayer();
					creditHiredUnit = true;
					double nearbyDist = 64.0D;
					byNearbyUnit = npc.getDistanceSqToEntity(entityplayer) <= nearbyDist * nearbyDist;
				}
			}
			if (entityplayer != null) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				TESFaction entityFaction = TES.getNPCFaction(entity);
				float prevAlignment = playerData.getAlignment(entityFaction);
				List<TESFaction> forcedBonusFactions = null;
				if (entity instanceof TESEntityNPC) {
					forcedBonusFactions = ((TESEntityNPC) entity).getKillBonusFactions();
				}
				boolean wasSelfDefenceAgainstAlliedUnit = false;
				if (!creditHiredUnit && prevAlignment > 0.0F && entity instanceof TESEntityNPC) {
					TESEntityNPC npc = (TESEntityNPC) entity;
					if (npc.getHireableInfo().isActive() && npc.getHireableInfo().isWasAttackCommanded()) {
						wasSelfDefenceAgainstAlliedUnit = true;
					}
				}
				TESAlignmentValues.AlignmentBonus alignmentBonus = null;
				if (!wasSelfDefenceAgainstAlliedUnit && entity instanceof TESEntityNPC) {
					TESEntityNPC npc = (TESEntityNPC) entity;
					alignmentBonus = new TESAlignmentValues.AlignmentBonus(npc.getAlignmentBonus(), npc.getEntityClassName());
					alignmentBonus.setNeedsTranslation(true);
					alignmentBonus.setCivilianKill(npc.isCivilianNPC());
				}
				if (alignmentBonus != null && alignmentBonus.getBonus() != 0.0F && (!creditHiredUnit || byNearbyUnit)) {
					alignmentBonus.setKill(true);
					if (creditHiredUnit) {
						alignmentBonus.setKillByHiredUnit(true);
					}
					playerData.addAlignment(entityplayer, alignmentBonus, entityFaction, forcedBonusFactions, entity);
				}
				if (!creditHiredUnit) {
					if (entityFaction.isAllowPlayer()) {
						playerData.getFactionData(entityFaction).addNPCKill();
						List<TESFaction> killBonuses = entityFaction.getBonusesForKilling();
						for (TESFaction enemy : killBonuses) {
							playerData.getFactionData(enemy).addEnemyKill();
						}
						if (!entityplayer.capabilities.isCreativeMode) {
							boolean recordBountyKill = entityFaction.inDefinedControlZone(entityplayer, 50);
							if (recordBountyKill) {
								TESFactionBounties.forFaction(entityFaction).forPlayer(entityplayer).recordNewKill();
							}
						}
						TESFaction pledgeFac = playerData.getPledgeFaction();
						if (pledgeFac != null && (pledgeFac == entityFaction || pledgeFac.isAlly(entityFaction))) {
							playerData.onPledgeKill(entityplayer);
						}
					}
					float newAlignment = playerData.getAlignment(entityFaction);
					if (!wasSelfDefenceAgainstAlliedUnit && !entityplayer.capabilities.isCreativeMode && entityFaction != TESFaction.UNALIGNED) {
						int sentSpeeches = 0;
						int maxSpeeches = 5;
						double range = 8.0D;
						List<EntityLiving> nearbyAlliedNPCs = world.selectEntitiesWithinAABB(EntityLiving.class, entity.boundingBox.expand(range, range, range), new EntitySelectorImpl(entityFaction));
						for (EntityLiving npc : nearbyAlliedNPCs) {
							if (npc instanceof TESEntityNPC) {
								TESEntityNPC tesNPC = (TESEntityNPC) npc;
								if (tesNPC.getHireableInfo().isActive() && newAlignment > 0.0F || tesNPC.getHireableInfo().isActive() && tesNPC.getHireableInfo().getHiringPlayer() == entityplayer) {
									continue;
								}
							}
							if (npc.getAttackTarget() == null) {
								npc.setAttackTarget(entityplayer);
								if (npc instanceof TESEntityNPC && sentSpeeches < maxSpeeches) {
									TESEntityNPC tesnpc = (TESEntityNPC) npc;
									String speech = tesnpc.getSpeechBank(entityplayer);
									if (speech != null && tesnpc.getDistanceSqToEntity(entityplayer) < range) {
										tesnpc.sendSpeechBank(entityplayer, speech);
										sentSpeeches++;
									}
								}
							}
						}
					}
					if (!playerData.isSiegeActive()) {
						List<TESMiniQuest> miniquests = playerData.getMiniQuests();
						for (TESMiniQuest quest : miniquests) {
							quest.onKill(entityplayer, entity);
						}
						if (entity instanceof EntityPlayer) {
							EntityPlayer slainPlayer = (EntityPlayer) entity;
							List<TESMiniQuest> slainMiniquests = TESLevelData.getData(slainPlayer).getMiniQuests();
							for (TESMiniQuest quest : slainMiniquests) {
								quest.onKilledByPlayer(slainPlayer, entityplayer);
							}
						}
					}
				}
			}
		}
		if (!world.isRemote && source.getEntity() instanceof EntityPlayer && source.getSourceOfDamage() != null && source.getSourceOfDamage().getClass() == TESEntitySpear.class) {
			EntityPlayer entityplayer = (EntityPlayer) source.getEntity();
			if (entity != entityplayer && entityplayer.getDistanceSqToEntity(entity) >= 2500.0D) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.useSpearFromFar);
			}
		}
		if (!world.isRemote && entity instanceof TESEntityButterfly && source.getEntity() instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) source.getEntity();
			TESLevelData.getData(entityplayer).addAchievement(TESAchievement.killButterfly);
		}
		if (!world.isRemote) {
			EntityPlayer attackingPlayer = null;
			TESEntityNPC attackingHiredUnit = null;
			if (source.getEntity() instanceof EntityPlayer) {
				attackingPlayer = (EntityPlayer) source.getEntity();
			} else if (source.getEntity() instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) source.getEntity();
				if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer() != null) {
					attackingPlayer = npc.getHireableInfo().getHiringPlayer();
					attackingHiredUnit = npc;
				}
			}
			if (attackingPlayer != null) {
				boolean isFoe = TESLevelData.getData(attackingPlayer).getAlignment(TES.getNPCFaction(entity)) < 0.0F;
				if (isFoe && attackingHiredUnit == null) {
					if (attackingPlayer.isPotionActive(Potion.confusion.id)) {
						TESLevelData.getData(attackingPlayer).addAchievement(TESAchievement.killWhileDrunk);
					}
					if (source.getSourceOfDamage() instanceof TESEntityCrossbowBolt) {
						TESLevelData.getData(attackingPlayer).addAchievement(TESAchievement.useCrossbow);
					}
					if (source.getSourceOfDamage() instanceof TESEntityThrowingAxe) {
						TESLevelData.getData(attackingPlayer).addAchievement(TESAchievement.useThrowingAxe);
					}
				}
			}
		}
		if (!world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			if (TESEnchantmentHelper.hasMeleeOrRangedEnchant(source, TESEnchantment.HEADHUNTING)) {
				ItemStack playerHead = new ItemStack(Items.skull, 1, 3);
				GameProfile profile = entityplayer.getGameProfile();
				NBTTagCompound profileData = new NBTTagCompound();
				NBTUtil.func_152460_a(profileData, profile);
				playerHead.setTagInfo("SkullOwner", profileData);
				entityplayer.entityDropItem(playerHead, 0.0F);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onLivingDrops(LivingDropsEvent event) {
		EntityLivingBase entity = event.entityLiving;
		Random rand = entity.getRNG();
		int i = event.lootingLevel;
		if (entity instanceof EntitySheep && TESConfig.dropMutton) {
			int meat = rand.nextInt(3) + rand.nextInt(1 + i);
			for (int l = 0; l < meat; l++) {
				if (entity.isBurning()) {
					entity.dropItem(TESItems.muttonCooked, 1);
				} else {
					entity.dropItem(TESItems.muttonRaw, 1);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onLivingHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.entityLiving;
		EntityLivingBase attacker;
		if (event.source.getEntity() instanceof EntityLivingBase) {
			attacker = (EntityLivingBase) event.source.getEntity();
		} else {
			attacker = null;
		}
		World world = entity.worldObj;
		if (entity instanceof EntityPlayerMP && event.source == TESDamage.FROST) {
			TESDamage.doFrostDamage((EntityPlayerMP) entity);
		}
		if (!world.isRemote) {
			int preMaxHurtResTime = entity.maxHurtResistantTime;
			int maxHurtResTime = 20;
			if (attacker != null) {
				ItemStack weapon = attacker.getHeldItem();
				if (TESWeaponStats.isMeleeWeapon(weapon)) {
					maxHurtResTime = TESWeaponStats.getAttackTimeWithBase(weapon, 20);
				}
			}
			maxHurtResTime = Math.min(maxHurtResTime, 20);
			entity.maxHurtResistantTime = maxHurtResTime;
			if (entity.hurtResistantTime == preMaxHurtResTime) {
				entity.hurtResistantTime = maxHurtResTime;
			}
		}
		if (attacker != null && event.source.getSourceOfDamage() == attacker) {
			ItemStack weapon = attacker.getHeldItem();
			if (!world.isRemote && entity instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) entity;
				if (entityplayer.isUsingItem()) {
					ItemStack usingItem = entityplayer.getHeldItem();
					if (TESWeaponStats.isRangedWeapon(usingItem)) {
						entityplayer.clearItemInUse();
						IMessage packet = new TESPacketStopItemUse();
						TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
					}
				}
			}
			boolean wearingAllAsshai = true;
			for (int i = 0; i < 4; i++) {
				ItemStack armour = entity.getEquipmentInSlot(i + 1);
				if (armour == null || !(armour.getItem() instanceof ItemArmor) || ((ItemArmor) armour.getItem()).getArmorMaterial() != TESMaterial.ASSHAI) {
					wearingAllAsshai = false;
					break;
				}
			}
			if (wearingAllAsshai && !world.isRemote && weapon != null && weapon.isItemStackDamageable()) {
				int damage = weapon.getItemDamage();
				int maxDamage = weapon.getMaxDamage();
				float durability = 1.0F - (float) damage / maxDamage;
				durability *= 0.9F;
				int newDamage = Math.round((1.0F - durability) * maxDamage);
				newDamage = Math.min(newDamage, maxDamage);
				weapon.damageItem(newDamage - damage, attacker);
			}
			if (weapon != null) {
				Item.ToolMaterial material = null;
				if (weapon.getItem() instanceof ItemTool) {
					material = ((ItemTool) weapon.getItem()).func_150913_i();
				} else if (weapon.getItem() instanceof ItemSword) {
					material = TESMaterial.getToolMaterialByName(((ItemSword) weapon.getItem()).getToolMaterialName());
				}
				if (material != null && material == TESMaterial.ASSHAI_TOOL && !world.isRemote) {
					entity.addPotionEffect(new PotionEffect(Potion.wither.id, 160));
				}
			}
		}
		if (event.source.getSourceOfDamage() instanceof TESEntityArrowPoisoned && !world.isRemote) {
			TESItemSword.applyStandardPoison(entity);
		}
		if (event.source.getSourceOfDamage() instanceof TESEntityArrowFire && !world.isRemote) {
			TESItemSword.applyStandardFire(entity);
		}
		if (!world.isRemote) {
			if (TESEnchantmentHelper.hasMeleeOrRangedEnchant(event.source, TESEnchantment.FIRE)) {
				IMessage packet = new TESPacketWeaponFX(TESPacketWeaponFX.Type.INFERNAL, entity);
				TESPacketHandler.NETWORK_WRAPPER.sendToAllAround(packet, TESPacketHandler.nearEntity(entity, 64.0D));
			}
			if (TESEnchantmentHelper.hasMeleeOrRangedEnchant(event.source, TESEnchantment.CHILL)) {
				TESEnchantmentWeaponSpecial.doChillAttack(entity);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		World world = entity.worldObj;
		if (!world.isRemote) {
			TESEnchantmentHelper.onEntityUpdate(entity);
		}
		if (TESConfig.enchantingAutoRemoveVanilla && !world.isRemote && entity instanceof EntityPlayer && entity.ticksExisted % 60 == 0) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			for (int l = 0; l < entityplayer.inventory.getSizeInventory(); l++) {
				ItemStack itemstack = entityplayer.inventory.getStackInSlot(l);
				if (itemstack != null) {
					dechant(itemstack, entityplayer);
				}
			}
		}
		boolean inWater = entity.isInWater();
		if (!world.isRemote && TES.canSpawnMobs(world) && entity.isEntityAlive() && inWater && entity.ridingEntity == null) {
			boolean flag = !(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode;
			if (flag) {
				int i = MathHelper.floor_double(entity.posX);
				int k = MathHelper.floor_double(entity.posZ);
				int j = world.getTopSolidOrLiquidBlock(i, k);
				while (world.getBlock(i, j + 1, k).getMaterial().isLiquid() || world.getBlock(i, j + 1, k).getMaterial().isSolid()) {
					j++;
				}
				}
			}
		
		if (!world.isRemote && TES.canSpawnMobs(world) && entity.isEntityAlive() && world.isDaytime()) {
			float f = 0.0F;
			int bounders = 0;
			if (f > 0.0F) {
				f = Math.min(f, 2000.0F);
				int chance = (int) (2000000.0F / f);
				bounders = Math.min(bounders, 5);
				int i = MathHelper.floor_double(entity.posX);
				int k = MathHelper.floor_double(entity.posZ);
				world.getTopSolidOrLiquidBlock(i, k);
			}
		}
		if (!world.isRemote && entity.isEntityAlive() && inWater && entity.ridingEntity == null && entity.ticksExisted % 10 == 0) {
			boolean flag = entity instanceof EntityPlayer || entity instanceof TESEntityHumanBase;
			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
				flag = false;
			}

			if (flag) {
				int i = MathHelper.floor_double(entity.posX);
				int k = MathHelper.floor_double(entity.posZ);
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(world, i, k);
			}
		}
		if (!world.isRemote && entity.isEntityAlive() && entity.ticksExisted % 10 == 0) {
			boolean flag = entity instanceof EntityPlayer || entity instanceof TESEntityHumanBase;
			if (entity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entity;
				if (entityplayer.capabilities.isCreativeMode) {
					flag = false;
				}
			}

			if (flag) {
				int i = MathHelper.floor_double(entity.posX);
				int k = MathHelper.floor_double(entity.posZ);
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(world, i, k);
			}
		}
		if (!world.isRemote && entity.isEntityAlive()) {
			ItemStack weapon = entity.getHeldItem();
			boolean lanceOnFoot = weapon != null && weapon.getItem() instanceof TESItemLance && entity.ridingEntity == null && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode);
			IAttributeInstance speedAttribute = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			if (speedAttribute.getModifier(TESItemLance.LANCE_SPEED_BOOST_ID) != null) {
				speedAttribute.removeModifier(TESItemLance.LANCE_SPEED_BOOST);
			}
			if (lanceOnFoot) {
				speedAttribute.applyModifier(TESItemLance.LANCE_SPEED_BOOST);
			}
		}
		if (!world.isRemote && entity.isEntityAlive() && entity.ticksExisted % 20 == 0) {
			boolean simplifySyntax = entity instanceof TESEntityNPC && ((TESEntityNPC) entity).isLegendaryNPC() || entity instanceof TESBiome.ImmuneToFrost;
			boolean flag = !simplifySyntax;

			if (entity instanceof EntityPlayer) {
				flag = !((EntityPlayer) entity).capabilities.isCreativeMode;
			}

			if (flag) {
				int i = MathHelper.floor_double(entity.posX);
				int j = MathHelper.floor_double(entity.boundingBox.minY);
				int k = MathHelper.floor_double(entity.posZ);
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(world, i, k);
				boolean standardColdBiome = biome instanceof TESBiome && biome.temperature == 0.0f;
				boolean altitudeColdBiome = biome instanceof TESBiome && ((TESBiome) biome).getClimateType() != null && ((TESBiome) biome).getClimateType().isAltitudeZone() && k >= 140;
				boolean isOpenAir = world.canBlockSeeTheSky(i, j, k);
				boolean noLightSource = world.getSavedLightValue(EnumSkyBlock.Block, i, j, k) < 10;
				if ((standardColdBiome || altitudeColdBiome) && (isOpenAir || inWater) && noLightSource) {
					int frostChance = 100;
					int frostProtection = 1;
					for (int l = 0; l < 4; l++) {
						ItemStack armor = entity.getEquipmentInSlot(l + 1);
						if (armor != null && armor.getItem() instanceof ItemArmor) {
							ItemArmor.ArmorMaterial material = ((ItemArmor) armor.getItem()).getArmorMaterial();
							if (material == TESMaterial.FUR || material == TESMaterial.GIFT) {
								frostProtection += 2;
							}
						}
					}
					frostChance *= frostProtection;
					if (world.isRaining()) {
						frostChance /= 5;
					}
					if (inWater) {
						frostChance /= 10;
					}
					frostChance = Math.max(frostChance, 1);
					if (world.rand.nextInt(frostChance) < 10) {
						entity.attackEntityFrom(TESDamage.FROST, 1.0f);
					}
				}
			}
		}
		if (!world.isRemote && entity.isEntityAlive() && entity.ticksExisted % 20 == 0) {
			boolean simplifySyntax = entity instanceof TESEntityNPC && ((TESEntityNPC) entity).isLegendaryNPC() || entity instanceof TESBiome.ImmuneToHeat || entity.isImmuneToFire();
			boolean flag = !simplifySyntax;

			if (entity instanceof EntityPlayer) {
				flag = !((EntityPlayer) entity).capabilities.isCreativeMode;
			}

			if (flag) {
				int i = MathHelper.floor_double(entity.posX);
				int j = MathHelper.floor_double(entity.boundingBox.minY);
				int k = MathHelper.floor_double(entity.posZ);
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(world, i, k);
				boolean isOpenAir = world.canBlockSeeTheSky(i, j, k);
				if (biome instanceof TESBiome.Desert && !inWater && isOpenAir && world.isDaytime()) {
					int burnChance = 100;
					int burnProtection = 1;
					for (int l = 0; l < 4; l++) {
						ItemStack armour = entity.getEquipmentInSlot(l + 1);
						if (armour != null && armour.getItem() instanceof ItemArmor) {
							ItemArmor.ArmorMaterial material = ((ItemArmor) armour.getItem()).getArmorMaterial();
							if (material == TESMaterial.ROBES) {
								burnProtection += 2;
							}
						}
					}
					burnChance *= burnProtection;
					burnChance = Math.max(burnChance, 1);
					if (world.rand.nextInt(burnChance) < 10) {
						boolean attacked = entity.attackEntityFrom(DamageSource.onFire, 1.0F);
						if (attacked && entity instanceof EntityPlayerMP) {
							TESDamage.doBurnDamage((EntityPlayerMP) entity);
						}
					}
				}
			}
		}
		if (world.isRemote) {
			TESPlateFallingInfo.getOrCreateFor(entity, true).update();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onMinecartInteract(MinecartInteractEvent event) {
		EntityPlayer entityplayer = event.player;
		World world = entityplayer.worldObj;
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		EntityMinecart minecart = event.minecart;
		if (minecart instanceof EntityMinecartChest && itemstack != null && itemstack.getItem() instanceof TESItemPouch) {
			if (!world.isRemote) {
				int pouchSlot = entityplayer.inventory.currentItem;
				entityplayer.openGui(TES.instance, TESGuiHandler.packGuiIDWithSlot(TESGuiId.POUCH_MINECART.ordinal(), pouchSlot), world, minecart.getEntityId(), 0, 0);
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		EntityPlayer entityplayer = event.player;
		if (!entityplayer.worldObj.isRemote) {
			TESLevelData.sendAlignmentToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
			TESLevelData.sendAllAlignmentsInWorldToPlayer(entityplayer, entityplayer.worldObj);
			TESLevelData.sendShieldToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
			TESLevelData.sendAllShieldsInWorldToPlayer(entityplayer, entityplayer.worldObj);
			TESLevelData.sendCapeToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
			TESLevelData.sendAllCapesInWorldToPlayer(entityplayer, entityplayer.worldObj);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onPlayerInteract(PlayerInteractEvent evt) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER || evt.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		World world = evt.entity.worldObj;
		Block block = world.getBlock(evt.x, evt.y, evt.z);
		if (block != Blocks.dragon_egg) {
			return;
		}
		evt.useBlock = Event.Result.DENY;
		evt.useItem = Event.Result.DENY;
		world.setBlock(evt.x, evt.y, evt.z, Blocks.air);
		TESEntityDragon dragon = new TESEntityDragon(world);
		dragon.setPosition(evt.x + 0.5, evt.y + 0.5, evt.z + 0.5);
		dragon.getReproductionHelper().setBreederName(evt.entityPlayer.getCommandSenderName());
		dragon.getLifeStageHelper().setLifeStage(TESDragonLifeStage.EGG);
		world.spawnEntityInWorld(dragon);
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer entityplayer = event.player;
		World world = entityplayer.worldObj;
		if (!world.isRemote) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) entityplayer;
			if (world.provider.terrainType instanceof TESWorldType && entityplayermp.dimension == 0 && !TESLevelData.getData(entityplayermp).getTeleportedKW()) {
				int dimension = TESDimension.GAME_OF_THRONES.getDimensionID();
				TESTeleporter teleporter = new TESTeleporter(DimensionManager.getWorld(dimension), false);
				MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(entityplayermp, dimension, teleporter);
				TESLevelData.getData(entityplayermp).setTeleportedKW(true);
			}
			TESLevelData.sendLoginPacket(entityplayermp);
			TESLevelData.sendPlayerData(entityplayermp);
			TESLevelData.sendAlignmentToAllPlayersInWorld(entityplayer, world);
			TESLevelData.sendAllAlignmentsInWorldToPlayer(entityplayer, world);
			TESLevelData.sendShieldToAllPlayersInWorld(entityplayermp, world);
			TESLevelData.sendAllShieldsInWorldToPlayer(entityplayermp, world);
			TESLevelData.sendCapeToAllPlayersInWorld(entityplayermp, world);
			TESLevelData.sendAllCapesInWorldToPlayer(entityplayermp, world);
			TESDate.sendUpdatePacket(entityplayermp, false);
			TESFactionRelations.sendAllRelationsTo(entityplayermp);
			TESPlayerData pd = TESLevelData.getData(entityplayermp);
			pd.updateFastTravelClockFromLastOnlineTime(entityplayermp);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		EntityPlayer entityplayer = event.player;
		World world = entityplayer.worldObj;
		if (!world.isRemote && entityplayer instanceof EntityPlayerMP && world instanceof WorldServer) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) entityplayer;
			WorldServer worldserver = (WorldServer) world;
			ChunkCoordinates deathPoint = TESLevelData.getData(entityplayermp).getDeathPoint();
			int deathDimension = TESLevelData.getData(entityplayermp).getDeathDimension();
			if (deathDimension == TESDimension.GAME_OF_THRONES.getDimensionID() && TESConfig.knownWorldRespawning) {
				ChunkCoordinates bedLocation = entityplayermp.getBedLocation(entityplayermp.dimension);
				boolean hasBed = bedLocation != null;
				ChunkCoordinates spawnLocation;
				double respawnThreshold;
				if (hasBed) {
					EntityPlayer.verifyRespawnCoordinates(worldserver, bedLocation, entityplayermp.isSpawnForced(entityplayermp.dimension));
					spawnLocation = bedLocation;
					respawnThreshold = TESConfig.kwrBedRespawnThreshold;
				} else {
					spawnLocation = worldserver.getSpawnPoint();
					respawnThreshold = TESConfig.kwrWorldRespawnThreshold;
				}
				if (deathPoint != null) {
					boolean flag = deathPoint.getDistanceSquaredToChunkCoordinates(spawnLocation) > respawnThreshold * respawnThreshold;
					if (flag) {
						double randomDistance = MathHelper.getRandomIntegerInRange(worldserver.rand, TESConfig.kwrMinRespawn, TESConfig.kwrMaxRespawn);
						float angle = worldserver.rand.nextFloat() * 3.1415927F * 2.0F;
						int i = deathPoint.posX + (int) (randomDistance * MathHelper.sin(angle));
						int k = deathPoint.posZ + (int) (randomDistance * MathHelper.cos(angle));
						int j = TES.getTrueTopBlock(worldserver, i, k);
						entityplayermp.setLocationAndAngles(i + 0.5D, j, k + 0.5D, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
						entityplayermp.playerNetServerHandler.setPlayerLocation(i + 0.5D, j, k + 0.5D, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onSaplingGrow(SaplingGrowTreeEvent event) {
		World world = event.world;
		int i = event.x;
		int j = event.y;
		int k = event.z;
		Block block = world.getBlock(i, j, k);
		if (block == Blocks.sapling) {
			TESVanillaSaplings.growTree(world, i, j, k, event.rand);
			event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onServerChat(ServerChatEvent event) {
		EntityPlayerMP entityplayer = event.player;
		String message = event.message;
		String username = event.username;
		ChatComponentTranslation chatComponent = event.component;
		if (TESConfig.drunkMessages) {
			PotionEffect nausea = entityplayer.getActivePotionEffect(Potion.confusion);
			if (nausea != null) {
				int duration = nausea.getDuration();
				float chance = duration / 4800.0F;
				chance = Math.min(chance, 1.0F);
				chance *= 0.4F;
				String key = chatComponent.getKey();
				Object[] formatArgs = chatComponent.getFormatArgs();
				for (int a = 0; a < formatArgs.length; a++) {
					Object arg = formatArgs[a];
					String chatText = null;
					if (arg instanceof ChatComponentText) {
						IChatComponent componentText = (IChatComponent) arg;
						chatText = componentText.getUnformattedText();
					} else if (arg instanceof String) {
						chatText = (String) arg;
					}
					if (chatText != null && chatText.equals(message)) {
						String newText = TESDrunkenSpeech.getDrunkenSpeech(chatText, chance);
						if (arg instanceof String) {
							formatArgs[a] = newText;
						} else {
							formatArgs[a] = new ChatComponentText(newText);
						}
					}
				}
				chatComponent = new ChatComponentTranslation(key, formatArgs);
			}
		}
		if (TESConfig.enableTitles) {
			TESTitle.PlayerTitle playerTitle = TESLevelData.getData(entityplayer).getPlayerTitle();
			if (playerTitle != null) {
				Collection<Object> newFormatArgs = new ArrayList<>();
				for (Object arg : chatComponent.getFormatArgs()) {
					if (arg instanceof ChatComponentText) {
						IChatComponent componentText = (IChatComponent) arg;
						if (componentText.getUnformattedText().contains(username)) {
							IChatComponent titleComponent = playerTitle.getFullTitleComponent(entityplayer);
							IChatComponent fullUsernameComponent = new ChatComponentText("").appendSibling(titleComponent).appendSibling(componentText);
							newFormatArgs.add(fullUsernameComponent);
						} else {
							newFormatArgs.add(componentText);
						}
					} else {
						newFormatArgs.add(arg);
					}
				}
				ChatComponentTranslation newChatComponent = new ChatComponentTranslation(chatComponent.getKey(), newFormatArgs.toArray());
				newChatComponent.setChatStyle(chatComponent.getChatStyle().createShallowCopy());
				for (Object sibling : chatComponent.getSiblings()) {
					newChatComponent.appendSibling((IChatComponent) sibling);
				}
				chatComponent = newChatComponent;
			}
		}
		event.component = chatComponent;
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onSmelting(PlayerEvent.ItemSmeltedEvent event) {
		EntityPlayer entityplayer = event.player;
		ItemStack itemstack = event.smelting;
		if (!entityplayer.worldObj.isRemote) {
			if (itemstack.getItem() == TESItems.bronzeIngot) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.craftBronze);
			}
			if (itemstack.getItem() == TESItems.copperIngot) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.craftCopper);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onStartTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
		if (event.target instanceof TESEntityCart) {
			TESEntityCart target = (TESEntityCart) event.target;
			if (target.getPulling() != null) {
				TESPacketHandler.NETWORK_WRAPPER.sendTo(new TESPacketCargocartUpdate(target.getPulling().getEntityId(), target.getEntityId()), (EntityPlayerMP) event.entityPlayer);
			}
		}
		if (event.target instanceof TESEntityCargocart) {
			TESEntityCargocart target = (TESEntityCargocart) event.target;
			TESPacketHandler.NETWORK_WRAPPER.sendTo(new TESPacketCargocart(target.getLoad(), target.getEntityId()), (EntityPlayerMP) event.entityPlayer);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onStartTrackingEntity(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
		Entity entity = event.target;
		EntityPlayer entityplayer = event.entityPlayer;
		if (!entity.worldObj.isRemote && entity instanceof TESEntityNPC) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) entityplayer;
			TESEntityNPC npc = (TESEntityNPC) entity;
			npc.onPlayerStartTracking(entityplayermp);
		}
		if (!entity.worldObj.isRemote && entity instanceof TESRandomSkinEntity) {
			IMessage packet = new TESPacketEntityUUID(entity.getEntityId(), entity.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
		if (!entity.worldObj.isRemote && entity instanceof TESEntityBanner) {
			((TESEntityBanner) entity).sendBannerToPlayer(entityplayer, false, false);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onUseBonemeal(BonemealEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		World world = event.world;
		Random rand = world.rand;
		int i = event.x;
		int j = event.y;
		int k = event.z;
		if (!world.isRemote) {
			if (event.block instanceof TESBlockSaplingBase) {
				TESBlockSaplingBase sapling = (TESBlockSaplingBase) event.block;
				int meta = world.getBlockMetadata(i, j, k);
				if (rand.nextFloat() < 0.45D) {
					sapling.incrementGrowth(world, i, j, k, rand);
				}
				if (sapling == TESBlocks.sapling4 && (meta & 0x7) == 1 && world.getBlock(i, j, k) == TESBlocks.wood4 && world.getBlockMetadata(i, j, k) == 1) {
					TESLevelData.getData(entityplayer).addAchievement(TESAchievement.growBaobab);
				}
				event.setResult(Event.Result.ALLOW);
				return;
			}
			if (event.block.canSustainPlant(world, i, j, k, ForgeDirection.UP, Blocks.tallgrass) && event.block instanceof IGrowable) {
				BiomeGenBase biomegenbase = TESCrashHandler.getBiomeGenForCoords(world, i, k);
				if (biomegenbase instanceof TESBiome) {
					TESBiome biome = (TESBiome) biomegenbase;
					int attempts = 0;
					label46:
					while (attempts < 128) {
						int i1 = i;
						int j1 = j + 1;
						int k1 = k;
						int subAttempts = 0;
						while (subAttempts < attempts / 16) {
							i1 += rand.nextInt(3) - 1;
							j1 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
							k1 += rand.nextInt(3) - 1;
							Block below = world.getBlock(i1, j1 - 1, k1);
							if (below instanceof IGrowable && below.canSustainPlant(world, i1, j1 - 1, k1, ForgeDirection.UP, Blocks.tallgrass) && !world.getBlock(i1, j1, k1).isNormalCube()) {
								subAttempts++;
								continue;
							}
							continue label46;
						}
						if (world.getBlock(i1, j1, k1).getMaterial() == Material.air) {
							if (rand.nextInt(8) > 0) {
								TESBiome.GrassBlockAndMeta obj = biome.getRandomGrass(rand);
								Block block = obj.getBlock();
								int meta = obj.getMeta();
								if (block.canBlockStay(world, i1, j1, k1)) {
									world.setBlock(i1, j1, k1, block, meta, 3);
								}
							} else {
								biome.plantFlower(world, rand, i1, j1, k1);
							}
						}
						attempts++;
					}
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onUseHoe(UseHoeEvent event) {
		World world = event.world;
		int i = event.x;
		int j = event.y;
		int k = event.z;
		Block block = world.getBlock(i, j, k);
		TESBlockGrapevine.setHoeing(true);
		if (world.getBlock(i, j + 1, k).isAir(world, i, j + 1, k) && (block == TESBlocks.mudGrass || block == TESBlocks.mud)) {
			Block tilled = TESBlocks.mudFarmland;
			world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, tilled.stepSound.getStepResourcePath(), (tilled.stepSound.getVolume() + 1.0F) / 2.0F, tilled.stepSound.getPitch() * 0.8F);
			if (!world.isRemote) {
				world.setBlock(i, j, k, tilled);
			}
			event.setResult(Event.Result.ALLOW);
			return;
		}
		TESBlockGrapevine.setHoeing(true);
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onWorldSave(WorldEvent.Save event) {
		World world = event.world;
		if (!world.isRemote && world.provider.dimensionId == 0) {
			TESTime.save();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onWorldUnload(WorldEvent.Unload event) {
		World world = event.world;
		if (world.provider instanceof TESWorldProvider) {
			TESBiomeVariantStorage.clearAllVariants(world);
		}
	}

	private static class EntitySelectorImpl implements IEntitySelector {
		private final TESFaction entityFaction;

		private EntitySelectorImpl(TESFaction entityFaction) {
			this.entityFaction = entityFaction;
		}

		@Override
		public boolean isEntityApplicable(Entity entitySelect) {
			if (entitySelect.isEntityAlive()) {
				TESFaction fac = TES.getNPCFaction(entitySelect);
				return fac.isGoodRelation(entityFaction);
			}
			return false;
		}
	}
}