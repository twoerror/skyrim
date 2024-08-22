package tes;

import tes.common.*;
import tes.common.block.other.TESBlockIronBank;
import tes.common.database.*;
import tes.common.enchant.TESEnchantmentCombining;
import tes.common.entity.TESEntity;
import tes.common.faction.TESFaction;
import tes.common.item.TESPoisonedDrinks;
import tes.common.network.TESPacketHandler;
import tes.common.quest.TESMiniQuestFactory;
import tes.common.recipe.TESRecipe;
import tes.common.recipe.TESRecipeBrewing;
import tes.common.recipe.TESRecipeMillstone;
import tes.common.tileentity.TESTileEntityRegistry;
import tes.common.util.TESModChecker;
import tes.common.util.TESPatcher;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.TESBeziers;
import tes.common.world.map.TESFixer;
import tes.common.world.structure.TESStructure;
import integrator.NEITESIntegrator;

public class TESLoader {
	private TESLoader() {
	}

	public static void preInit() {
		TESConfig.preInit();
		TESPacketHandler.preInit();
		TESBlocks.preInit();
		TESItems.preInit();
		TESEntity.preInit();
		TESInvasions.preInit();
		TESBiome.preInit();
		TESShields.preInit();
		TESCapes.preInit();
		TESPoisonedDrinks.preInit();
		TESPotionChanges.preInit();
		if (TESModChecker.hasNEI() && TES.proxy.isClient()) {
			NEITESIntegrator.registerRecipes();
		}
		TESBlockIronBank.preInit();
	}

	public static void onInit() {
		TESBlocks.onInit();
		TESTileEntityRegistry.onInit();
		TESCreativeTabs.onInit();
		TESRecipe.onInit();
		TESMaterial.onInit();
		TESDimension.onInit();
		TESSpeech.onInit();
		TESNames.onInit();
		TESRecipeBrewing.onInit();
		TESRecipeMillstone.onInit();
		TESEnchantmentCombining.onInit();
		TESAchievement.onInit();
		TESChatEvents.onInit();
		TESFaction.onInit();
		TESStructure.onInit();
		TESBeziers.onInit();
		TESMiniQuestFactory.onInit();
		TESLore.onInit();
		TESTitle.onInit();
		TESFixer.onInit();
		TESPatcher.onInit();
	}

	public static void postInit() {
		TESBiome.postInit();
	}
}