package tes.common.quest;

import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.entity.other.TESEntityNPC;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionBounties;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TESMiniQuestBounty extends TESMiniQuest {
	private UUID targetID;
	private String targetName;
	private boolean killed;
	private float alignmentBonus;
	private int coinBonus;
	private boolean bountyClaimedByOther;
	private boolean killedByBounty;

	@SuppressWarnings("unused")
	public TESMiniQuestBounty(TESPlayerData pd) {
		super(pd);
	}

	private static TESFaction getPledgeOrHighestAlignmentFaction(EntityPlayer entityplayer, float min) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		if (pd.getPledgeFaction() != null) {
			return pd.getPledgeFaction();
		}
		ArrayList<TESFaction> highestFactions = new ArrayList<>();
		float highestAlignment = min;
		for (TESFaction f : TESFaction.getPlayableAlignmentFactions()) {
			float alignment = pd.getAlignment(f);
			if (alignment <= min) {
				continue;
			}
			if (alignment > highestAlignment) {
				highestFactions.clear();
				highestFactions.add(f);
				highestAlignment = alignment;
				continue;
			}
			if (alignment != highestAlignment) {
				continue;
			}
			highestFactions.add(f);
		}
		if (!highestFactions.isEmpty()) {
			Random rand = entityplayer.getRNG();
			return highestFactions.get(rand.nextInt(highestFactions.size()));
		}
		return null;
	}

	@Override
	public boolean canPlayerAccept(EntityPlayer entityplayer) {
		if (super.canPlayerAccept(entityplayer) && !targetID.equals(entityplayer.getUniqueID()) && TESLevelData.getData(entityplayer).getAlignment(entityFaction) >= 100.0f) {
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			List<TESMiniQuest> active = pd.getActiveMiniQuests();
			for (TESMiniQuest quest : active) {
				if (!(quest instanceof TESMiniQuestBounty) || !((TESMiniQuestBounty) quest).targetID.equals(targetID)) {
					continue;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	protected void complete(EntityPlayer entityplayer, TESEntityNPC npc) {
		TESPlayerData pd = playerData;
		pd.addCompletedBountyQuest();
		int bComplete = pd.getCompletedBountyQuests();
		boolean specialReward = bComplete > 0 && bComplete % 5 == 0;
		if (specialReward) {
			ItemStack trophy = new ItemStack(TESItems.bountyTrophy);
			rewardItemTable.add(trophy);
		}
		super.complete(entityplayer, npc);
		pd.addAchievement(TESAchievement.doMiniquestHunter);
		if (specialReward) {
			pd.addAchievement(TESAchievement.doMiniquestHunter5);
		}
	}

	@Override
	public float getAlignmentBonus() {
		return alignmentBonus;
	}

	protected void setAlignmentBonus(float alignmentBonus) {
		this.alignmentBonus = alignmentBonus;
	}

	@Override
	public int getCoinBonus() {
		return coinBonus;
	}

	protected void setCoinBonus(int coinBonus) {
		this.coinBonus = coinBonus;
	}

	@Override
	public float getCompletionFactor() {
		return killed ? 1.0f : 0.0f;
	}

	private float getKilledAlignmentPenalty() {
		return -alignmentBonus * 2.0f;
	}

	@Override
	public String getObjectiveInSpeech() {
		return targetName;
	}

	@Override
	public String getProgressedObjectiveInSpeech() {
		return targetName;
	}

	@Override
	public String getQuestFailure() {
		if (killedByBounty) {
			return StatCollector.translateToLocalFormatted("tes.miniquest.bounty.killedBy", targetName);
		}
		if (bountyClaimedByOther) {
			return StatCollector.translateToLocalFormatted("tes.miniquest.bounty.claimed", targetName);
		}
		return super.getQuestFailure();
	}

	@Override
	public String getQuestFailureShorthand() {
		if (killedByBounty) {
			return StatCollector.translateToLocal("tes.miniquest.bounty.killedBy.short");
		}
		if (bountyClaimedByOther) {
			return StatCollector.translateToLocal("tes.miniquest.bounty.claimed.short");
		}
		return super.getQuestFailureShorthand();
	}

	@Override
	public ItemStack getQuestIcon() {
		return new ItemStack(Items.iron_sword);
	}

	@Override
	public String getQuestObjective() {
		return StatCollector.translateToLocalFormatted("tes.miniquest.bounty", targetName);
	}

	@Override
	public String getQuestProgress() {
		if (killed) {
			return StatCollector.translateToLocal("tes.miniquest.bounty.progress.slain");
		}
		return StatCollector.translateToLocal("tes.miniquest.bounty.progress.notSlain");
	}

	@Override
	public String getQuestProgressShorthand() {
		return StatCollector.translateToLocalFormatted("tes.miniquest.progressShort", killed ? 1 : 0, 1);
	}

	@Override
	public void handleEvent(TESMiniQuestEvent event) {
	}

	@Override
	public boolean isFailed() {
		return super.isFailed() || bountyClaimedByOther || killedByBounty;
	}

	@Override
	public boolean isValidQuest() {
		return super.isValidQuest() && targetID != null;
	}

	@Override
	public void onInteract(EntityPlayer entityplayer, TESEntityNPC npc) {
		if (killed) {
			complete(entityplayer, npc);
		} else {
			sendProgressSpeechbank(entityplayer, npc);
		}
	}

	@Override
	public void onKill(EntityPlayer entityplayer, EntityLivingBase entity) {
		if (!killed && !isFailed() && entity instanceof EntityPlayer && entity.getUniqueID().equals(targetID)) {
			EntityPlayer slainPlayer = (EntityPlayer) entity;
			TESPlayerData slainPlayerData = TESLevelData.getData(slainPlayer);
			killed = true;
			TESFactionBounties.forFaction(entityFaction).forPlayer(slainPlayer).recordBountyKilled();
			playerData.updateMiniQuest(this);
			TESFaction highestFaction = getPledgeOrHighestAlignmentFaction(slainPlayer, 100.0f);
			if (highestFaction != null) {
				float curAlignment = slainPlayerData.getAlignment(highestFaction);
				float alignmentLoss = getKilledAlignmentPenalty();
				if (curAlignment + alignmentLoss < 100.0f) {
					alignmentLoss = -(curAlignment - 100.0f);
				}
				TESAlignmentValues.AlignmentBonus source = new TESAlignmentValues.AlignmentBonus(alignmentLoss, "tes.alignment.bountyKill");
				slainPlayerData.addAlignment(slainPlayer, source, highestFaction, entityplayer);
				IChatComponent slainMsg1 = new ChatComponentTranslation("tes.chat.bountyKilled1", entityplayer.getCommandSenderName(), entityFaction.factionName());
				IChatComponent slainMsg2 = new ChatComponentTranslation("tes.chat.bountyKilled2", highestFaction.factionName());
				slainMsg1.getChatStyle().setColor(EnumChatFormatting.YELLOW);
				slainMsg2.getChatStyle().setColor(EnumChatFormatting.YELLOW);
				slainPlayer.addChatMessage(slainMsg1);
				slainPlayer.addChatMessage(slainMsg2);
			}
			IChatComponent announceMsg = new ChatComponentTranslation("tes.chat.bountyKill", entityplayer.getCommandSenderName(), slainPlayer.getCommandSenderName(), entityFaction.factionName());
			announceMsg.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			for (ICommandSender otherPlayer : (List<EntityPlayer>) MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if (otherPlayer == slainPlayer) {
					continue;
				}
				otherPlayer.addChatMessage(announceMsg);
			}
		}
	}

	@Override
	public void onKilledByPlayer(EntityPlayer entityplayer, EntityPlayer killer) {
		if (!killed && !isFailed() && killer.getUniqueID().equals(targetID)) {
			TESPlayerData pd;
			TESPlayerData killerData = TESLevelData.getData(killer);
			TESFaction killerHighestFaction = getPledgeOrHighestAlignmentFaction(killer, 0.0f);
			if (killerHighestFaction != null) {
				float killerBonus = alignmentBonus;
				TESAlignmentValues.AlignmentBonus source = new TESAlignmentValues.AlignmentBonus(killerBonus, "tes.alignment.killedHunter");
				killerData.addAlignment(killer, source, killerHighestFaction, entityplayer);
			}
			float curAlignment = (pd = playerData).getAlignment(entityFaction);
			if (curAlignment > 100.0f) {
				float alignmentLoss = getKilledAlignmentPenalty();
				if (curAlignment + alignmentLoss < 100.0f) {
					alignmentLoss = -(curAlignment - 100.0f);
				}
				TESAlignmentValues.AlignmentBonus source = new TESAlignmentValues.AlignmentBonus(alignmentLoss, "tes.alignment.killedByBounty");
				pd.addAlignment(entityplayer, source, entityFaction, killer);
				IChatComponent slainMsg1 = new ChatComponentTranslation("tes.chat.killedByBounty1", killer.getCommandSenderName());
				IChatComponent slainMsg2 = new ChatComponentTranslation("tes.chat.killedByBounty2", entityFaction.factionName());
				slainMsg1.getChatStyle().setColor(EnumChatFormatting.YELLOW);
				slainMsg2.getChatStyle().setColor(EnumChatFormatting.YELLOW);
				entityplayer.addChatMessage(slainMsg1);
				entityplayer.addChatMessage(slainMsg2);
			}
			killedByBounty = true;
			playerData.updateMiniQuest(this);
			killerData.addAchievement(TESAchievement.killHuntingPlayer);
			IChatComponent announceMsg = new ChatComponentTranslation("tes.chat.killedByBounty", entityplayer.getCommandSenderName(), killer.getCommandSenderName());
			announceMsg.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			for (ICommandSender otherPlayer : (List<EntityPlayer>) MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if (otherPlayer == entityplayer) {
					continue;
				}
				otherPlayer.addChatMessage(announceMsg);
			}
		}
	}

	@Override
	public void onPlayerTick() {
		if (isActive() && !killed && !bountyClaimedByOther && TESFactionBounties.forFaction(entityFaction).forPlayer(targetID).recentlyBountyKilled()) {
			bountyClaimedByOther = true;
			playerData.updateMiniQuest(this);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("TargetID")) {
			String s = nbt.getString("TargetID");
			targetID = UUID.fromString(s);
		}
		if (nbt.hasKey("TargetName")) {
			targetName = nbt.getString("TargetName");
		}
		killed = nbt.getBoolean("Killed");
		alignmentBonus = nbt.hasKey("Alignment") ? nbt.getInteger("Alignment") : nbt.getFloat("AlignF");
		coinBonus = nbt.getInteger("Coins");
		bountyClaimedByOther = nbt.getBoolean("BountyClaimed");
		killedByBounty = nbt.getBoolean("KilledBy");
	}

	@Override
	public boolean shouldRandomiseCoinReward() {
		return false;
	}

	@Override
	public void start(EntityPlayer entityplayer, TESEntityNPC npc) {
		super.start(entityplayer, npc);
		TESLevelData.getData(targetID).placeBountyFor(npc.getFaction());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (targetID != null) {
			nbt.setString("TargetID", targetID.toString());
		}
		if (targetName != null) {
			nbt.setString("TargetName", targetName);
		}
		nbt.setBoolean("Killed", killed);
		nbt.setFloat("AlignF", alignmentBonus);
		nbt.setInteger("Coins", coinBonus);
		nbt.setBoolean("BountyClaimed", bountyClaimedByOther);
		nbt.setBoolean("KilledBy", killedByBounty);
	}

	public UUID getTargetID() {
		return targetID;
	}

	protected void setTargetID(UUID targetID) {
		this.targetID = targetID;
	}

	public String getTargetName() {
		return targetName;
	}

	protected void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public boolean isKilled() {
		return killed;
	}

	public enum BountyHelp {
		BIOME("biome"), WAYPOINT("wp");

		private final String speechName;

		BountyHelp(String s) {
			speechName = s;
		}

		public static BountyHelp getRandomHelpType(Random random) {
			return values()[random.nextInt(values().length)];
		}

		public String getSpeechName() {
			return speechName;
		}
	}

	public static class QFBounty<Q extends TESMiniQuestBounty> extends TESMiniQuest.QuestFactoryBase<Q> {
		public QFBounty() {
			super("bounty");
		}

		@Override
		public Q createQuest(TESEntityNPC npc, Random rand) {
			if (!TESConfig.allowBountyQuests) {
				return null;
			}
			Q quest = super.createQuest(npc, rand);
			TESFaction faction = npc.getFaction();
			TESFactionBounties bounties = TESFactionBounties.forFaction(faction);
			List<TESFactionBounties.PlayerData> players = bounties.findBountyTargets(25);
			if (players.isEmpty()) {
				return null;
			}
			TESFactionBounties.PlayerData targetData = players.get(rand.nextInt(players.size()));
			int alignment = (int) (float) targetData.getNumKills();
			alignment = MathHelper.clamp_int(alignment, 1, 50);
			int coins = (int) (targetData.getNumKills() * 10.0f * MathHelper.randomFloatClamp(rand, 0.75f, 1.25f));
			coins = MathHelper.clamp_int(coins, 1, 1000);
			quest.setTargetID(targetData.getPlayerID());
			String username = targetData.findUsername();
			if (StringUtils.isBlank(username)) {
				username = quest.getTargetID().toString();
			}
			quest.setTargetName(username);
			quest.setAlignmentBonus(alignment);
			quest.setCoinBonus(coins);
			return quest;
		}

		@Override
		public Class<Q> getQuestClass() {
			return (Class<Q>) TESMiniQuestBounty.class;
		}
	}
}