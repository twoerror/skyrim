package tes.common.item.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.client.render.other.TESRenderBow;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESItems;
import tes.common.enchant.TESEnchantment;
import tes.common.enchant.TESEnchantmentHelper;
import tes.common.entity.other.inanimate.TESEntityArrowFire;
import tes.common.entity.other.inanimate.TESEntityArrowPoisoned;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import java.util.Arrays;

public class TESItemBow extends ItemBow {
	private final Item.ToolMaterial bowMaterial;
	private final double arrowDamageFactor;

	private int bowPullTime;

	@SideOnly(Side.CLIENT)
	private IIcon[] bowPullIcons;

	public TESItemBow(Item.ToolMaterial material) {
		this(material, 1.0);
	}

	public TESItemBow(Item.ToolMaterial material, double d) {
		bowMaterial = material;
		setMaxDamage((int) (material.getMaxUses() * 1.5f));
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		arrowDamageFactor = d;
		bowPullTime = 20;
	}

	public static void applyBowModifiers(EntityArrow arrow, ItemStack itemstack) {
		int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemstack);
		if (power > 0) {
			arrow.setDamage(arrow.getDamage() + power * 0.5 + 0.5);
		}
		int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemstack);
		punch += TESEnchantmentHelper.calcRangedKnockback(itemstack);
		if (punch > 0) {
			arrow.setKnockbackStrength(punch);
		}
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) + TESEnchantmentHelper.calcFireAspect(itemstack) > 0) {
			arrow.setFire(100);
		}
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (!ench.getApplyToProjectile() || !TESEnchantmentHelper.hasEnchant(itemstack, ench)) {
				continue;
			}
			TESEnchantmentHelper.setProjectileEnchantment(arrow, ench);
		}
	}

	public static float getLaunchSpeedFactor(ItemStack itemstack) {
		float f = 1.0f;
		if (itemstack != null) {
			if (itemstack.getItem() instanceof TESItemBow) {
				f = (float) (f * ((TESItemBow) itemstack.getItem()).arrowDamageFactor);
			}
			f *= TESEnchantmentHelper.calcRangedDamageFactor(itemstack);
		}
		return f;
	}

	private static int getInvArrowSlot(EntityPlayer entityplayer) {
		for (int slot = 0; slot < entityplayer.inventory.mainInventory.length; ++slot) {
			ItemStack invItem = entityplayer.inventory.mainInventory[slot];
			if (invItem == null || invItem.getItem() != Items.arrow && invItem.getItem() != TESItems.arrowPoisoned && invItem.getItem() != TESItems.arrowFire) {
				continue;
			}
			return slot;
		}
		return -1;
	}

	private static boolean shouldConsumeArrow(ItemStack itemstack, EntityPlayer entityplayer) {
		return !entityplayer.capabilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) == 0;
	}

	public BowState getBowState(EntityLivingBase entity, ItemStack usingItem, int useRemaining) {
		if (entity instanceof EntityPlayer && usingItem != null && usingItem.getItem() == this) {
			int ticksInUse = usingItem.getMaxItemUseDuration() - useRemaining;
			double useAmount = (double) ticksInUse / bowPullTime;
			if (useAmount >= 0.9) {
				return BowState.PULL_2;
			}
			if (useAmount > 0.65) {
				return BowState.PULL_1;
			}
			if (useAmount > 0.0) {
				return BowState.PULL_0;
			}
		}
		return BowState.HELD;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int renderPass, EntityPlayer entityplayer, ItemStack usingItem, int useRemaining) {
		BowState bowState = getBowState(entityplayer, usingItem, useRemaining);
		if (bowState == BowState.PULL_0) {
			return bowPullIcons[0];
		}
		if (bowState == BowState.PULL_1) {
			return bowPullIcons[1];
		}
		if (bowState == BowState.PULL_2) {
			return bowPullIcons[2];
		}
		return itemIcon;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack, ItemStack repairItem) {
		return repairItem.getItem() == Items.string || super.getIsRepairable(itemstack, repairItem);
	}

	@Override
	public int getItemEnchantability() {
		return 1 + bowMaterial.getEnchantability() / 5;
	}

	public int getMaxDrawTime() {
		return bowPullTime;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		ArrowNockEvent event = new ArrowNockEvent(entityplayer, itemstack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return event.result;
		}
		if (!shouldConsumeArrow(itemstack, entityplayer) || getInvArrowSlot(entityplayer) >= 0) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		int useTick = getMaxItemUseDuration(itemstack) - i;
		ArrowLooseEvent event = new ArrowLooseEvent(entityplayer, itemstack, useTick);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		useTick = event.charge;
		ItemStack arrowItem = null;
		int arrowSlot = getInvArrowSlot(entityplayer);
		if (arrowSlot >= 0) {
			arrowItem = entityplayer.inventory.mainInventory[arrowSlot];
		}
		boolean shouldConsume = shouldConsumeArrow(itemstack, entityplayer);
		if (arrowItem == null && !shouldConsume) {
			arrowItem = new ItemStack(Items.arrow);
		}
		if (arrowItem != null) {
			float charge = (float) useTick / bowPullTime;
			if (charge < 0.65f) {
				return;
			}
			charge = charge * (charge + 2.0f) / 3.0f;
			charge = Math.min(charge, 1.0f);
			EntityArrow arrow = arrowItem.getItem() == TESItems.arrowFire ? new TESEntityArrowFire(world, entityplayer, charge * 2.0f * getLaunchSpeedFactor(itemstack)) : arrowItem.getItem() == TESItems.arrowPoisoned ? new TESEntityArrowPoisoned(world, entityplayer, charge * 2.0f * getLaunchSpeedFactor(itemstack)) : new EntityArrow(world, entityplayer, charge * 2.0f * getLaunchSpeedFactor(itemstack));
			if (arrow.getDamage() < 1.0) {
				arrow.setDamage(1.0);
			}
			if (charge >= 1.0f) {
				arrow.setIsCritical(true);
			}
			applyBowModifiers(arrow, itemstack);
			itemstack.damageItem(1, entityplayer);
			world.playSoundAtEntity(entityplayer, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + charge * 0.5f);
			if (shouldConsume) {
				--arrowItem.stackSize;
				if (arrowItem.stackSize <= 0) {
					entityplayer.inventory.mainInventory[arrowSlot] = null;
				}
			} else {
				arrow.canBePickedUp = 2;
			}
			if (!world.isRemote) {
				world.spawnEntityInWorld(arrow);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		itemIcon = iconregister.registerIcon(getIconString());
		bowPullIcons = new IIcon[3];
		IItemRenderer bowRenderer = MinecraftForgeClient.getItemRenderer(new ItemStack(this), IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
		if (bowRenderer instanceof TESRenderBow && ((TESRenderBow) bowRenderer).isLargeBow()) {
			Arrays.fill(bowPullIcons, itemIcon);
		} else {
			bowPullIcons[0] = iconregister.registerIcon(getIconString() + '_' + BowState.PULL_0.getIconName());
			bowPullIcons[1] = iconregister.registerIcon(getIconString() + '_' + BowState.PULL_1.getIconName());
			bowPullIcons[2] = iconregister.registerIcon(getIconString() + '_' + BowState.PULL_2.getIconName());
		}
	}

	public TESItemBow setDrawTime(int i) {
		bowPullTime = i;
		return this;
	}

	public double getArrowDamageFactor() {
		return arrowDamageFactor;
	}

	public enum BowState {
		HELD(""), PULL_0("pull_0"), PULL_1("pull_1"), PULL_2("pull_2");

		private final String iconName;

		BowState(String s) {
			iconName = s;
		}

		public String getIconName() {
			return iconName;
		}
	}
}