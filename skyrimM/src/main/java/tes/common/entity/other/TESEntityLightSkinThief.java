package tes.common.entity.other;

import tes.common.database.TESItems;
import tes.common.database.TESNames;
import tes.common.item.other.TESItemLeatherHat;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESEntityLightSkinThief extends TESEntityThiefBase {
	private static final ItemStack[] WEAPONS = {new ItemStack(TESItems.ironDagger), new ItemStack(TESItems.bronzeDagger), new ItemStack(Items.iron_axe), new ItemStack(TESItems.bronzeAxe), new ItemStack(Items.stone_axe)};

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityLightSkinThief(World world) {
		super(world);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		IEntityLivingData entityData = super.onSpawnWithEgg(data);
		int i = rand.nextInt(WEAPONS.length);
		npcItemsInv.setMeleeWeapon(WEAPONS[i].copy());
		npcItemsInv.setIdleItem(npcItemsInv.getMeleeWeapon());
		if (rand.nextInt(3) == 0) {
			ItemStack hat = new ItemStack(TESItems.leatherHat);
			TESItemLeatherHat.setHatColor(hat, 0);
			TESItemLeatherHat.setFeatherColor(hat, 16777215);
			setCurrentItemOrArmor(4, hat);
		}
		return entityData;
	}

	@Override
	public void setupNPCName() {
		int i = rand.nextInt(4);
		switch (i) {
			case 0:
				familyInfo.setName(TESNames.getWesterosName(rand, true));
				break;
			case 1:
				familyInfo.setName(TESNames.getEssosName(rand, true));
				break;
			case 2:
				familyInfo.setName(TESNames.getQarthName(rand, true));
				break;
			case 3:
				familyInfo.setName(TESNames.getWildName(rand, true));
		}
	}
}