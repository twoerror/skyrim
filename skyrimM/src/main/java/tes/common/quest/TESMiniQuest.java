package tes.common.quest;

import cpw.mods.fml.common.FMLLog;
import tes.common.TESDate;
import tes.common.TESDimension;
import tes.common.TESLore;
import tes.common.TESPlayerData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.database.TESSpeech;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import tes.common.faction.TESAlignmentBonusMap;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.item.other.TESItemCoin;
import tes.common.item.other.TESItemModifierTemplate;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class TESMiniQuest {
	public static final int MAX_MINIQUESTS_PER_FACTION = 5;
	public static final double RENDER_HEAD_DISTANCE = 12.0;

	private static final Map<String, Class<? extends TESMiniQuest>> NAME_TO_QUEST_MAPPING = new HashMap<>();
	private static final Map<Class<? extends TESMiniQuest>, String> QUEST_TO_NAME_MAPPING = new HashMap<>();

	static {
		registerQuestType("Collect", TESMiniQuestCollect.class);
		registerQuestType("KillFaction", TESMiniQuestKillFaction.class);
		registerQuestType("KillEntity", TESMiniQuestKillEntity.class);
		registerQuestType("Bounty", TESMiniQuestBounty.class);
		registerQuestType("Welcome", TESMiniQuestWelcome.class);
		registerQuestType("Pickpocket", TESMiniQuestPickpocket.class);
	}

	protected final Collection<ItemStack> itemsRewarded = new ArrayList<>();
	protected final List<String> quotesStages = new ArrayList<>();
	protected final List<ItemStack> rewardItemTable = new ArrayList<>();

	protected TESPlayerData playerData;
	protected String entityNameFull;
	protected String speechBankProgress;
	protected String speechBankComplete;
	protected String entityName;
	protected String speechBankStart;
	protected String speechBankTooMany;
	protected String quoteStart;
	protected String quoteComplete;
	protected UUID entityUUID;

	protected TESMiniQuestFactory questGroup;
	protected TESFaction entityFaction;

	protected boolean completed;
	protected boolean isLegendary;
	protected boolean wasHired;
	protected boolean willHire;
	protected float rewardFactor = 1.0f;
	protected float hiringAlignment;
	protected int dateCompleted;
	protected int coinsRewarded;

	private Pair<ChunkCoordinates, Integer> lastLocation;
	private TESBiome biomeGiven;
	private UUID questUUID;

	private boolean entityDead;
	private float alignmentRewarded;
	private int questColor;
	private int dateGiven;

	protected TESMiniQuest(TESPlayerData pd) {
		playerData = pd;
		questUUID = UUID.randomUUID();
	}

	public static TESMiniQuest loadQuestFromNBT(NBTTagCompound nbt, TESPlayerData playerData) {
		String questTypeName = nbt.getString("QuestType");
		Class<? extends TESMiniQuest> questType = NAME_TO_QUEST_MAPPING.get(questTypeName);
		if (questType == null) {
			FMLLog.severe("Could not instantiate miniquest of type " + questTypeName);
			return null;
		}
		TESMiniQuest quest = newQuestInstance(questType, playerData);
		if (quest != null) {
			quest.readFromNBT(nbt);
			if (quest.isValidQuest()) {
				return quest;
			}
			FMLLog.severe("Loaded an invalid TES miniquest " + quest.speechBankStart);
		}
		return null;
	}

	private static <Q extends TESMiniQuest> Q newQuestInstance(Class<Q> questType, TESPlayerData playerData) {
		try {
			return questType.getConstructor(TESPlayerData.class).newInstance(playerData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void registerQuestType(String name, Class<? extends TESMiniQuest> questType) {
		NAME_TO_QUEST_MAPPING.put(name, questType);
		QUEST_TO_NAME_MAPPING.put(questType, name);
	}

	public boolean anyRewardsGiven() {
		return alignmentRewarded > 0.0f || coinsRewarded > 0 || !itemsRewarded.isEmpty();
	}

	public boolean canPlayerAccept(EntityPlayer entityplayer) {
		return true;
	}

	protected boolean canRewardVariousExtraItems() {
		return true;
	}

	protected void complete(EntityPlayer entityplayer, TESEntityNPC npc) {
		TESAchievement achievement;
		completed = true;
		dateCompleted = TESDate.AegonCalendar.getCurrentDay();
		Random rand = npc.getRNG();
		List<ItemStack> dropItems = new ArrayList<>();
		float alignment = getAlignmentBonus();
		if (alignment != 0.0f) {
			alignment *= MathHelper.randomFloatClamp(rand, 0.75f, 1.25f);
			alignment = Math.max(alignment, 1.0f);
			TESAlignmentValues.AlignmentBonus bonus = TESAlignmentValues.createMiniquestBonus(alignment);
			TESFaction rewardFaction = getAlignmentRewardFaction();
			if (!questGroup.isNoAlignRewardForEnemy() || playerData.getAlignment(rewardFaction) >= 0.0f) {
				TESAlignmentBonusMap alignmentMap = playerData.addAlignment(entityplayer, bonus, rewardFaction, npc);
				alignmentRewarded = alignmentMap.get(rewardFaction);
			}
		}
		int coins = getCoinBonus();
		if (coins != 0) {
			if (shouldRandomiseCoinReward()) {
				coins = Math.round(coins * MathHelper.randomFloatClamp(rand, 0.75f, 1.25f));
				if (rand.nextInt(12) == 0) {
					coins *= MathHelper.getRandomIntegerInRange(rand, 2, 5);
				}
			}
			coinsRewarded = coins = Math.max(coins, 1);
			int coinsRemain = coins;
			for (int l = TESItemCoin.VALUES.length - 1; l >= 0; --l) {
				int coinValue = TESItemCoin.VALUES[l];
				if (coinsRemain < coinValue) {
					continue;
				}
				int numCoins = coinsRemain / coinValue;
				coinsRemain -= numCoins * coinValue;
				while (numCoins > 64) {
					numCoins -= 64;
					dropItems.add(new ItemStack(TESItems.coin, 64, l));
				}
				dropItems.add(new ItemStack(TESItems.coin, numCoins, l));
			}
		}
		if (!rewardItemTable.isEmpty()) {
			ItemStack item = rewardItemTable.get(rand.nextInt(rewardItemTable.size()));
			dropItems.add(item.copy());
			itemsRewarded.add(item.copy());
		}
		if (canRewardVariousExtraItems()) {
			TESLore lore;
			if (rand.nextInt(10) == 0 && questGroup != null && !questGroup.getLoreCategories().isEmpty() && (lore = TESLore.getMultiRandomLore(questGroup.getLoreCategories(), rand, true)) != null) {
				ItemStack loreBook = lore.createLoreBook(rand);
				dropItems.add(loreBook.copy());
				itemsRewarded.add(loreBook.copy());
			}
			if (rand.nextInt(15) == 0) {
				ItemStack modItem = TESItemModifierTemplate.getRandomCommonTemplate(rand);
				dropItems.add(modItem.copy());
				itemsRewarded.add(modItem.copy());
			}
		}
		if (!dropItems.isEmpty()) {
			boolean givePouch = canRewardVariousExtraItems() && rand.nextInt(10) == 0;
			if (givePouch) {
				ItemStack pouch = npc.createNPCPouchDrop();
				TESEntityNPC.fillPouchFromListAndRetainUnfilled(pouch, dropItems);
				npc.entityDropItem(pouch, 0.0f);
				ItemStack pouchCopy = pouch.copy();
				pouchCopy.setTagCompound(null);
				itemsRewarded.add(pouchCopy);
			}
			npc.dropItemList(dropItems, true);
		}
		if (willHire) {
			TESUnitTradeEntry tradeEntry = new TESUnitTradeEntry(npc.getClass(), 0, hiringAlignment);
			tradeEntry.setTask(TESHireableInfo.Task.WARRIOR);
			npc.getHireableInfo().hireUnit(entityplayer, false, entityFaction, tradeEntry, null, npc.ridingEntity);
			wasHired = true;
		}
		if (isLegendary) {
			npc.getHireableInfo().setActive(true);
		}
		playerData.updateMiniQuest(this);
		playerData.completeMiniQuest(this);
		sendCompletedSpeech(entityplayer, npc);
		if (questGroup != null && (achievement = questGroup.getAchievement()) != null) {
			playerData.addAchievement(achievement);
		}
	}

	protected abstract float getAlignmentBonus();

	private TESFaction getAlignmentRewardFaction() {
		return questGroup.checkAlignmentRewardFaction(entityFaction);
	}

	protected abstract int getCoinBonus();

	public abstract float getCompletionFactor();

	public String getFactionSubtitle() {
		if (entityFaction.isPlayableAlignmentFaction()) {
			return entityFaction.factionName();
		}
		return "";
	}

	public ChunkCoordinates getLastLocation() {
		if (lastLocation != null) {
			return lastLocation.getLeft();
		}
		return null;
	}

	public abstract String getObjectiveInSpeech();

	public abstract String getProgressedObjectiveInSpeech();

	public float[] getQuestColorComponents() {
		return new Color(questColor).getColorComponents(null);
	}

	public String getQuestFailure() {
		return StatCollector.translateToLocalFormatted("tes.gui.redBook.mq.diary.dead", entityName);
	}

	public String getQuestFailureShorthand() {
		return StatCollector.translateToLocal("tes.gui.redBook.mq.dead");
	}

	public abstract ItemStack getQuestIcon();

	public abstract String getQuestObjective();

	public abstract String getQuestProgress();

	public abstract String getQuestProgressShorthand();

	public abstract void handleEvent(TESMiniQuestEvent event);

	public boolean isActive() {
		return !completed && !isFailed();
	}

	public boolean isFailed() {
		return entityDead;
	}

	public boolean isValidQuest() {
		return entityUUID != null && entityFaction != null;
	}

	public abstract void onInteract(EntityPlayer entityplayer, TESEntityNPC npc);

	public boolean onInteractOther(EntityPlayer entityplayer, TESEntityNPC npc) {
		return false;
	}

	public abstract void onKill(EntityPlayer entityplayer, EntityLivingBase entity);

	public abstract void onKilledByPlayer(EntityPlayer entityplayer, EntityPlayer killer);

	public abstract void onPlayerTick();

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound itemData;
		ItemStack item;
		TESMiniQuestFactory factory;
		NBTTagList itemTags;
		String recovery;
		isLegendary = nbt.getBoolean("Legendary");
		if (nbt.hasKey("QuestGroup") && (factory = TESMiniQuestFactory.forName(nbt.getString("QuestGroup"))) != null) {
			questGroup = factory;
		}
		if (nbt.hasKey("QuestUUID")) {
			String questUUIDString = nbt.getString("QuestUUID");
			questUUID = UUID.fromString(questUUIDString);
		}
		entityUUID = nbt.hasKey("UUIDMost") && nbt.hasKey("UUIDLeast") ? new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast")) : UUID.fromString(nbt.getString("EntityUUID"));
		entityName = nbt.getString("Owner");
		entityNameFull = nbt.hasKey("OwnerFull") ? nbt.getString("OwnerFull") : entityName;
		entityFaction = TESFaction.forName(nbt.getString("Faction"));
		questColor = nbt.hasKey("Color") ? nbt.getInteger("Color") : entityFaction.getFactionColor();
		dateGiven = nbt.getInteger("DateGiven");
		if (nbt.hasKey("BiomeID")) {
			int biomeID = nbt.getByte("BiomeID") & 0xFF;
			String biomeDimName = nbt.getString("BiomeDim");
			TESDimension biomeDim = TESDimension.forName(biomeDimName);
			if (biomeDim != null) {
				biomeGiven = biomeDim.getBiomeList()[biomeID];
			}
		}
		rewardFactor = nbt.hasKey("RewardFactor") ? nbt.getFloat("RewardFactor") : 1.0f;
		willHire = nbt.getBoolean("WillHire");
		hiringAlignment = nbt.hasKey("HiringAlignment") ? nbt.getInteger("HiringAlignment") : nbt.getFloat("HiringAlignF");
		rewardItemTable.clear();
		if (nbt.hasKey("RewardItemTable")) {
			itemTags = nbt.getTagList("RewardItemTable", 10);
			for (int l = 0; l < itemTags.tagCount(); ++l) {
				itemData = itemTags.getCompoundTagAt(l);
				item = ItemStack.loadItemStackFromNBT(itemData);
				if (item == null) {
					continue;
				}
				rewardItemTable.add(item);
			}
		}
		completed = nbt.getBoolean("Completed");
		dateCompleted = nbt.getInteger("DateCompleted");
		coinsRewarded = nbt.getShort("CoinReward");
		alignmentRewarded = nbt.hasKey("AlignmentReward") ? nbt.getShort("AlignmentReward") : nbt.getFloat("AlignRewardF");
		wasHired = nbt.getBoolean("WasHired");
		itemsRewarded.clear();
		if (nbt.hasKey("ItemRewards")) {
			itemTags = nbt.getTagList("ItemRewards", 10);
			for (int l = 0; l < itemTags.tagCount(); ++l) {
				itemData = itemTags.getCompoundTagAt(l);
				item = ItemStack.loadItemStackFromNBT(itemData);
				if (item == null) {
					continue;
				}
				itemsRewarded.add(item);
			}
		}
		entityDead = nbt.getBoolean("OwnerDead");
		if (nbt.hasKey("Dimension")) {
			ChunkCoordinates coords = new ChunkCoordinates(nbt.getInteger("XPos"), nbt.getInteger("YPos"), nbt.getInteger("ZPos"));
			int dimension = nbt.getInteger("Dimension");
			lastLocation = Pair.of(coords, dimension);
		}
		speechBankStart = nbt.getString("SpeechStart");
		speechBankProgress = nbt.getString("SpeechProgress");
		speechBankComplete = nbt.getString("SpeechComplete");
		speechBankTooMany = nbt.getString("SpeechTooMany");
		quoteStart = nbt.getString("QuoteStart");
		quoteComplete = nbt.getString("QuoteComplete");
		quotesStages.clear();
		if (nbt.hasKey("QuotesStages")) {
			NBTTagList stageTags = nbt.getTagList("QuotesStages", 8);
			for (int l = 0; l < stageTags.tagCount(); ++l) {
				String s = stageTags.getStringTagAt(l);
				quotesStages.add(s);
			}
		}
		if (questGroup == null && (recovery = speechBankStart) != null) {
			TESMiniQuestFactory factory2;
			int i1 = recovery.indexOf('/');
			int i2 = recovery.indexOf('/', i1 + 1);
			if (i1 >= 0 && i2 >= 0 && (factory2 = TESMiniQuestFactory.forName(recovery.substring(i1 + 1, i2))) != null) {
				questGroup = factory2;
			}
		}
	}

	protected void sendCompletedSpeech(EntityPlayer entityplayer, TESEntityNPC npc) {
		sendQuoteSpeech(entityplayer, npc, quoteComplete);
	}

	protected void sendProgressSpeechbank(EntityPlayer entityplayer, TESEntityNPC npc) {
		npc.sendSpeechBank(entityplayer, speechBankProgress, this);
	}

	protected void sendQuoteSpeech(EntityPlayer entityplayer, TESEntityNPC npc, String quote) {
		TESSpeech.sendSpeech(entityplayer, npc, TESSpeech.formatSpeech(quote, entityplayer, null, getObjectiveInSpeech()));
		npc.markNPCSpoken();
	}

	public void setEntityDead() {
		entityDead = true;
		playerData.updateMiniQuest(this);
	}

	protected void setNPCInfo(TESEntityNPC npc) {
		entityUUID = npc.getUniqueID();
		entityName = npc.getNPCName();
		entityNameFull = npc.getCommandSenderName();
		entityFaction = npc.getFaction();
		questColor = npc.getFaction().getFactionColor();
	}

	public TESFaction getEntityFaction() {
		return entityFaction;
	}

	public UUID getQuestUUID() {
		return questUUID;
	}

	public boolean isCompleted() {
		return completed;
	}

	public UUID getEntityUUID() {
		return entityUUID;
	}

	protected boolean shouldRandomiseCoinReward() {
		return true;
	}

	public String getSpeechBankTooMany() {
		return speechBankTooMany;
	}

	public void setPlayerData(TESPlayerData playerData) {
		this.playerData = playerData;
	}

	public boolean isWillHire() {
		return willHire;
	}

	public Collection<ItemStack> getItemsRewarded() {
		return itemsRewarded;
	}

	public boolean isWasHired() {
		return wasHired;
	}

	public List<String> getQuotesStages() {
		return quotesStages;
	}

	public int getDateCompleted() {
		return dateCompleted;
	}

	public String getQuoteComplete() {
		return quoteComplete;
	}

	public float getAlignmentRewarded() {
		return alignmentRewarded;
	}

	public int getCoinsRewarded() {
		return coinsRewarded;
	}

	public int getDateGiven() {
		return dateGiven;
	}

	public TESBiome getBiomeGiven() {
		return biomeGiven;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setQuestGroup(TESMiniQuestFactory questGroup) {
		this.questGroup = questGroup;
	}

	public String getQuoteStart() {
		return quoteStart;
	}

	public String getSpeechBankStart() {
		return speechBankStart;
	}

	public int getQuestColor() {
		return questColor;
	}

	public void start(EntityPlayer entityplayer, TESEntityNPC npc) {
		setNPCInfo(npc);
		dateGiven = TESDate.AegonCalendar.getCurrentDay();
		int i = MathHelper.floor_double(entityplayer.posX);
		int k = MathHelper.floor_double(entityplayer.posZ);
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(entityplayer.worldObj, i, k);
		if (biome instanceof TESBiome) {
			biomeGiven = (TESBiome) biome;
		}
		playerData.addMiniQuest(this);
		npc.getQuestInfo().addActiveQuestPlayer(entityplayer);
		playerData.setTrackingMiniQuest(this);
	}

	public void updateLocation(TESEntityNPC npc) {
		int i = MathHelper.floor_double(npc.posX);
		int j = MathHelper.floor_double(npc.posY);
		int k = MathHelper.floor_double(npc.posZ);
		ChunkCoordinates coords = new ChunkCoordinates(i, j, k);
		int dim = npc.dimension;
		ChunkCoordinates prevCoords = null;
		if (lastLocation != null) {
			prevCoords = lastLocation.getLeft();
		}
		lastLocation = Pair.of(coords, dim);
		boolean sendUpdate = prevCoords == null || coords.getDistanceSquaredToChunkCoordinates(prevCoords) > 256.0;
		if (sendUpdate) {
			playerData.updateMiniQuest(this);
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList itemTags;
		NBTTagCompound itemData;
		nbt.setString("QuestType", QUEST_TO_NAME_MAPPING.get(getClass()));
		if (questGroup != null) {
			nbt.setString("QuestGroup", questGroup.getBaseName());
		}
		nbt.setString("QuestUUID", questUUID.toString());
		nbt.setString("EntityUUID", entityUUID.toString());
		nbt.setString("Owner", entityName);
		nbt.setString("OwnerFull", entityNameFull);
		nbt.setString("Faction", entityFaction.codeName());
		nbt.setInteger("Color", questColor);
		nbt.setInteger("DateGiven", dateGiven);
		if (biomeGiven != null) {
			nbt.setByte("BiomeID", (byte) biomeGiven.biomeID);
			nbt.setString("BiomeDim", biomeGiven.getBiomeDimension().getDimensionName());
		}
		nbt.setFloat("RewardFactor", rewardFactor);
		nbt.setBoolean("WillHire", willHire);
		nbt.setFloat("HiringAlignF", hiringAlignment);
		if (!rewardItemTable.isEmpty()) {
			itemTags = new NBTTagList();
			for (ItemStack item : rewardItemTable) {
				itemData = new NBTTagCompound();
				item.writeToNBT(itemData);
				itemTags.appendTag(itemData);
			}
			nbt.setTag("RewardItemTable", itemTags);
		}
		nbt.setBoolean("Completed", completed);
		nbt.setInteger("DateCompleted", dateCompleted);
		nbt.setShort("CoinReward", (short) coinsRewarded);
		nbt.setFloat("AlignRewardF", alignmentRewarded);
		nbt.setBoolean("WasHired", wasHired);
		if (!itemsRewarded.isEmpty()) {
			itemTags = new NBTTagList();
			for (ItemStack item : itemsRewarded) {
				itemData = new NBTTagCompound();
				item.writeToNBT(itemData);
				itemTags.appendTag(itemData);
			}
			nbt.setTag("ItemRewards", itemTags);
		}
		nbt.setBoolean("OwnerDead", entityDead);
		if (lastLocation != null) {
			ChunkCoordinates coords = lastLocation.getLeft();
			nbt.setInteger("XPos", coords.posX);
			nbt.setInteger("YPos", coords.posY);
			nbt.setInteger("ZPos", coords.posZ);
			nbt.setInteger("Dimension", lastLocation.getRight());
		}
		nbt.setBoolean("Legendary", isLegendary);
		nbt.setString("SpeechStart", speechBankStart);
		nbt.setString("SpeechProgress", speechBankProgress);
		nbt.setString("SpeechComplete", speechBankComplete);
		nbt.setString("SpeechTooMany", speechBankTooMany);
		nbt.setString("QuoteStart", quoteStart);
		nbt.setString("QuoteComplete", quoteComplete);
		if (!quotesStages.isEmpty()) {
			NBTTagList stageTags = new NBTTagList();
			for (String s : quotesStages) {
				stageTags.appendTag(new NBTTagString(s));
			}
			nbt.setTag("QuotesStages", stageTags);
		}
	}

	public abstract static class QuestFactoryBase<Q extends TESMiniQuest> {
		protected final String questName;

		protected TESMiniQuestFactory questFactoryGroup;

		protected boolean willHire;
		protected boolean isLegendary;
		protected float rewardFactor = 1.0f;
		protected float hiringAlignment;

		protected QuestFactoryBase(String name) {
			questName = name;
		}

		public Q createQuest(TESEntityNPC npc, Random rand) {
			Q quest = newQuestInstance(getQuestClass(), null);
			if (quest != null) {
				quest.questGroup = questFactoryGroup;
				String pathName = "miniquest/" + questFactoryGroup.getBaseName() + '/';
				String pathNameBaseSpeech = "miniquest/" + questFactoryGroup.getBaseSpeechGroup().getBaseName() + '/';
				String questPathName = pathName + questName + '_';
				quest.speechBankStart = questPathName + "start";
				quest.speechBankProgress = questPathName + "progress";
				quest.speechBankComplete = questPathName + "complete";
				quest.speechBankTooMany = pathNameBaseSpeech + "_tooMany";
				quest.isLegendary = isLegendary;
				quest.quoteStart = TESSpeech.getRandomSpeech(quest.speechBankStart);
				quest.quoteComplete = TESSpeech.getRandomSpeech(quest.speechBankComplete);
				quest.setNPCInfo(npc);
				quest.rewardFactor = rewardFactor;
				quest.willHire = willHire;
				quest.hiringAlignment = hiringAlignment;
				return quest;
			}
			return null;
		}

		public void setFactoryGroup(TESMiniQuestFactory factory) {
			questFactoryGroup = factory;
		}

		public abstract Class<Q> getQuestClass();

		public QuestFactoryBase<Q> setHiring() {
			willHire = true;
			hiringAlignment = 100.0F;
			return this;
		}

		public QuestFactoryBase<Q> setIsLegendary() {
			isLegendary = true;
			return this;
		}

		public QuestFactoryBase<Q> setRewardFactor(float f) {
			rewardFactor = f;
			return this;
		}
	}

	public static class SorterAlphabetical implements Comparator<TESMiniQuest> {
		@Override
		public int compare(TESMiniQuest q1, TESMiniQuest q2) {
			if (!q2.isActive() && q1.isActive()) {
				return 1;
			}
			if (!q1.isActive() && q2.isActive()) {
				return -1;
			}
			if (q1.entityFaction == q2.entityFaction) {
				return q1.entityName.compareTo(q2.entityName);
			}
			return Integer.compare(q1.entityFaction.ordinal(), q2.entityFaction.ordinal());
		}
	}
}