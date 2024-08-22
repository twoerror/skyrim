package tes.common.entity;

import tes.common.entity.dragon.TESEntityDragon;
import tes.common.entity.animal.*;
import tes.common.entity.other.*;
import tes.common.entity.other.inanimate.*;
import tes.common.faction.TESFaction;
import tes.common.entity.other.TESEntityHummel009;
import tes.common.entity.other.inanimate.TESEntityPortal;

public class TESEntity {
	private TESEntity() {
	}

	@SuppressWarnings({"UnusedAssignment", "ValueOfIncrementOrDecrementUsed"})
	public static void preInit() {
		int id = 0;
		
		TESEntityRegistry.register(TESEntityBison.class, id++, 7488812);
		TESEntityRegistry.register(TESEntityBear.class, id++, 7492416);
		TESEntityRegistry.register(TESEntityBird.class, id++, 7451872);
		TESEntityRegistry.register(TESEntityBeaver.class, id++, 7492416);
		TESEntityRegistry.register(TESEntityButterfly.class, id++, 16119285);
		TESEntityRegistry.register(TESEntityCamel.class, id++, 13150061);
		TESEntityRegistry.register(TESEntityCrocodile.class, id++, 2896659);
		TESEntityRegistry.register(TESEntityDeer.class, id++, 5978669);
		TESEntityRegistry.register(TESEntityDesertScorpion.class, id++, 16376764);
		TESEntityRegistry.register(TESEntityDikDik.class, id++, 12023867);
		TESEntityRegistry.register(TESEntityDirewolf.class, id++, 0x808080);
		TESEntityRegistry.register(TESEntityDragon.class, id++, 0x282728);
		TESEntityRegistry.register(TESEntityElephant.class, id++, 9605778);
		TESEntityRegistry.register(TESEntityFish.class, id++, 7053203);
		TESEntityRegistry.register(TESEntityFlamingo.class, id++, 16087966);
		TESEntityRegistry.register(TESEntityGemsbok.class, id++, 11759423);
		TESEntityRegistry.register(TESEntityGiraffe.class, id++, 13608551);
		TESEntityRegistry.register(TESEntityGorcrow.class, id++, 928034);
		TESEntityRegistry.register(TESEntityHorse.class, id++, 8601889);
		TESEntityRegistry.register(TESEntityJungleScorpion.class, id++, 2630945);
		TESEntityRegistry.register(TESEntityWhiteBison.class, id++, 16702665);
		TESEntityRegistry.register(TESEntityLion.class, id++, 13345354);
		TESEntityRegistry.register(TESEntityLioness.class, id++, 13346908);
		TESEntityRegistry.register(TESEntityMammoth.class, id++, 5653040);
		TESEntityRegistry.register(TESEntityManticore.class, id++, 0x2B3F19);
		TESEntityRegistry.register(TESEntityMidges.class, id++, 5653040);
		TESEntityRegistry.register(TESEntityRabbit.class, id++, 9860178);
		TESEntityRegistry.register(TESEntityRedScorpion.class, id++, 0x7c0a02);
		TESEntityRegistry.register(TESEntityRhino.class, id++, 6118481);
		TESEntityRegistry.register(TESEntitySeagull.class, id++, 15920107);
		TESEntityRegistry.register(TESEntityShadowcat.class, id++, 0x282728);
		TESEntityRegistry.register(TESEntitySnowBear.class, id++, 15594495);
		TESEntityRegistry.register(TESEntitySwan.class, id++, 16119285);
		TESEntityRegistry.register(TESEntityTermite.class, id++, 0x282728);
		TESEntityRegistry.register(TESEntityWalrus.class, id++, 7053203);
		TESEntityRegistry.register(TESEntityWhiteOryx.class, id++, 16381146);
		TESEntityRegistry.register(TESEntityBoar.class, id++, 6635562);
		TESEntityRegistry.register(TESEntityWoolyRhino.class, id++, 5653040);
		TESEntityRegistry.register(TESEntityWyvern.class, id++, 2896659);
		TESEntityRegistry.register(TESEntityZebra.class, id++, 15000804);
		
		TESEntityRegistry.registerHidden(TESEntityHummel009.class, id++);
		TESEntityRegistry.registerHidden(TESEntityPortal.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityPortal.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntitySmokeRing.class, id++, 64, 10, true);
		TESEntityRegistry.registerHidden(TESEntityBomb.class, id++, 160, 10, true);
		TESEntityRegistry.registerHidden(TESEntitySpear.class, id++, 64, Integer.MAX_VALUE, false);
		TESEntityRegistry.registerHidden(TESEntityPlate.class, id++, 64, 3, true);
		TESEntityRegistry.registerHidden(TESEntityThrowingAxe.class, id++, 64, Integer.MAX_VALUE, false);
		TESEntityRegistry.registerHidden(TESEntityCrossbowBolt.class, id++, 64, Integer.MAX_VALUE, false);
		TESEntityRegistry.registerHidden(TESEntityPebble.class, id++, 64, 3, true);
		TESEntityRegistry.registerHidden(TESEntityMysteryWeb.class, id++, 64, 10, true);
		TESEntityRegistry.registerHidden(TESEntityTraderRespawn.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityThrownRock.class, id++, 64, 10, true);
		TESEntityRegistry.registerHidden(TESEntityBarrel.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityInvasionSpawner.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityThrownTermite.class, id++, 64, 10, true);
		TESEntityRegistry.registerHidden(TESEntityConker.class, id++, 64, 3, true);
		TESEntityRegistry.registerHidden(TESEntityFishHook.class, id++, 64, 5, true);
		TESEntityRegistry.registerHidden(TESEntityDart.class, id++, 64, Integer.MAX_VALUE, false);
		TESEntityRegistry.registerHidden(TESEntityNPCRespawner.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityFallingTreasure.class, id++, 160, 20, true);
		TESEntityRegistry.registerHidden(TESEntityFallingFireJar.class, id++, 160, 20, true);
		TESEntityRegistry.registerHidden(TESEntityFirePot.class, id++, 64, 3, true);
		TESEntityRegistry.registerHidden(TESEntityArrowPoisoned.class, id++, 64, 20, false);
		TESEntityRegistry.registerHidden(TESEntityArrowFire.class, id++, 64, 20, false);
		TESEntityRegistry.registerHidden(TESEntityLionRug.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityBearRug.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityGiraffeRug.class, id++, 80, 3, true);
		TESEntityRegistry.registerHidden(TESEntityBanner.class, id++, 160, 3, true);
		TESEntityRegistry.registerHidden(TESEntityBannerWall.class, id++, 160, Integer.MAX_VALUE, false);
		TESEntityRegistry.registerHidden(TESEntityLingeringPotion.class, id++, 64, 10, true);
		TESEntityRegistry.registerHidden(TESEntityLingeringEffect.class, id++, 64, 1, true);
		TESEntityRegistry.registerHidden(TESEntityCargocart.class, id++, 80, 20, false);
		TESEntityRegistry.registerHidden(TESEntityPlowcart.class, id++, 80, 20, false);
		TESEntityRegistry.registerHidden(TESEntityMarshWraithBall.class, id++, 64, 10, true);
		TESEntityRegistry.registerHidden(TESEntitySnowball.class, id++, 64, 1, true);	}
}