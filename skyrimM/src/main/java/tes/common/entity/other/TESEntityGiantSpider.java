package tes.common.entity.other;

import tes.common.database.TESAchievement;
import tes.common.faction.TESFaction;
import tes.common.world.biome.TESBiome;
import net.minecraft.world.World;

public class TESEntityGiantSpider extends TESEntitySpiderBase implements TESBiome.ImmuneToHeat, TESBiome.ImmuneToFrost {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityGiantSpider(World world) {
		super(world);
		spawnsInDarkness = true;
	}

	@Override
	public float getAlignmentBonus() {
		return 2.0f;
	}

	@Override
	public TESFaction getFaction() {
		return TESFaction.EMPIRE;
	}

	@Override
	public TESAchievement getKillAchievement() {
		return TESAchievement.killUlthos;
	}

	@Override
	public int getRandomSpiderScale() {
		return rand.nextInt(3);
	}

	@Override
	public int getRandomSpiderType() {
		return rand.nextBoolean() ? 0 : 1 + rand.nextInt(2);
	}
}