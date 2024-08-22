package tes.common.entity.other.info;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.database.TESTradeEntries;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.inventory.TESContainerTrade;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketTraderInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;

import java.util.List;
import java.util.Random;

public class TESTraderInfo {
	private final TESEntityNPC theEntity;

	private TESTradeEntry[] buyTrades;
	private TESTradeEntry[] sellTrades;

	private boolean shouldLockTrades = true;
	private boolean shouldRefresh = true;

	private int timeUntilAdvertisement;
	private int timeSinceTrade;
	private int lockTradeAtValue = 200;
	private int lockValueDecayTicks = 60;
	private int valueSinceRefresh;
	private int refreshAtValue = 5000;
	private int lockTicksAfterRefresh = 6000;

	public TESTraderInfo(TESEntityNPC npc) {
		theEntity = npc;
		if (theEntity instanceof TESTradeable && !theEntity.worldObj.isRemote) {
			refreshTrades();
		}
	}

	public TESTradeEntry[] getBuyTrades() {
		return buyTrades;
	}

	private void setBuyTrades(TESTradeEntry[] trades) {
		for (TESTradeEntry trade : trades) {
			trade.setOwningTrader(this);
		}
		buyTrades = trades;
	}

	public int getLockTradeAtValue() {
		return lockTradeAtValue;
	}

	public TESTradeEntry[] getSellTrades() {
		return sellTrades;
	}

	private void setSellTrades(TESTradeEntry[] trades) {
		for (TESTradeEntry trade : trades) {
			trade.setOwningTrader(this);
		}
		sellTrades = trades;
	}

	public int getValueDecayTicks() {
		return lockValueDecayTicks;
	}

	public void onTrade(EntityPlayer entityplayer, TESTradeEntry trade, TESTradeEntries.TradeType type, int value) {
		((TESTradeable) theEntity).onPlayerTrade(entityplayer, type, trade.createTradeItem());
		TESLevelData.getData(entityplayer).getFactionData(theEntity.getFaction()).addTrade();
		trade.doTransaction(value);
		timeSinceTrade = 0;
		valueSinceRefresh += value;
		sendClientPacket(entityplayer);
	}

