package tes.common.entity.animal;

import tes.common.database.TESItems;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import java.util.UUID;

public class TESEntityWhiteOryx extends TESEntityGemsbok implements TESRandomSkinEntity {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityWhiteOryx(World world) {
		super(world);
		setSize(width * 0.9f, height * 0.9f);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entity) {
		return new TESEntityWhiteOryx(worldObj);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		int hide = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < hide; ++l) {
			dropItem(Items.leather, 1);
		}
		int meat = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < meat; ++l) {
			if (isBurning()) {
				dropItem(TESItems.deerCooked, 1);
				continue;
			}
			dropItem(TESItems.deerRaw, 1);
		}
	}

	@Override
	public float getGemsbokSoundPitch() {
		return 0.9f;
	}

	@Override
	public void setUniqueID(UUID uuid) {
		entityUniqueID = uuid;
	}
}