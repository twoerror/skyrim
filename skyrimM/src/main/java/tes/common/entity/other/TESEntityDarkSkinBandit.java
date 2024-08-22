package tes.common.entity.other;

import tes.common.database.TESItems;
import tes.common.database.TESNames;
import tes.common.item.other.TESItemRobes;
import tes.common.item.other.TESItemTurban;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESEntityDarkSkinBandit extends TESEntityBanditBase {
	private static final ItemStack[] WEAPONS = {new ItemStack(TESItems.bronzeDagger), new ItemStack(TESItems.ironDagger), new ItemStack(TESItems.bronzeDaggerPoisoned)};
	private static final int[] ROBE_COLORS = {3354412, 5984843, 5968655, 3619908, 9007463, 3228720};

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityDarkSkinBandit(World world) {
		super(world);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		IEntityLivingData entityData = super.onSpawnWithEgg(data);
		int i = rand.nextInt(WEAPONS.length);
		npcItemsInv.setMeleeWeapon(WEAPONS[i].copy());
		npcItemsInv.setIdleItem(npcItemsInv.getMeleeWeapon());
		setCurrentItemOrArmor(4, null);
		if (rand.nextInt(4) == 0) {
			ItemStack turban = new ItemStack(TESItems.bronzeHelmet);
			int robeColor = ROBE_COLORS[rand.nextInt(ROBE_COLORS.length)];
			TESItemRobes.setRobesColor(turban, robeColor);
			if (rand.nextInt(3) == 0) {
				TESItemTurban.setHasOrnament(turban, true);
			}
			setCurrentItemOrArmor(4, turban);
		}
		return entityData;
	}

	@Override
	public void setupNPCName() {
		int i = rand.nextInt(6);
		switch (i) {
			case 0:
				familyInfo.setName(TESNames.getGhiscarName(rand, true));
				break;
			case 1:
				familyInfo.setName(TESNames.getLhazarName(rand, true));
				break;
			case 2:
				familyInfo.setName(TESNames.getJogosName(rand, true));
				break;
			case 3:
				familyInfo.setName(TESNames.getDothrakiName(rand, true));
				break;
			case 4:
				familyInfo.setName(TESNames.getSothoryosName(rand, true));
				break;
			case 5:
				familyInfo.setName(TESNames.getWildName(rand, true));
		}
	}
}