	public void onUpdate() {
		if (!theEntity.worldObj.isRemote) {
			if (timeUntilAdvertisement > 0) {
				--timeUntilAdvertisement;
			}
			++timeSinceTrade;
			int ticksExisted = theEntity.ticksExisted;
			boolean sendUpdate = false;
			for (TESTradeEntry trade : buyTrades) {
				if (trade == null || !trade.updateAvailability(ticksExisted)) {
					continue;
				}
				sendUpdate = true;
			}
			for (TESTradeEntry trade : sellTrades) {
				if (trade == null || !trade.updateAvailability(ticksExisted)) {
					continue;
				}
				sendUpdate = true;
			}
			if (shouldRefresh && valueSinceRefresh >= refreshAtValue) {
				refreshTrades();
				setAllTradesDelayed();
				sendUpdate = true;
			}
			if (sendUpdate) {
				List<EntityPlayer> players = theEntity.worldObj.playerEntities;
				for (EntityPlayer entityplayer : players) {
					Container container = entityplayer.openContainer;
					if (!(container instanceof TESContainerTrade) || ((TESContainerTrade) container).getTheTraderNPC() != theEntity) {
						continue;
					}
					sendClientPacket(entityplayer);
				}
			}
			if (theEntity.isEntityAlive() && theEntity.getAttackTarget() == null && timeUntilAdvertisement == 0 && timeSinceTrade > 600) {
				double range = 10.0;
				List<EntityPlayer> players = theEntity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, theEntity.boundingBox.expand(range, range, range));
				for (EntityPlayer obj : players) {
					String speechBank;
					if (!obj.isEntityAlive() || obj.capabilities.isCreativeMode || obj.openContainer != null && obj.openContainer != obj.inventoryContainer || (speechBank = theEntity.getSpeechBank(obj)) == null || theEntity.getRNG().nextInt(3) != 0) {
						continue;
					}
					theEntity.sendSpeechBank(obj, speechBank);
				}
				timeUntilAdvertisement = 20 * MathHelper.getRandomIntegerInRange(theEntity.getRNG(), 5, 20);
			}
		}
	}

	public void readFromNBT(NBTTagCompound data) {
		int i;
		NBTTagCompound nbt;
		NBTTagCompound sellTradesData;
		NBTTagCompound buyTradesData;
		TESTradeEntry trade;
		if (data.hasKey("TESBuyTrades") && (buyTradesData = data.getCompoundTag("TESBuyTrades")).hasKey("Trades")) {
			NBTTagList buyTradesTags = buyTradesData.getTagList("Trades", 10);
			buyTrades = new TESTradeEntry[buyTradesTags.tagCount()];
			for (i = 0; i < buyTradesTags.tagCount(); ++i) {
				nbt = buyTradesTags.getCompoundTagAt(i);
				trade = TESTradeEntry.readFromNBT(nbt);
				trade.setOwningTrader(this);
				buyTrades[i] = trade;
			}
		}
		if (data.hasKey("TESSellTrades") && (sellTradesData = data.getCompoundTag("TESSellTrades")).hasKey("Trades")) {
			NBTTagList sellTradesTags = sellTradesData.getTagList("Trades", 10);
			sellTrades = new TESTradeEntry[sellTradesTags.tagCount()];
			for (i = 0; i < sellTradesTags.tagCount(); ++i) {
				nbt = sellTradesTags.getCompoundTagAt(i);
				trade = TESTradeEntry.readFromNBT(nbt);
				trade.setOwningTrader(this);
				sellTrades[i] = trade;
			}
		}
		timeSinceTrade = data.getInteger("TimeSinceTrade");
		if (data.hasKey("ShouldLockTrades")) {
			shouldLockTrades = data.getBoolean("ShouldLockTrades");
		}
		if (data.hasKey("LockTradeAtValue")) {
			lockTradeAtValue = data.getInteger("LockTradeAtValue");
		}
		if (data.hasKey("LockValueDecayTicks")) {
			lockValueDecayTicks = data.getInteger("LockValueDecayTicks");
		}
		if (data.hasKey("ShouldRefresh")) {
			shouldRefresh = data.getBoolean("ShouldRefresh");
		}
		if (data.hasKey("RefreshAtValue")) {
			refreshAtValue = data.getInteger("RefreshAtValue");
		}
		if (data.hasKey("LockTicksAfterRefresh")) {
			lockTicksAfterRefresh = data.getInteger("LockTicksAfterRefresh");
		}
		valueSinceRefresh = data.getInteger("ValueSinceRefresh");
	}

	public void receiveClientPacket(TESPacketTraderInfo packet) {
		NBTTagCompound nbt = packet.getTraderData();
		readFromNBT(nbt);
	}

	private void refreshTrades() {
		TESTradeable theTrader = (TESTradeable) theEntity;
		Random rand = theEntity.getRNG();
		setBuyTrades(theTrader.getBuyPool().getRandomTrades(rand));
		setSellTrades(theTrader.getSellPool().getRandomTrades(rand));
		valueSinceRefresh = 0;
		List<EntityPlayer> players = theEntity.worldObj.playerEntities;
		for (EntityPlayer entityplayer : players) {
			Container container = entityplayer.openContainer;
			if (!(container instanceof TESContainerTrade) || ((TESContainerTrade) container).getTheTraderNPC() != theEntity) {
				continue;
			}
			((TESContainerTrade) container).updateAllTradeSlots();
		}
	}

	public void sendClientPacket(EntityPlayer entityplayer) {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		IMessage packet = new TESPacketTraderInfo(nbt);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
	}

	private void setAllTradesDelayed() {
		int delay = lockTicksAfterRefresh;
		for (TESTradeEntry trade : buyTrades) {
			if (trade == null) {
				continue;
			}
			trade.setLockedForTicks(delay);
		}
		for (TESTradeEntry trade : sellTrades) {
			if (trade == null) {
				continue;
			}
			trade.setLockedForTicks(delay);
		}
	}

	public boolean shouldLockTrades() {
		return shouldLockTrades;
	}

	public void writeToNBT(NBTTagCompound data) {
		NBTTagCompound nbt;
		if (buyTrades != null) {
			NBTTagList buyTradesTags = new NBTTagList();
			for (TESTradeEntry trade : buyTrades) {
				if (trade == null) {
					continue;
				}
				nbt = new NBTTagCompound();
				trade.writeToNBT(nbt);
				buyTradesTags.appendTag(nbt);
			}
			NBTTagCompound buyTradesData = new NBTTagCompound();
			buyTradesData.setTag("Trades", buyTradesTags);
			data.setTag("TESBuyTrades", buyTradesData);
		}
		if (sellTrades != null) {
			NBTTagList sellTradesTags = new NBTTagList();
			for (TESTradeEntry trade : sellTrades) {
				if (trade == null) {
					continue;
				}
				nbt = new NBTTagCompound();
				trade.writeToNBT(nbt);
				sellTradesTags.appendTag(nbt);
			}
			NBTTagCompound sellTradesData = new NBTTagCompound();
			sellTradesData.setTag("Trades", sellTradesTags);
			data.setTag("TESSellTrades", sellTradesData);
		}
		data.setInteger("TimeSinceTrade", timeSinceTrade);
		data.setBoolean("ShouldLockTrades", shouldLockTrades);
		data.setInteger("LockTradeAtValue", lockTradeAtValue);
		data.setInteger("LockValueDecayTicks", lockValueDecayTicks);
		data.setBoolean("ShouldRefresh", shouldRefresh);
		data.setInteger("RefreshAtValue", refreshAtValue);
		data.setInteger("LockTicksAfterRefresh", lockTicksAfterRefresh);
		data.setInteger("ValueSinceRefresh", valueSinceRefresh);
	}
}