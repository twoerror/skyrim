package tes.common.inventory;

import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.block.table.TESBlockCraftingTable;
import tes.common.database.TESBlocks;
import tes.common.recipe.TESRecipe;
import tes.common.recipe.TESRecipePouch;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class TESContainerCraftingTable extends ContainerWorkbench {
	private final World theWorld;
	private final int tablePosX;
	private final int tablePosY;
	private final int tablePosZ;
	private final EntityPlayer entityplayer;
	private final List<IRecipe> recipeList;

	private final TESBlockCraftingTable tableBlock;

	protected TESContainerCraftingTable(InventoryPlayer inv, World world, int i, int j, int k, List<IRecipe> list, Block block) {
		super(inv, world, i, j, k);
		theWorld = world;
		tablePosX = i;
		tablePosY = j;
		tablePosZ = k;
		entityplayer = inv.player;
		tableBlock = (TESBlockCraftingTable) block;
		recipeList = new ArrayList<>(list);
		recipeList.add(new TESRecipePouch(tableBlock.getFaction()));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return theWorld.getBlock(tablePosX, tablePosY, tablePosZ) == tableBlock && entityplayer.getDistanceSq(tablePosX + 0.5, tablePosY + 0.5, tablePosZ + 0.5) <= 64.0;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inv) {
		if (recipeList == null) {
			return;
		}
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		boolean tableSwitched = pd.getTableSwitched();
		if (tableSwitched) {
			craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, theWorld));
		} else {
			craftResult.setInventorySlotContents(0, TESRecipe.findMatchingRecipe(recipeList, craftMatrix, theWorld));
		}
	}

	public TESBlockCraftingTable getTableBlock() {
		return tableBlock;
	}

	public List<IRecipe> getRecipeList() {
		return recipeList;
	}

	public static class Empire extends TESContainerCraftingTable {
		public Empire(InventoryPlayer inv, World world, int i, int j, int k) {
			super(inv, world, i, j, k, TESRecipe.Empire, TESBlocks.tableEmpire);
		}
	}

}