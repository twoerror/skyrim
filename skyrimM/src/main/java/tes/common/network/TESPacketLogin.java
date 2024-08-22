package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.EnumDifficulty;

public class TESPacketLogin implements IMessage {
	private EnumDifficulty difficulty;
	private boolean difficultyLocked;
	private boolean alignmentZones;
	private boolean feastMode;
	private boolean fellowshipCreation;
	private boolean enchanting;
	private boolean enchantingTES;
	private boolean strictFactionTitleRequirements;
	private boolean conquestDecay;
	private int swordPortalX;
	private int swordPortalY;
	private int swordPortalZ;
	private int ftCooldownMax;
	private int ftCooldownMin;
	private int fellowshipMaxSize;
	private int customWaypointMinY;

	@Override
	public void fromBytes(ByteBuf data) {
		swordPortalX = data.readInt();
		swordPortalY = data.readInt();
		swordPortalZ = data.readInt();
		ftCooldownMax = data.readInt();
		ftCooldownMin = data.readInt();
		byte diff = data.readByte();
		difficulty = diff >= 0 ? EnumDifficulty.getDifficultyEnum(diff) : null;
		difficultyLocked = data.readBoolean();
		alignmentZones = data.readBoolean();
		feastMode = data.readBoolean();
		fellowshipCreation = data.readBoolean();
		fellowshipMaxSize = data.readInt();
		enchanting = data.readBoolean();
		enchantingTES = data.readBoolean();
		strictFactionTitleRequirements = data.readBoolean();
		conquestDecay = data.readBoolean();
		customWaypointMinY = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(swordPortalX);
		data.writeInt(swordPortalY);
		data.writeInt(swordPortalZ);
		data.writeInt(ftCooldownMax);
		data.writeInt(ftCooldownMin);
		int diff = difficulty == null ? -1 : difficulty.getDifficultyId();
		data.writeByte(diff);
		data.writeBoolean(difficultyLocked);
		data.writeBoolean(alignmentZones);
		data.writeBoolean(feastMode);
		data.writeBoolean(fellowshipCreation);
		data.writeInt(fellowshipMaxSize);
		data.writeBoolean(enchanting);
		data.writeBoolean(enchantingTES);
		data.writeBoolean(strictFactionTitleRequirements);
		data.writeBoolean(conquestDecay);
		data.writeInt(customWaypointMinY);
	}

	public void setSwordPortalX(int swordPortalX) {
		this.swordPortalX = swordPortalX;
	}

	public void setSwordPortalY(int swordPortalY) {
		this.swordPortalY = swordPortalY;
	}

	public void setSwordPortalZ(int swordPortalZ) {
		this.swordPortalZ = swordPortalZ;
	}

	public void setFtCooldownMax(int ftCooldownMax) {
		this.ftCooldownMax = ftCooldownMax;
	}

	public void setFtCooldownMin(int ftCooldownMin) {
		this.ftCooldownMin = ftCooldownMin;
	}

	public void setDifficulty(EnumDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void setDifficultyLocked(boolean difficultyLocked) {
		this.difficultyLocked = difficultyLocked;
	}

	public void setAlignmentZones(boolean alignmentZones) {
		this.alignmentZones = alignmentZones;
	}

	public void setFeastMode(boolean feastMode) {
		this.feastMode = feastMode;
	}

	public void setFellowshipCreation(boolean fellowshipCreation) {
		this.fellowshipCreation = fellowshipCreation;
	}

	public void setFellowshipMaxSize(int fellowshipMaxSize) {
		this.fellowshipMaxSize = fellowshipMaxSize;
	}

	public void setEnchanting(boolean enchanting) {
		this.enchanting = enchanting;
	}

	public void setEnchantingTES(boolean enchantingTES) {
		this.enchantingTES = enchantingTES;
	}

	public void setStrictFactionTitleRequirements(boolean strictFactionTitleRequirements) {
		this.strictFactionTitleRequirements = strictFactionTitleRequirements;
	}

	public void setCustomWaypointMinY(int customWaypointMinY) {
		this.customWaypointMinY = customWaypointMinY;
	}

	public static class Handler implements IMessageHandler<TESPacketLogin, IMessage> {
		@Override
		public IMessage onMessage(TESPacketLogin packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				TESLevelData.destroyAllPlayerData();
			}
			TESLevelData.setGameOfThronesPortalX(packet.swordPortalX);
			TESLevelData.setGameOfThronesPortalY(packet.swordPortalY);
			TESLevelData.setGameOfThronesPortalZ(packet.swordPortalZ);
			TESLevelData.setWaypointCooldown(packet.ftCooldownMax, packet.ftCooldownMin);
			EnumDifficulty diff = packet.difficulty;
			if (diff != null) {
				TESLevelData.setSavedDifficulty(diff);
				TES.proxy.setClientDifficulty(diff);
			} else {
				TESLevelData.setSavedDifficulty(null);
			}
			TESLevelData.setDifficultyLocked(packet.difficultyLocked);
			TESLevelData.setEnableAlignmentZones(packet.alignmentZones);
			TESLevelData.setClientSideThisServerFeastMode(packet.feastMode);
			TESLevelData.setClientSideThisServerFellowshipCreation(packet.fellowshipCreation);
			TESLevelData.setClientSideThisServerFellowshipMaxSize(packet.fellowshipMaxSize);
			TESLevelData.setClientSideThisServerEnchanting(packet.enchanting);
			TESLevelData.setClientSideThisServerEnchantingTES(packet.enchantingTES);
			TESLevelData.setClientSideThisServerStrictFactionTitleRequirements(packet.strictFactionTitleRequirements);
			TESLevelData.setClientSideThisServerCustomWaypointMinY(packet.customWaypointMinY);
			return null;
		}
	}
}