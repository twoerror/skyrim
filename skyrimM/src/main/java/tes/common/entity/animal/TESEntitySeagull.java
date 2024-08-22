package tes.common.entity.animal;

import tes.common.entity.other.utils.TESAmbientSpawnChecks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class TESEntitySeagull extends TESEntityBird {
	public static final float SEAGULL_SCALE = 1.4f;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntitySeagull(World world) {
		super(world);
		setSize(width * SEAGULL_SCALE, height * SEAGULL_SCALE);
	}

	@Override
	public boolean canBirdSpawnHere() {
		if (TESAmbientSpawnChecks.canSpawn(this, 8, 4, 40, 4, Material.leaves, Material.sand)) {
			double range = 16.0;
			List<TESEntitySeagull> nearbyGulls = worldObj.getEntitiesWithinAABB(TESEntitySeagull.class, boundingBox.expand(range, range, range));
			return nearbyGulls.size() < 2;
		}
		return false;
	}

	@Override
	public boolean canStealItems() {
		return true;
	}

	@Override
	public String getBirdTextureDir() {
		return "seagull";
	}

	@Override
	public String getDeathSound() {
		return "tes:bird.seagull.hurt";
	}

	@Override
	public String getHurtSound() {
		return "tes:bird.seagull.hurt";
	}

	@Override
	public String getLivingSound() {
		return "tes:bird.seagull.say";
	}

	@Override
	public boolean isStealable(ItemStack itemstack) {
		Item item = itemstack.getItem();
		return item == Items.fish || item == Items.cooked_fished || super.isStealable(itemstack);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		IEntityLivingData entityData = super.onSpawnWithEgg(data);
		setBirdType(TESEntityBird.BirdType.COMMON);
		return entityData;
	}
}