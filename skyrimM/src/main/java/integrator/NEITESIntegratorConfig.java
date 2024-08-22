package integrator;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import tes.TES;
import tes.common.database.TESBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class NEITESIntegratorConfig implements IConfigureNEI {
	public static final Collection<ItemStack> HIDDEN_ITEMS = new ArrayList<>();

	private static void hideItem(Block block) {
		hideItem(false, block);
	}

	private static void hideItem(boolean all, Block block) {
		hideItem(new ItemStack(block), all);
	}

	private static void hideItem(ItemStack stack, boolean all) {
		int i = all ? 0 : 8;
		while (i < 16) {
			ItemStack s = new ItemStack(stack.getItem(), 1, i);
			API.hideItem(s);
			HIDDEN_ITEMS.add(s);
			i++;
		}
	}

	@Override
	public String getName() {
		return TES.NAME;
	}

	@Override
	public String getVersion() {
		return TES.VERSION;
	}

	@Override
	public void loadConfig() {
		hideItem(true, TESBlocks.lionBed);
		hideItem(true, TESBlocks.strawBed);
		hideItem(true, TESBlocks.furBed);
		hideItem(true, TESBlocks.berryPie);
		hideItem(true, TESBlocks.cherryPie);
		hideItem(true, TESBlocks.pastry);
		hideItem(true, TESBlocks.appleCrumble);
		hideItem(true, TESBlocks.bananaCake);
		hideItem(true, TESBlocks.lemonCake);
		hideItem(true, TESBlocks.date);
		hideItem(true, TESBlocks.banana);
		hideItem(true, TESBlocks.grapevineRed);
		hideItem(true, TESBlocks.grapevineWhite);
		hideItem(true, TESBlocks.fuse);
		hideItem(true, TESBlocks.sothoryosDoubleTorch);
		hideItem(true, TESBlocks.lettuceCrop);
		hideItem(true, TESBlocks.pipeweedCrop);
		hideItem(true, TESBlocks.flaxCrop);
		hideItem(true, TESBlocks.leekCrop);
		hideItem(true, TESBlocks.turnipCrop);
		hideItem(true, TESBlocks.yamCrop);
		hideItem(true, TESBlocks.spawnerChest);
		hideItem(true, TESBlocks.spawnerChestStone);
		hideItem(true, TESBlocks.spawnerChestAncientEssos);
		hideItem(true, TESBlocks.mug);
		hideItem(true, TESBlocks.ceramicMug);
		hideItem(true, TESBlocks.gobletGold);
		hideItem(true, TESBlocks.gobletSilver);
		hideItem(true, TESBlocks.gobletCopper);
		hideItem(true, TESBlocks.gobletWood);
		hideItem(true, TESBlocks.skullCup);
		hideItem(true, TESBlocks.wineGlass);
		hideItem(true, TESBlocks.glassBottle);
		hideItem(true, TESBlocks.aleHorn);
		hideItem(true, TESBlocks.aleHornGold);
		hideItem(true, TESBlocks.plate);
		hideItem(true, TESBlocks.ceramicPlate);
		hideItem(true, TESBlocks.woodPlate);
		hideItem(true, TESBlocks.flowerPot);
		hideItem(true, TESBlocks.armorStand);
		hideItem(true, TESBlocks.marshLights);
		hideItem(true, TESBlocks.signCarved);
		hideItem(true, TESBlocks.signCarvedGlowing);
		hideItem(true, TESBlocks.bookshelfStorage);
		hideItem(true, TESBlocks.slabDouble1);
		hideItem(true, TESBlocks.slabDouble2);
		hideItem(true, TESBlocks.slabDouble3);
		hideItem(true, TESBlocks.slabDouble4);
		hideItem(true, TESBlocks.slabDouble5);
		hideItem(true, TESBlocks.slabDouble6);
		hideItem(true, TESBlocks.slabDouble7);
		hideItem(true, TESBlocks.slabDouble8);
		hideItem(true, TESBlocks.slabDouble9);
		hideItem(true, TESBlocks.slabDouble10);
		hideItem(true, TESBlocks.slabDouble11);
		hideItem(true, TESBlocks.slabDouble12);
		hideItem(true, TESBlocks.slabDoubleV);
		hideItem(true, TESBlocks.slabDoubleThatch);
		hideItem(true, TESBlocks.slabDoubleDirt);
		hideItem(true, TESBlocks.slabDoubleSand);
		hideItem(true, TESBlocks.slabDoubleGravel);
		hideItem(true, TESBlocks.rottenSlabDouble);
		hideItem(true, TESBlocks.scorchedSlabDouble);
		hideItem(true, TESBlocks.slabClayTileDouble);
		hideItem(true, TESBlocks.slabClayTileDyedDouble1);
		hideItem(true, TESBlocks.slabClayTileDyedDouble2);
		hideItem(true, TESBlocks.slabBoneDouble);
		hideItem(true, TESBlocks.woodSlabDouble1);
		hideItem(true, TESBlocks.woodSlabDouble2);
		hideItem(true, TESBlocks.woodSlabDouble3);
		hideItem(true, TESBlocks.woodSlabDouble4);
		hideItem(true, TESBlocks.woodSlabDouble5);
		hideItem(TESBlocks.slabSingle1);
		hideItem(TESBlocks.slabSingle2);
		hideItem(TESBlocks.slabSingle3);
		hideItem(TESBlocks.slabSingle4);
		hideItem(TESBlocks.slabSingle5);
		hideItem(TESBlocks.slabSingle6);
		hideItem(TESBlocks.slabSingle7);
		hideItem(TESBlocks.slabSingle8);
		hideItem(TESBlocks.slabSingle9);
		hideItem(TESBlocks.slabSingle10);
		hideItem(TESBlocks.slabSingle11);
		hideItem(TESBlocks.slabSingle12);
		hideItem(TESBlocks.slabSingleV);
		hideItem(TESBlocks.slabSingleThatch);
		hideItem(TESBlocks.slabSingleDirt);
		hideItem(TESBlocks.slabSingleSand);
		hideItem(TESBlocks.slabSingleGravel);
		hideItem(TESBlocks.rottenSlabSingle);
		hideItem(TESBlocks.scorchedSlabSingle);
		hideItem(TESBlocks.slabClayTileSingle);
		hideItem(TESBlocks.slabClayTileDyedSingle1);
		hideItem(TESBlocks.slabClayTileDyedSingle2);
		hideItem(TESBlocks.slabBoneSingle);
		hideItem(TESBlocks.woodSlabSingle1);
		hideItem(TESBlocks.woodSlabSingle2);
		hideItem(TESBlocks.woodSlabSingle3);
		hideItem(TESBlocks.woodSlabSingle4);
		hideItem(TESBlocks.woodSlabSingle5);
	}
}