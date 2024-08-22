package tes.common;

import cpw.mods.fml.common.network.IGuiHandler;
import tes.TES;
import tes.client.gui.*;
import tes.common.database.TESBlocks;
import tes.common.database.TESGuiId;
import tes.common.entity.animal.TESEntityHorse;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.entity.other.iface.TESMercenary;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.entity.other.iface.TESUnitTradeable;
import tes.common.entity.other.inanimate.TESEntityNPCRespawner;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.inventory.*;
import tes.common.item.other.TESItemPouch;
import tes.common.tileentity.*;
import tes.common.util.TESReflection;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;

public class TESGuiHandler implements IGuiHandler {
	public static final TESGuiHandler INSTANCE = new TESGuiHandler();

	private TESGuiHandler() {
	}

	private static boolean testForSlotPackedGuiID(int fullID, int guiID) {
		return (fullID & 0xFFFF) == guiID;
	}

	private static int unpackSlot(int fullID) {
		return fullID >> 16;
	}

	public static int packGuiIDWithSlot(int guiID, int slotNo) {
		return guiID | slotNo << 16;
	}

	public static void usePouchOnChest(EntityPlayer entityplayer, World world, int i, int j, int k, int side, ItemStack itemstack, int pouchSlot) {
		if (world.isRemote && TES.proxy.isClient()) {
			((EntityClientPlayerMP) entityplayer).sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(i, j, k, side, itemstack, 0.0F, 0.0F, 0.0F));
		} else {
			entityplayer.openGui(TES.instance, packGuiIDWithSlot(TESGuiId.POUCH_CHEST.ordinal(), pouchSlot), world, i, j, k);
		}
	}

	@Override
	@SuppressWarnings("CastConflictsWithInstanceof")
	public Object getClientGuiElement(int ID, EntityPlayer entityplayer, World world, int i, int j, int k) {
		TESEntityNPC npc;
		Entity entity;
		if (testForSlotPackedGuiID(ID, TESGuiId.POUCH_CHEST.ordinal())) {
			int slot = unpackSlot(ID);
			IInventory chest2 = TESItemPouch.getChestInvAt(entityplayer, world, i, j, k);
			if (chest2 != null) {
				return new TESGuiChestWithPouch(entityplayer, slot, chest2);
			}
		}
		if (testForSlotPackedGuiID(ID, TESGuiId.POUCH_MINECART.ordinal())) {
			int slot = unpackSlot(ID);
			Entity minecart = world.getEntityByID(i);
			if (minecart instanceof EntityMinecartContainer) {
				return new TESGuiChestWithPouch(entityplayer, slot, (IInventory) minecart);
			}
		}
		TESGuiId id = TESGuiId.values()[ID];
		switch (id) {
			case ALLOY_FORGE:
				TileEntity forge = world.getTileEntity(i, j, k);
				if (forge instanceof TESTileEntityAlloyForge) {
					return new TESGuiAlloyForge(entityplayer.inventory, (TESTileEntityAlloyForge) forge);
				}
				break;
			case ANVIL:
				return new TESGuiAnvil(entityplayer, i, j, k);
			case ANVIL_NPC:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					return new TESGuiAnvil(entityplayer, (TESEntityNPC) entity);
				}
				break;
			case ARMOR_STAND:
				TileEntity stand = world.getTileEntity(i, j, k);
				if (stand instanceof TESTileEntityArmorStand) {
					return new TESGuiArmorStand(entityplayer.inventory, (TESTileEntityArmorStand) stand);
				}
				break;
			case BARREL:
				TileEntity barrel = world.getTileEntity(i, j, k);
				if (barrel instanceof TESTileEntityBarrel) {
					return new TESGuiBarrel(entityplayer.inventory, (TESTileEntityBarrel) barrel);
				}
				break;
			case BOOKSHELF:
				if (world.getBlock(i, j, k) == Blocks.bookshelf) {
					world.setBlock(i, j, k, TESBlocks.bookshelfStorage, 0, 3);
				}
				TileEntity bookshelf = world.getTileEntity(i, j, k);
				if (bookshelf instanceof TESTileEntityBookshelf) {
					return new TESGuiBookshelf(entityplayer.inventory, (TESTileEntityBookshelf) bookshelf);
				}
				break;
			case BRANDING_IRON:
				return new TESGuiBrandingIron();
			case CHEST:
				TileEntity chest = world.getTileEntity(i, j, k);
				if (chest instanceof TESTileEntityChest) {
					return new GuiChest(entityplayer.inventory, (IInventory) chest);
				}
				break;
			case COIN_EXCHANGE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					return new TESGuiCoinExchange(entityplayer, (TESEntityNPC) entity);
				}
				break;
			case CONQUEST:
				return new TESGuiMap().setConquestGrid();
			case DISPENSER:
				TileEntity trap = world.getTileEntity(i, j, k);
				if (trap instanceof TESTileEntitySarbacaneTrap) {
					return new GuiDispenser(entityplayer.inventory, (TileEntityDispenser) trap);
				}
				break;
			case EDIT_SIGN:
				Block block = world.getBlock(i, j, k);
				int meta = world.getBlockMetadata(i, j, k);
				TESTileEntitySignCarved fake = (TESTileEntitySignCarved) block.createTileEntity(world, meta);
				fake.setWorldObj(world);
				fake.xCoord = i;
				fake.yCoord = j;
				fake.zCoord = k;
				fake.setFakeGuiSign(true);
				return new TESGuiEditSign(fake);
			case HIRED_FARMER_INVENTORY:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					npc = (TESEntityNPC) entity;
					if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer().equals(entityplayer) && npc.getHireableInfo().getHiredTask() == TESHireableInfo.Task.FARMER) {
						return new TESGuiHiredFarmerInventory(entityplayer.inventory, npc);
					}
				}
				break;
			case HIRED_INTERACT:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					return new TESGuiHiredInteract((TESEntityNPC) entity);
				}
				break;
			case HIRED_INTERACT_NO_FUNC:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					return new TESGuiHiredInteractNoFunc((TESEntityNPC) entity);
				}
				break;
			case HIRED_WARRIOR_INVENTORY:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					npc = (TESEntityNPC) entity;
					//if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer().equals(entityplayer) && npc.getHireableInfo().getHiredTask() == TESHireableInfo.Task.WARRIOR) {
					//	return new TESGuiHiredWarriorInventory(entityplayer.inventory, npc);
					//}
				}
				break;
			case HORN_SELECT:
				return new TESGuiHornSelect();
			case IRON_BANK:
				return new TESGuiIronBank();
			case MENU:
				return TESGuiMenu.openMenu(entityplayer);
			case MERCENARY_HIRE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESMercenary) {
					return new TESGuiMercenaryHire(entityplayer, (TESMercenary) entity, world);
				}
				break;
			case MERCENARY_INTERACT:
				entity = world.getEntityByID(i);
				if (entity instanceof TESMercenary) {
					return new TESGuiMercenaryInteract((TESEntityNPC) entity);
				}
				break;
			case MILLSTONE:
				TileEntity millstone = world.getTileEntity(i, j, k);
				if (millstone instanceof TESTileEntityMillstone) {
					return new TESGuiMillstone(entityplayer.inventory, (TESTileEntityMillstone) millstone);
				}
				break;
			case MOUNT_INVENTORY:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityHorse) {
					TESEntityHorse horse = (TESEntityHorse) entity;
					return new TESGuiMountInventory(entityplayer.inventory, new AnimalChest(horse.getCommandSenderName(), j), horse);
				}
				break;
			case NPC_RESPAWNER:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPCRespawner) {
					return new TESGuiNPCRespawner((TESEntityNPCRespawner) entity);
				}
				break;
			case OVEN:
				TileEntity oven = world.getTileEntity(i, j, k);
				if (oven instanceof TESTileEntityOven) {
					return new TESGuiOven(entityplayer.inventory, (TESTileEntityOven) oven);
				}
				break;
			case POUCH:
				return new TESGuiPouch(entityplayer, i);
			case QUEST_BOOK:
				return new TESGuiQuestBook();
			case SQUADRON_ITEM:
				return new TESGuiSquadronItem();
			case TABLE_EMPIRE:
				return new TESGuiCraftingTable.Empire(entityplayer.inventory, world, i, j, k);

			case TRADE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESTradeable) {
					return new TESGuiTrade(entityplayer.inventory, (TESTradeable) entity, world);
				}
				break;
			case TRADE_INTERACT:
				entity = world.getEntityByID(i);
				if (entity instanceof TESTradeable) {
					return new TESGuiTradeInteract((TESEntityNPC) entity);
				}
				break;
			case TRADE_UNIT_TRADE_INTERACT:
				entity = world.getEntityByID(i);
				if (entity instanceof TESTradeable) {
					return new TESGuiTradeUnitTradeInteract((TESEntityNPC) entity);
				}
				break;
			case UNIT_TRADE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESUnitTradeable) {
					return new TESGuiUnitTrade(entityplayer, (TESUnitTradeable) entity, world);
				}
				break;
			case UNIT_TRADE_INTERACT:
				entity = world.getEntityByID(i);
				if (entity instanceof TESUnitTradeable) {
					return new TESGuiUnitTradeInteract((TESEntityNPC) entity);
				}
				break;
			case UNSMELTERY:
				TileEntity unsmeltery = world.getTileEntity(i, j, k);
				if (unsmeltery instanceof TESTileEntityUnsmeltery) {
					return new TESGuiUnsmeltery(entityplayer.inventory, (TESTileEntityUnsmeltery) unsmeltery);
				}
				break;
			default:
				break;
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer entityplayer, World world, int i, int j, int k) {
		TESEntityNPC npc;
		Entity entity;
		IInventory chest2;
		int slot = unpackSlot(ID);
		if (testForSlotPackedGuiID(ID, TESGuiId.POUCH_CHEST.ordinal()) && TESItemPouch.isHoldingPouch(entityplayer, slot) && (chest2 = TESItemPouch.getChestInvAt(entityplayer, world, i, j, k)) != null) {
			return new TESContainerChestWithPouch(entityplayer, slot, chest2);
		}
		Entity minecart = world.getEntityByID(i);
		if (testForSlotPackedGuiID(ID, TESGuiId.POUCH_MINECART.ordinal()) && TESItemPouch.isHoldingPouch(entityplayer, slot) && minecart instanceof EntityMinecartContainer) {
			return new TESContainerChestWithPouch(entityplayer, slot, (IInventory) minecart);
		}
		TESGuiId id = TESGuiId.values()[ID];
		switch (id) {
			case ALLOY_FORGE:
				TileEntity forge = world.getTileEntity(i, j, k);
				if (forge instanceof TESTileEntityAlloyForge) {
					return new TESContainerAlloyForge(entityplayer.inventory, (TESTileEntityAlloyForge) forge);
				}
				break;
			case ANVIL:
				return new TESContainerAnvil(entityplayer, i, j, k);
			case ANVIL_NPC:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					return new TESContainerAnvil(entityplayer, (TESEntityNPC) entity);
				}
				break;
			case ARMOR_STAND:
				TileEntity stand = world.getTileEntity(i, j, k);
				if (stand instanceof TESTileEntityArmorStand) {
					return new TESContainerArmorStand(entityplayer.inventory, (TESTileEntityArmorStand) stand);
				}
				break;
			case BARREL:
				TileEntity barrel = world.getTileEntity(i, j, k);
				if (barrel instanceof TESTileEntityBarrel) {
					return new TESContainerBarrel(entityplayer.inventory, (TESTileEntityBarrel) barrel);
				}
				break;
			case BOOKSHELF:
				TileEntity bookshelf = world.getTileEntity(i, j, k);
				if (bookshelf instanceof TESTileEntityBookshelf) {
					return new TESContainerBookshelf(entityplayer.inventory, (TESTileEntityBookshelf) bookshelf);
				}
				break;
			case CHEST:
				TileEntity chest = world.getTileEntity(i, j, k);
				if (chest instanceof TESTileEntityChest) {
					return new ContainerChest(entityplayer.inventory, (IInventory) chest);
				}
				break;
			case COIN_EXCHANGE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					npc = (TESEntityNPC) entity;
					return new TESContainerCoinExchange(entityplayer, npc);
				}
				break;
			case DISPENSER:
				TileEntity trap = world.getTileEntity(i, j, k);
				if (trap instanceof TESTileEntitySarbacaneTrap) {
					return new ContainerDispenser(entityplayer.inventory, (TileEntityDispenser) trap);
				}
				break;
			case HIRED_FARMER_INVENTORY:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					npc = (TESEntityNPC) entity;
					if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer().equals(entityplayer) && npc.getHireableInfo().getHiredTask() == TESHireableInfo.Task.FARMER) {
						return new TESContainerHiredFarmerInventory(entityplayer.inventory, npc);
					}
				}
				break;
			case HIRED_WARRIOR_INVENTORY:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityNPC) {
					npc = (TESEntityNPC) entity;
					if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer().equals(entityplayer) && npc.getHireableInfo().getHiredTask() == TESHireableInfo.Task.WARRIOR) {
						return new TESContainerHiredWarriorInventory(entityplayer.inventory, npc);
					}
				}
				break;
			case IRON_BANK:
				return new TESGuiIronBank();
			case MERCENARY_HIRE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESMercenary) {
					return new TESContainerUnitTrade(entityplayer, (TESHireableBase) entity, world);
				}
				break;
			case MILLSTONE:
				TileEntity millstone = world.getTileEntity(i, j, k);
				if (millstone instanceof TESTileEntityMillstone) {
					return new TESContainerMillstone(entityplayer.inventory, (TESTileEntityMillstone) millstone);
				}
				break;
			case MOUNT_INVENTORY:
				entity = world.getEntityByID(i);
				if (entity instanceof TESEntityHorse) {
					TESEntityHorse horse = (TESEntityHorse) entity;
					return new TESContainerMountInventory(entityplayer.inventory, TESReflection.getHorseInv(horse), horse);
				}
				break;
			case OVEN:
				TileEntity oven = world.getTileEntity(i, j, k);
				if (oven instanceof TESTileEntityOven) {
					return new TESContainerOven(entityplayer.inventory, (TESTileEntityOven) oven);
				}
				break;
			case POUCH:
				if (TESItemPouch.isHoldingPouch(entityplayer, i)) {
					return new TESContainerPouch(entityplayer, i);
				}
				break;
			case TABLE_EMPIRE:
				return new TESContainerCraftingTable.Empire(entityplayer.inventory, world, i, j, k);
			case TRADE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESTradeable) {
					return new TESContainerTrade(entityplayer.inventory, (TESTradeable) entity, world);
				}
				break;
			case UNIT_TRADE:
				entity = world.getEntityByID(i);
				if (entity instanceof TESUnitTradeable) {
					return new TESContainerUnitTrade(entityplayer, (TESHireableBase) entity, world);
				}
				break;
			case UNSMELTERY:
				TileEntity unsmeltery = world.getTileEntity(i, j, k);
				if (unsmeltery instanceof TESTileEntityUnsmeltery) {
					return new TESContainerUnsmeltery(entityplayer.inventory, (TESTileEntityUnsmeltery) unsmeltery);
				}
				break;
			default:
				break;
		}
		return null;
	}
}