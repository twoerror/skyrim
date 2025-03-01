package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.client.render.other.TESConnectedTextures;
import tes.common.block.TESConnectedBlock;
import tes.common.database.TESCreativeTabs;
import tes.common.item.TESWeaponStats;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TESBlockGate extends Block implements TESConnectedBlock {
	private final boolean hasConnectedTextures;

	private TESBlockGate(Material material, boolean ct) {
		super(material);
		hasConnectedTextures = ct;
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
	}

	public static TESBlockGate createMetal(boolean ct) {
		TESBlockGate block = new TESBlockGate(Material.iron, ct);
		block.setHardness(4.0f);
		block.setResistance(10.0f);
		block.setStepSound(soundTypeMetal);
		return block;
	}

	public static TESBlockGate createWooden(boolean ct) {
		TESBlockGate block = new TESBlockGate(Material.wood, ct);
		block.setHardness(4.0f);
		block.setResistance(5.0f);
		block.setStepSound(soundTypeWood);
		return block;
	}

	private static int getGateDirection(IBlockAccess world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		return getGateDirection(meta);
	}

	private static int getGateDirection(int meta) {
		return meta & 7;
	}

	private static boolean isGateOpen(IBlockAccess world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		return isGateOpen(meta);
	}

	private static boolean isGateOpen(int meta) {
		return (meta & 8) != 0;
	}

	private static void setGateOpen(World world, int i, int j, int k, boolean flag) {
		int meta = world.getBlockMetadata(i, j, k);
		meta = flag ? meta | 8 : meta & 7;
		world.setBlockMetadataWithNotify(i, j, k, meta, 3);
	}

	private static boolean directionsMatch(int dir1, int dir2) {
		switch (dir1) {
			case 0:
			case 1:
				return dir1 == dir2;
			case 2:
			case 3:
				return dir2 == 2 || dir2 == 3;
			case 4:
			case 5:
				return dir2 == 4 || dir2 == 5;
			default:
				return false;
		}
	}

	private static void gatherAdjacentGate(IBlockAccess world, int i, int j, int k, int dir, boolean open, Collection<ChunkCoordinates> allCoords, Collection<ChunkCoordinates> currentDepthCoords) {
		ChunkCoordinates coords = new ChunkCoordinates(i, j, k);
		if (allCoords.contains(coords)) {
			return;
		}
		Block otherBlock = world.getBlock(i, j, k);
		if (otherBlock instanceof TESBlockGate) {
			boolean otherOpen = isGateOpen(world, i, j, k);
			int otherDir = getGateDirection(world, i, j, k);
			if (otherOpen == open && directionsMatch(dir, otherDir)) {
				allCoords.add(coords);
				currentDepthCoords.add(coords);
			}
		}
	}

	private static void gatherAdjacentGates(IBlockAccess world, int i, int j, int k, int dir, boolean open, Collection<ChunkCoordinates> allCoords, Collection<ChunkCoordinates> currentDepthCoords) {
		if (dir != 0 && dir != 1) {
			gatherAdjacentGate(world, i, j - 1, k, dir, open, allCoords, currentDepthCoords);
			gatherAdjacentGate(world, i, j + 1, k, dir, open, allCoords, currentDepthCoords);
		}
		if (dir != 2 && dir != 3) {
			gatherAdjacentGate(world, i, j, k - 1, dir, open, allCoords, currentDepthCoords);
			gatherAdjacentGate(world, i, j, k + 1, dir, open, allCoords, currentDepthCoords);
		}
		if (dir != 4 && dir != 5) {
			gatherAdjacentGate(world, i - 1, j, k, dir, open, allCoords, currentDepthCoords);
			gatherAdjacentGate(world, i + 1, j, k, dir, open, allCoords, currentDepthCoords);
		}
	}

	private static List<ChunkCoordinates> getConnectedGates(IBlockAccess world, int i, int j, int k) {
		boolean open = isGateOpen(world, i, j, k);
		int dir = getGateDirection(world, i, j, k);
		HashSet<ChunkCoordinates> allCoords = new HashSet<>();
		HashSet<ChunkCoordinates> lastDepthCoords = new HashSet<>();
		Collection<ChunkCoordinates> currentDepthCoords = new HashSet<>();
		for (int depth = 0; depth <= 16; ++depth) {
			if (depth == 0) {
				allCoords.add(new ChunkCoordinates(i, j, k));
				currentDepthCoords.add(new ChunkCoordinates(i, j, k));
			} else {
				for (ChunkCoordinates coords : lastDepthCoords) {
					gatherAdjacentGates(world, coords.posX, coords.posY, coords.posZ, dir, open, allCoords, currentDepthCoords);
				}
			}
			lastDepthCoords.clear();
			lastDepthCoords.addAll(currentDepthCoords);
			currentDepthCoords.clear();
		}
		return new ArrayList<>(allCoords);
	}

	private void activateGate(World world, int i, int j, int k) {
		boolean wasOpen = isGateOpen(world, i, j, k);
		boolean isOpen = !wasOpen;
		List<ChunkCoordinates> gates = getConnectedGates(world, i, j, k);
		for (ChunkCoordinates coords : gates) {
			setGateOpen(world, coords.posX, coords.posY, coords.posZ, isOpen);
		}
		boolean stone = getMaterial() == Material.rock;
		String soundEffect = stone ? isOpen ? "tes:block.gate.stone_open" : "tes:block.gate.stone_close" : isOpen ? "tes:block.gate.open" : "tes:block.gate.close";
		world.playSoundEffect(i + 0.5, j + 0.5, k + 0.5, soundEffect, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
	}

	@Override
	public boolean areBlocksConnected(IBlockAccess world, int i, int j, int k, int i1, int j1, int k1) {
		int meta = world.getBlockMetadata(i, j, k);
		Block otherBlock = world.getBlock(i1, j1, k1);
		int otherMeta = world.getBlockMetadata(i1, j1, k1);
		int dir = getGateDirection(meta);
		boolean open = isGateOpen(meta);
		int otherDir = getGateDirection(otherMeta);
		boolean otherOpen = isGateOpen(otherMeta);
		if ((dir == 0 || dir == 1) && j1 != j) {
			return false;
		}
		if ((dir == 2 || dir == 3) && k1 != k) {
			return false;
		}
		if ((dir == 4 || dir == 5) && i1 != i) {
			return false;
		}
		if (open && j1 == j - 1 && !(otherBlock instanceof TESBlockGate)) {
			return true;
		}
		boolean connected = open ? otherBlock instanceof TESBlockGate : otherBlock == this;
		return connected && directionsMatch(dir, otherDir) && open == otherOpen;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess world, int i, int j, int k) {
		return isGateOpen(world, i, j, k);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		if (isGateOpen(world, i, j, k)) {
			return null;
		}
		setBlockBoundsBasedOnState(world, i, j, k);
		return super.getCollisionBoundingBoxFromPool(world, i, j, k);
	}

	@Override
	public String getConnectedName(int meta) {
		return textureName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int i, int j, int k, int side) {
		boolean open = isGateOpen(world, i, j, k);
		if (hasConnectedTextures) {
			return TESConnectedTextures.getConnectedIconBlock(this, world, i, j, k, side, open);
		}
		if (open) {
			return TESConnectedTextures.getConnectedIconBlock(this, world, i, j, k, side, false);
		}
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j) {
		if (hasConnectedTextures) {
			return TESConnectedTextures.getConnectedIconItem(this, j);
		}
		return blockIcon;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		ItemStack itemstack = entityplayer.getHeldItem();
		if (itemstack != null) {
			Item item = itemstack.getItem();
			if (getBlockFromItem(item) instanceof TESBlockGate || TESWeaponStats.isRangedWeapon(itemstack)) {
				return false;
			}
		}
		if (!world.isRemote) {
			activateGate(world, i, j, k);
		}
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		if (!world.isRemote && !(block instanceof TESBlockGate)) {
			boolean open = isGateOpen(world, i, j, k);
			boolean powered = false;
			List<ChunkCoordinates> gates = getConnectedGates(world, i, j, k);
			for (ChunkCoordinates coords : gates) {
				int i1 = coords.posX;
				int j1 = coords.posY;
				int k1 = coords.posZ;
				if (!world.isBlockIndirectlyGettingPowered(i1, j1, k1)) {
					continue;
				}
				powered = true;
				break;
			}
			if ((powered || block.canProvidePower()) && powered ^ open) {
				activateGate(world, i, j, k);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister) {
		if (hasConnectedTextures) {
			TESConnectedTextures.registerConnectedIcons(iconregister, this, 0, true);
		} else {
			super.registerBlockIcons(iconregister);
			TESConnectedTextures.registerNonConnectedGateIcons(iconregister, this, 0);
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
		int dir = getGateDirection(world, i, j, k);
		setBlockBoundsForDirection(dir);
	}

	private void setBlockBoundsForDirection(int dir) {
		float width = 0.25f;
		float halfWidth = width / 2.0f;
		switch (dir) {
			case 0:
				setBlockBounds(0.0f, 1.0f - width, 0.0f, 1.0f, 1.0f, 1.0f);
				break;
			case 1:
				setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, width, 1.0f);
				break;
			case 2:
			case 3:
				setBlockBounds(0.0f, 0.0f, 0.5f - halfWidth, 1.0f, 1.0f, 0.5f + halfWidth);
				break;
			case 4:
			case 5:
				setBlockBounds(0.5f - halfWidth, 0.0f, 0.0f, 0.5f + halfWidth, 1.0f, 1.0f);
				break;
		}
	}

	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBoundsForDirection(4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int i, int j, int k, int side) {
		int i1 = i - Facing.offsetsXForSide[side];
		int j1 = j - Facing.offsetsYForSide[side];
		int k1 = k - Facing.offsetsZForSide[side];
		Block otherBlock = world.getBlock(i, j, k);
		if (otherBlock instanceof TESBlockGate) {
			int metaThis = world.getBlockMetadata(i1, j1, k1);
			int metaOther = world.getBlockMetadata(i, j, k);
			int dirThis = getGateDirection(metaThis);
			boolean openThis = isGateOpen(metaThis);
			int dirOther = getGateDirection(metaOther);
			boolean openOther = isGateOpen(metaOther);
			boolean connect = !directionsMatch(dirThis, side);
			if (connect) {
				return openThis != openOther || !directionsMatch(dirThis, dirOther);
			}
		}
		return super.shouldSideBeRendered(world, i, j, k, side);
	}
}