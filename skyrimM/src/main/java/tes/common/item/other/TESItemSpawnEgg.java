package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseSpawnEgg;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class TESItemSpawnEgg extends Item {
	public TESItemSpawnEgg() {
		setHasSubtypes(true);
		setCreativeTab(TESCreativeTabs.TAB_SPAWN);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseSpawnEgg());
	}

	public static Entity spawnCreature(World world, int id, double d, double d1, double d2) {
		if (!TESEntityRegistry.SPAWN_EGGS.containsKey(id)) {
			return null;
		}
		String entityName = TESEntityRegistry.getStringFromID(id);
		Entity entity = EntityList.createEntityByName(entityName, world);
		if (entity instanceof EntityLiving) {
			EntityLiving entityliving = (EntityLiving) entity;
			entityliving.setLocationAndAngles(d, d1, d2, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
			entityliving.rotationYawHead = entityliving.rotationYaw;
			entityliving.renderYawOffset = entityliving.rotationYaw;
			entityliving.onSpawnWithEgg(null);
			world.spawnEntityInWorld(entityliving);
			entityliving.playLivingSound();
		}
		return entity;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int i) {
		TESEntityRegistry.SpawnEggInfo info = TESEntityRegistry.SPAWN_EGGS.get(itemstack.getItemDamage());
		return info != null ? i == 0 ? info.getPrimaryColor() : info.getSecondaryColor() : 16777215;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		return Items.spawn_egg.getIconFromDamageForRenderPass(i, j);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		StringBuilder itemName = new StringBuilder(StatCollector.translateToLocal(getUnlocalizedName() + ".name").trim());
		String entityName = TESEntityRegistry.getStringFromID(itemstack.getItemDamage());
		if (entityName != null) {
			itemName.append(' ').append(StatCollector.translateToLocal("entity." + entityName + ".name"));
		}
		return itemName.toString();
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (TESEntityRegistry.SpawnEggInfo info : TESEntityRegistry.SPAWN_EGGS.values()) {
			list.add(new ItemStack(item, 1, info.getSpawnedID()));
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
		int i1 = i;
		int j1 = j;
		int k1 = k;
		if (world.isRemote) {
			return true;
		}
		Block block = world.getBlock(i1, j1, k1);
		i1 += Facing.offsetsXForSide[l];
		j1 += Facing.offsetsYForSide[l];
		k1 += Facing.offsetsZForSide[l];
		double d = 0.0;
		if (l == 1 && block != null && block.getRenderType() == 11) {
			d = 0.5;
		}
		Entity entity = spawnCreature(world, itemstack.getItemDamage(), i1 + 0.5, j1 + d, k1 + 0.5);
		if (entity != null) {
			if (entity instanceof EntityLiving && itemstack.hasDisplayName()) {
				((EntityLiving) entity).setCustomNameTag(itemstack.getDisplayName());
			}
			if (entity instanceof TESEntityNPC) {
				((TESEntityNPC) entity).setNPCPersistent(true);
				((TESEntityNPC) entity).onArtificalSpawn();
			}
			if (!entityplayer.capabilities.isCreativeMode) {
				--itemstack.stackSize;
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}