package tes.client.render;

import tes.client.effect.TESEntityAlignmentBonus;
import tes.client.effect.TESEntityDeadMarshFace;
import tes.client.effect.TESEntitySwordCommandMarker;
import tes.client.render.animal.*;
import tes.client.render.npc.TESRenderLegendaryNPC;
import tes.client.render.other.*;
import tes.common.database.TESItems;
import tes.common.entity.animal.*;
import tes.common.entity.dragon.TESEntityDragon;

import tes.common.entity.other.*;
import tes.common.entity.other.inanimate.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;

import java.util.HashMap;
import java.util.Map;

public class TESRender {
	public static final Map<Class<? extends Entity>, Render> RENDERS = new HashMap<>();

	private TESRender() {
	}

	public static void onInit() {
		RENDERS.put(TESEntityHummel009.class, new TESRenderLegendaryNPC("hummel009"));
		RENDERS.put(TESEntityBeaver.class, new TESRenderBeaver());
		RENDERS.put(EntityPotion.class, new RenderSnowball(Items.potionitem, 16384));
		RENDERS.put(TESEntityAlignmentBonus.class, new TESRenderAlignmentBonus());
		RENDERS.put(TESEntityArrowPoisoned.class, new TESRenderArrowPoisoned());
		RENDERS.put(TESEntityArrowFire.class, new TESRenderArrowFire());
		RENDERS.put(TESEntityBison.class, new TESRenderBison());
		RENDERS.put(TESEntityBanner.class, new TESRenderBanner());
		RENDERS.put(TESEntityBannerWall.class, new TESRenderBannerWall());
		RENDERS.put(TESEntityBarrel.class, new TESRenderBarrel());
		RENDERS.put(TESEntityBarrowWight.class, new TESRenderBarrowWight());
		RENDERS.put(TESEntityBear.class, new TESRenderBear());
		RENDERS.put(TESEntityBearRug.class, new TESRenderBearRug());
		RENDERS.put(TESEntityBird.class, new TESRenderBird());
		RENDERS.put(TESEntityBomb.class, new TESRenderBomb());
		RENDERS.put(TESEntityButterfly.class, new TESRenderButterfly());
		RENDERS.put(TESEntityCamel.class, new TESRenderCamel());
		RENDERS.put(TESEntityCargocart.class, new TESRenderCargocart());
		RENDERS.put(TESEntitySnowball.class, new RenderSnowball(Items.snowball));
		RENDERS.put(TESEntityConker.class, new RenderSnowball(TESItems.chestnut));
		RENDERS.put(TESEntityCrocodile.class, new TESRenderCrocodile());
		RENDERS.put(TESEntityCrossbowBolt.class, new TESRenderCrossbowBolt());
		RENDERS.put(TESEntityDart.class, new TESRenderDart());
		RENDERS.put(TESEntityDeer.class, new TESRenderDeer());
		RENDERS.put(TESEntityDikDik.class, new TESRenderDikDik());
		RENDERS.put(TESEntityDirewolf.class, new TESRenderDirewolf());
		RENDERS.put(TESEntityDragon.class, new TESRenderDragon());
		RENDERS.put(TESEntityElephant.class, new TESRenderElephant());
		RENDERS.put(TESEntityFallingFireJar.class, new TESRenderFallingFireJar());
		RENDERS.put(TESEntityFallingTreasure.class, new TESRenderFallingCoinPile());
		RENDERS.put(TESEntityFirePot.class, new RenderSnowball(TESItems.firePot));
		RENDERS.put(TESEntityFish.class, new TESRenderFish());
		RENDERS.put(TESEntityFlamingo.class, new TESRenderFlamingo());
		RENDERS.put(TESEntityGemsbok.class, new TESRenderGemsbok());
		RENDERS.put(TESEntityGiraffe.class, new TESRenderGiraffe());
		RENDERS.put(TESEntityGiraffeRug.class, new TESRenderGiraffeRug());		RENDERS.put(TESEntityHorse.class, new TESRenderHorse());
		RENDERS.put(TESEntityInvasionSpawner.class, new TESRenderInvasionSpawner());
		RENDERS.put(TESEntityWhiteBison.class, new TESRenderWhiteBison());
		RENDERS.put(TESEntityLingeringEffect.class, new TESRenderLingeringEffect());
		RENDERS.put(TESEntityLingeringPotion.class, new TESRenderLingeringPotion());
		RENDERS.put(TESEntityLionBase.class, new TESRenderLion());
		RENDERS.put(TESEntityLionRug.class, new TESRenderLionRug());
		RENDERS.put(TESEntityMammoth.class, new TESRenderMammoth());
		RENDERS.put(TESEntityManticore.class, new TESRenderManticore());
		RENDERS.put(TESEntityMidges.class, new TESRenderMidges());
		RENDERS.put(TESEntityMysteryWeb.class, new RenderSnowball(TESItems.mysteryWeb));
		RENDERS.put(TESEntityNPCRespawner.class, new TESRenderNPCRespawner());
		RENDERS.put(TESEntityPebble.class, new RenderSnowball(TESItems.pebble));
		RENDERS.put(TESEntityPlate.class, new TESRenderPlate());
		RENDERS.put(TESEntityPlowcart.class, new TESRenderPlowcart());
		RENDERS.put(TESEntityPortal.class, new TESRenderPortal());
		RENDERS.put(TESEntityRabbit.class, new TESRenderRabbit());
		RENDERS.put(TESEntityRedScorpion.class, new TESRenderRedScorpion());
		RENDERS.put(TESEntityRhino.class, new TESRenderRhino());
		RENDERS.put(TESEntityScorpionBig.class, new TESRenderScorpion());
		RENDERS.put(TESEntitySmokeRing.class, new TESRenderSmokeRing());
		RENDERS.put(TESEntitySnowBear.class, new TESRenderSnowBear());
		RENDERS.put(TESEntitySpear.class, new TESRenderSpear());
		RENDERS.put(TESEntitySwan.class, new TESRenderSwan());
		RENDERS.put(TESEntityTermite.class, new TESRenderTermite());
		RENDERS.put(TESEntityThrowingAxe.class, new TESRenderThrowingAxe());
		RENDERS.put(TESEntityThrownRock.class, new TESRenderThrownRock());
		RENDERS.put(TESEntityThrownTermite.class, new RenderSnowball(TESItems.termite));
		RENDERS.put(TESEntityTraderRespawn.class, new TESRenderTraderRespawn());
		RENDERS.put(TESEntityShadowcat.class, new TESRenderShadowcat());
		RENDERS.put(TESEntityWalrus.class, new TESRenderWalrus());
		RENDERS.put(TESEntityWhiteOryx.class, new TESRenderWhiteOryx());
		RENDERS.put(TESEntityBoar.class, new TESRenderBoar());
		RENDERS.put(TESEntityWoolyRhino.class, new TESRenderWoolyRhino());
		RENDERS.put(TESEntityWyvern.class, new TESRenderWyvern());
		RENDERS.put(TESEntityZebra.class, new TESRenderZebra());
		RENDERS.put(TESEntityMarshWraithBall.class, new TESRenderWraithBall());
		RENDERS.put(TESEntitySwordCommandMarker.class, new TESRenderSwordCommandMarker());
	}
}