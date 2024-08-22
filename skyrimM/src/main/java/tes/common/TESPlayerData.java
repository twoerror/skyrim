package tes.common;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.TES;
import tes.common.block.table.TESBlockCraftingTable;
import tes.common.command.TESCommandAdminHideMap;
import tes.common.database.*;
import tes.common.entity.dragon.TESEntityDragon;

import tes.common.entity.other.TESEntityNPC;
import tes.common.faction.*;
import tes.common.fellowship.*;
import tes.common.item.other.TESItemArmor;
import tes.common.item.weapon.TESItemCrossbowBolt;
import tes.common.network.*;
import tes.common.quest.TESMiniQuest;
import tes.common.quest.TESMiniQuestEvent;
import tes.common.quest.TESMiniQuestSelector;
import tes.common.quest.TESMiniQuestWelcome;
import tes.common.util.TESCrashHandler;
import tes.common.util.TESLog;
import tes.common.world.TESWorldProvider;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.*;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Stream;

public class TESPlayerData {
	private static final int TICKS_UNTIL_FT_MAX = 200;

	private final Collection<TESFaction> bountiesPlaced = new ArrayList<>();
	private final Collection<CWPSharedKey> cwpSharedHidden = new HashSet<>();
	private final Collection<CWPSharedKey> cwpSharedUnlocked = new HashSet<>();
	private final Collection<TESFellowshipInvite> fellowshipInvites = new ArrayList<>();

	private final List<TESMiniQuest> miniQuests = new ArrayList<>();
	private final List<TESMiniQuest> miniQuestsCompleted = new ArrayList<>();
	private final List<TESAchievement> achievements = new ArrayList<>();
	private final List<TESCustomWaypoint> customWaypoints = new ArrayList<>();
	private final List<TESCustomWaypoint> customWaypointsShared = new ArrayList<>();
	private final List<TESFellowshipClient> fellowshipInvitesClient = new ArrayList<>();
	private final List<TESFellowshipClient> fellowshipsClient = new ArrayList<>();
	private final List<UUID> fellowshipIDs = new ArrayList<>();

	private final Map<TESDimension.DimensionRegion, TESFaction> prevRegionFactions = new EnumMap<>(TESDimension.DimensionRegion.class);
	private final Map<TESFaction, Float> alignments = new EnumMap<>(TESFaction.class);
	private final Map<TESFaction, TESFactionData> factionDataMap = new EnumMap<>(TESFaction.class);
	private final Map<TESGuiMessageTypes, Boolean> sentMessageTypes = new EnumMap<>(TESGuiMessageTypes.class);
	private final Map<TESWaypoint, Integer> wpUseCounts = new EnumMap<>(TESWaypoint.class);
	private final Map<CWPSharedKey, Integer> cwpSharedUseCounts = new HashMap<>();
	private final Map<Integer, Integer> cwpUseCounts = new HashMap<>();

	private final Set<TESFaction> takenAlignmentRewards = EnumSet.noneOf(TESFaction.class);
	private final Set<TESWaypoint.Region> unlockedFTRegions = EnumSet.noneOf(TESWaypoint.Region.class);

	private final TESPlayerQuestData questData = new TESPlayerQuestData(this);
	private final UUID playerUUID;

	private TESCapes cape;
	private TESFaction brokenPledgeFaction;
	private TESFaction pledgeFaction;
	private TESFaction viewingFaction;
	private TESShields shield;
	private TESWaypoint lastWaypoint;

	private TESAbstractWaypoint targetFTWaypoint;
	private TESBiome lastBiome;

	private ChunkCoordinates deathPoint;
	private TESTitle.PlayerTitle playerTitle;
	private UUID chatBoundFellowshipID;
	private UUID trackingMiniQuestID;
	private UUID uuidToMount;

	private boolean adminHideMap;
	private boolean askedForJaqen;
	private boolean checkedMenu;
	private boolean conquestKills = true;
	private boolean feminineRanks;
	private boolean friendlyFire;
	private boolean hideAlignment;
	private boolean hideOnMap;
	private boolean hiredDeathMessages = true;
	private boolean needsSave;
	private boolean showCustomWaypoints = true;
	private boolean showHiddenSharedWaypoints = true;
	private boolean showWaypoints = true;
	private boolean structuresBanned;
	private boolean tableSwitched;
	private boolean teleportedKW;

	private int alcoholTolerance;
	private int balance;
	private int completedBountyQuests;
	private int completedMiniquestCount;
	private int deathDim;
	private int ftSinceTick;
	private int nextCwpID = 20000;
	private int pdTick;
	private int pledgeBreakCooldown;
	private int pledgeBreakCooldownStart;
	private int pledgeKillCooldown;
	private int siegeActiveTime;
	private int ticksUntilFT;
	private int uuidToMountTime;

	private long lastOnlineTime = -1L;

	public TESPlayerData(UUID uuid) {
		playerUUID = uuid;
		viewingFaction = TESFaction.EMPIRE;
		ftSinceTick = TESLevelData.getWaypointCooldownMax() * 20;
	}

	private static ItemArmor.ArmorMaterial getBodyMaterial(EntityLivingBase entity) {
		ItemStack item = entity.getEquipmentInSlot(3);
		if (item == null || !(item.getItem() instanceof TESItemArmor)) {
			return null;
		}
		return ((ItemArmor) item.getItem()).getArmorMaterial();
	}

	private static ItemArmor.ArmorMaterial getFullArmorMaterial(EntityLivingBase entity) {
		ItemArmor.ArmorMaterial material = null;
		for (int i = 1; i <= 4; ++i) {
			ItemStack item = entity.getEquipmentInSlot(i);
			if (item == null || !(item.getItem() instanceof ItemArmor)) {
				return null;
			}
			ItemArmor.ArmorMaterial itemMaterial = ((ItemArmor) item.getItem()).getArmorMaterial();
			if (material != null && itemMaterial != material) {
				return null;
			}
			material = itemMaterial;
		}
		return material;
	}

	private static ItemArmor.ArmorMaterial getFullArmorMaterialWithoutHelmet(EntityLivingBase entity) {
		ItemArmor.ArmorMaterial material = null;
		for (int i = 1; i <= 3; ++i) {
			ItemStack item = entity.getEquipmentInSlot(i);
			if (item == null || !(item.getItem() instanceof TESItemArmor)) {
				return null;
			}
			ItemArmor.ArmorMaterial itemMaterial = ((ItemArmor) item.getItem()).getArmorMaterial();
			if (material != null && itemMaterial != material) {
				return null;
			}
			material = itemMaterial;
		}
		return material;
	}

	private static ItemArmor.ArmorMaterial getHelmetMaterial(EntityLivingBase entity) {
		ItemStack item = entity.getEquipmentInSlot(4);
		if (item == null || !(item.getItem() instanceof TESItemArmor)) {
			return null;
		}
		return ((ItemArmor) item.getItem()).getArmorMaterial();
	}

	private static boolean isTimerAutosaveTick() {
		return MinecraftServer.getServer() != null && MinecraftServer.getServer().getTickCounter() % 200 == 0;
	}

	public static void customWaypointAddSharedFellowshipClient(TESCustomWaypoint waypoint, TESFellowshipClient fs) {
		waypoint.addSharedFellowship(fs.getFellowshipID());
	}

	public static void customWaypointRemoveSharedFellowshipClient(TESCustomWaypoint waypoint, UUID fsID) {
		waypoint.removeSharedFellowship(fsID);
	}

	private static boolean doesFactionPreventPledge(TESFaction pledgeFac, TESFaction otherFac) {
		return pledgeFac.isMortalEnemy(otherFac);
	}

	private static <T extends EntityLiving> T fastTravelEntity(WorldServer world, T entity, int i, int j, int k) {
		String entityID = EntityList.getEntityString(entity);
		NBTTagCompound nbt = new NBTTagCompound();
		entity.writeToNBT(nbt);
		entity.setDead();
		EntityLiving entityLiving = (EntityLiving) EntityList.createEntityByName(entityID, world);
		entityLiving.readFromNBT(nbt);
		entityLiving.setLocationAndAngles(i + 0.5D, j, k + 0.5D, entityLiving.rotationYaw, entityLiving.rotationPitch);
		entityLiving.fallDistance = 0.0F;
		entityLiving.getNavigator().clearPathEntity();
		entityLiving.setAttackTarget(null);
		world.spawnEntityInWorld(entityLiving);
		world.updateEntityWithOptionalForce(entityLiving, false);
		return (T) entityLiving;
	}

	private static long getCurrentOnlineTime() {
		return MinecraftServer.getServer().worldServerForDimension(0).getTotalWorldTime();
	}

	private static EntityPlayer getOtherPlayer(UUID uuid) {
		for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
			EntityPlayer entityplayer = worldServer.func_152378_a(uuid);
			if (entityplayer != null) {
				return entityplayer;
			}
		}
		return null;
	}

	public static void renameCustomWaypointClient(TESCustomWaypoint waypoint, String newName) {
		waypoint.rename(newName);
	}

	private static void sendAchievementPacket(EntityPlayerMP entityplayer, TESAchievement achievement, boolean display) {
		IMessage packet = new TESPacketAchievement(achievement, display);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	private static void sendAchievementRemovePacket(EntityPlayerMP entityplayer, TESAchievement achievement) {
		IMessage packet = new TESPacketAchievementRemove(achievement);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	private static void sendFTBouncePacket(EntityPlayerMP entityplayer) {
		IMessage packet = new TESPacketFTBounceClient();
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	private static void sendFTPacket(EntityPlayerMP entityplayer, TESAbstractWaypoint waypoint, int startX, int startZ) {
		IMessage packet = new TESPacketFTScreen(waypoint, startX, startZ);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	private static void sendMiniQuestPacket(EntityPlayerMP entityplayer, TESMiniQuest quest, boolean completed) {
		NBTTagCompound nbt = new NBTTagCompound();
		quest.writeToNBT(nbt);
		IMessage packet = new TESPacketMiniquest(nbt, completed);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	public void setShowWaypoints(boolean flag) {
		showWaypoints = flag;
		markDirty();
	}

	public void setShowHiddenSharedWaypoints(boolean flag) {
		showHiddenSharedWaypoints = flag;
		markDirty();
	}

	public void setShowCustomWaypoints(boolean flag) {
		showCustomWaypoints = flag;
		markDirty();
	}

	public void setCheckedMenu(boolean flag) {
		if (checkedMenu != flag) {
			checkedMenu = flag;
			markDirty();
		}
	}

	public void setTrackingMiniQuestID(UUID npcID) {
		trackingMiniQuestID = npcID;
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketMiniquestTrackClient(trackingMiniQuestID);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public void setChatBoundFellowshipID(UUID fsID) {
		chatBoundFellowshipID = fsID;
		markDirty();
	}

	private void setTicksUntilFT(int i) {
		if (ticksUntilFT != i) {
			ticksUntilFT = i;
			if (ticksUntilFT == TICKS_UNTIL_FT_MAX || ticksUntilFT == 0) {
				markDirty();
			}
		}
	}

	public void acceptFellowshipInvite(TESFellowship fs, boolean respectSizeLimit) {
		UUID fsID = fs.getFellowshipID();
		TESFellowshipInvite existingInvite = null;
		for (TESFellowshipInvite invite : fellowshipInvites) {
			if (invite.getFellowshipID().equals(fsID)) {
				existingInvite = invite;
				break;
			}
		}
		if (existingInvite != null) {
			EntityPlayer entityplayer = getPlayer();
			if (fs.isDisbanded()) {
				rejectFellowshipInvite(fs);
				if (entityplayer != null && !entityplayer.worldObj.isRemote) {
					IMessage resultPacket = new TESPacketFellowshipAcceptInviteResult(fs, TESPacketFellowshipAcceptInviteResult.AcceptInviteResult.DISBANDED);
					TESPacketHandler.NETWORK_WRAPPER.sendTo(resultPacket, (EntityPlayerMP) entityplayer);
				}
			} else {
				int limit = TESConfig.fellowshipMaxSize;
				if (respectSizeLimit && limit >= 0 && fs.getPlayerCount() >= limit) {
					rejectFellowshipInvite(fs);
					if (entityplayer != null && !entityplayer.worldObj.isRemote) {
						IMessage resultPacket = new TESPacketFellowshipAcceptInviteResult(fs, TESPacketFellowshipAcceptInviteResult.AcceptInviteResult.TOO_LARGE);
						TESPacketHandler.NETWORK_WRAPPER.sendTo(resultPacket, (EntityPlayerMP) entityplayer);
					}
				} else {
					fs.addMember(playerUUID);
					fellowshipInvites.remove(existingInvite);
					markDirty();
					sendFellowshipInviteRemovePacket(fs);
					if (entityplayer != null && !entityplayer.worldObj.isRemote) {
						IMessage resultPacket = new TESPacketFellowshipAcceptInviteResult(fs, TESPacketFellowshipAcceptInviteResult.AcceptInviteResult.JOINED);
						TESPacketHandler.NETWORK_WRAPPER.sendTo(resultPacket, (EntityPlayerMP) entityplayer);
						UUID inviterID = existingInvite.getInviterID();
						if (inviterID == null) {
							inviterID = fs.getOwner();
						}
						EntityPlayer inviter = getOtherPlayer(inviterID);
						if (inviter != null) {
							TESFellowship.sendNotification(inviter, "TES.gui.fellowships.notifyAccept", entityplayer.getCommandSenderName());
						}
					}
				}
			}
		}
	}

	public void addAchievement(TESAchievement achievement) {
		TESAchievement TESAchievement = achievement;
		while (true) {
			if (hasAchievement(TESAchievement) || isSiegeActive()) {
				return;
			}
			achievements.add(TESAchievement);
			markDirty();
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				boolean canEarn = TESAchievement.canPlayerEarn(entityplayer);
				sendAchievementPacket((EntityPlayerMP) entityplayer, TESAchievement, canEarn);
				if (canEarn) {
					TESAchievement.broadcastEarning(entityplayer);
					List<TESAchievement> earnedAchievements = getEarnedAchievements(TESDimension.GAME_OF_THRONES);
					int biomes = 0;
					for (TESAchievement earnedAchievement : earnedAchievements) {
						if (earnedAchievement.isBiomeAchievement()) {
							biomes++;
						}
					}
					if (biomes >= 20) {
						addAchievement(TESAchievement.travel20);
					}
					if (biomes >= 40) {
						addAchievement(TESAchievement.travel40);
					}
					if (biomes >= 60) {
						addAchievement(TESAchievement.travel60);
					}
					if (biomes >= 80) {
						addAchievement(TESAchievement.travel80);
					}
					if (biomes >= 100) {
						TESAchievement = TESAchievement.travel100;
						continue;
					}
				}
			}
			return;
		}
	}

	private TESAlignmentBonusMap addAlignment(EntityPlayer entityplayer, TESAlignmentValues.AlignmentBonus source, TESFaction faction, Collection<TESFaction> forcedBonusFactions, double posX, double posY, double posZ) {
		float bonus = source.getBonus();
		TESAlignmentBonusMap factionBonusMap = new TESAlignmentBonusMap();
		float prevMainAlignment = getAlignment(faction);
		float conquestBonus = 0.0F;
		if (source.isKill()) {
			List<TESFaction> killBonuses = faction.getBonusesForKilling();
			for (TESFaction bonusFaction : killBonuses) {
				if (bonusFaction.isPlayableAlignmentFaction() && (bonusFaction.isApprovesWarCrimes() || !source.isCivilianKill())) {
					if (!source.isKillByHiredUnit()) {
						float mplier;
						if (forcedBonusFactions != null && forcedBonusFactions.contains(bonusFaction)) {
							mplier = 1.0F;
						} else {
							mplier = bonusFaction.getControlZoneAlignmentMultiplier(entityplayer);
						}
						if (mplier > 0.0F) {
							float alignment = getAlignment(bonusFaction);
							float factionBonus = Math.abs(bonus);
							factionBonus *= mplier;
							if (alignment >= bonusFaction.getPledgeAlignment() && !isPledgedTo(bonusFaction)) {
								factionBonus *= 0.5F;
							}
							factionBonus = checkBonusForPledgeEnemyLimit(bonusFaction, factionBonus);
							alignment += factionBonus;
							setAlignment(bonusFaction, alignment);
							factionBonusMap.put(bonusFaction, factionBonus);
						}
					}
					if (bonusFaction == pledgeFaction) {
						float conq = bonus;
						if (source.isKillByHiredUnit()) {
							conq *= 0.25F;
						}
						conquestBonus = TESConquestGrid.onConquestKill(entityplayer, bonusFaction, faction, conq);
						getFactionData(bonusFaction).addConquest(Math.abs(conquestBonus));
					}
				}
			}
			List<TESFaction> killPenalties = faction.getPenaltiesForKilling();
			for (TESFaction penaltyFaction : killPenalties) {
				if (penaltyFaction.isPlayableAlignmentFaction() && !source.isKillByHiredUnit()) {
					float mplier;
					if (penaltyFaction == faction) {
						mplier = 1.0F;
					} else {
						mplier = penaltyFaction.getControlZoneAlignmentMultiplier(entityplayer);
					}
					if (mplier > 0.0F) {
						float alignment = getAlignment(penaltyFaction);
						float factionPenalty = -Math.abs(bonus);
						factionPenalty *= mplier;
						factionPenalty = TESAlignmentValues.AlignmentBonus.scalePenalty(factionPenalty, alignment);
						alignment += factionPenalty;
						setAlignment(penaltyFaction, alignment);
						factionBonusMap.put(penaltyFaction, factionPenalty);
					}
				}
			}
		} else if (faction.isPlayableAlignmentFaction()) {
			float alignment = getAlignment(faction);
			float factionBonus = bonus;
			if (factionBonus > 0.0F && alignment >= faction.getPledgeAlignment() && !isPledgedTo(faction)) {
				factionBonus *= 0.5F;
			}
			factionBonus = checkBonusForPledgeEnemyLimit(faction, factionBonus);
			alignment += factionBonus;
			setAlignment(faction, alignment);
			factionBonusMap.put(faction, factionBonus);
		}
		if (!factionBonusMap.isEmpty() || conquestBonus != 0.0F) {
			sendAlignmentBonusPacket(source, faction, prevMainAlignment, factionBonusMap, conquestBonus, posX, posY, posZ);
		}
		return factionBonusMap;
	}

	public TESAlignmentBonusMap addAlignment(EntityPlayer entityplayer, TESAlignmentValues.AlignmentBonus source, TESFaction faction, Collection<TESFaction> forcedBonusFactions, Entity entity) {
		return addAlignment(entityplayer, source, faction, forcedBonusFactions, entity.posX, entity.boundingBox.minY + entity.height * 0.7D, entity.posZ);
	}

	public void addAlignment(EntityPlayer entityplayer, TESAlignmentValues.AlignmentBonus source, TESFaction faction, double posX, double posY, double posZ) {
		addAlignment(entityplayer, source, faction, null, posX, posY, posZ);
	}

	public TESAlignmentBonusMap addAlignment(EntityPlayer entityplayer, TESAlignmentValues.AlignmentBonus source, TESFaction faction, Entity entity) {
		return addAlignment(entityplayer, source, faction, null, entity);
	}

	public void addAlignmentFromCommand(TESFaction faction, float add) {
		float alignment = getAlignment(faction);
		alignment += add;
		setAlignment(faction, alignment);
	}

	public void addCompletedBountyQuest() {
		completedBountyQuests++;
		markDirty();
	}

	public void addCustomWaypoint(TESCustomWaypoint waypoint) {
		customWaypoints.add(waypoint);
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketCreateCWPClient packet = waypoint.getClientPacket();
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			TESCustomWaypointLogger.logCreate(entityplayer, waypoint);
		}
		TESCustomWaypoint shareCopy = waypoint.createCopyOfShared(playerUUID);
		List<UUID> sharedPlayers = shareCopy.getPlayersInAllSharedFellowships();
		for (UUID sharedPlayerUUID : sharedPlayers) {
			EntityPlayer sharedPlayer = getOtherPlayer(sharedPlayerUUID);
			if (sharedPlayer != null && !sharedPlayer.worldObj.isRemote) {
				TESLevelData.getData(sharedPlayerUUID).addOrUpdateSharedCustomWaypoint(shareCopy);
			}
		}
	}

	public void addCustomWaypointClient(TESCustomWaypoint waypoint) {
		customWaypoints.add(waypoint);
	}

	public void addFellowship(TESFellowship fs) {
		if (fs.containsPlayer(playerUUID)) {
			UUID fsID = fs.getFellowshipID();
			if (!fellowshipIDs.contains(fsID)) {
				fellowshipIDs.add(fsID);
				markDirty();
				sendFellowshipPacket(fs);
				addSharedCustomWaypointsFromAllIn(fs.getFellowshipID());
			}
		}
	}

	private void addFellowshipInvite(TESFellowship fs, UUID inviterUUID, String inviterUsername) {
		UUID fsID = fs.getFellowshipID();
		boolean hasInviteAlready = false;
		for (TESFellowshipInvite invite : fellowshipInvites) {
			if (invite.getFellowshipID().equals(fsID)) {
				hasInviteAlready = true;
				break;
			}
		}
		if (!hasInviteAlready) {
			fellowshipInvites.add(new TESFellowshipInvite(fsID, inviterUUID));
			markDirty();
			sendFellowshipInvitePacket(fs);
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				TESFellowship.sendNotification(entityplayer, "TES.gui.fellowships.notifyInvite", inviterUsername);
			}
		}
	}

	public void addMiniQuest(TESMiniQuest quest) {
		miniQuests.add(quest);
		updateMiniQuest(quest);
	}

	public void addMiniQuestCompleted(TESMiniQuest quest) {
		miniQuestsCompleted.add(quest);
		markDirty();
	}

	public void addOrUpdateClientFellowship(TESFellowshipClient fs) {
		UUID fsID = fs.getFellowshipID();
		TESFellowshipClient inList = null;
		for (TESFellowshipClient fsInList : fellowshipsClient) {
			if (fsInList.getFellowshipID().equals(fsID)) {
				inList = fsInList;
				break;
			}
		}
		if (inList != null) {
			inList.updateDataFrom(fs);
		} else {
			fellowshipsClient.add(fs);
		}
	}

	public void addOrUpdateClientFellowshipInvite(TESFellowshipClient fs) {
		UUID fsID = fs.getFellowshipID();
		TESFellowshipClient inList = null;
		for (TESFellowshipClient fsInList : fellowshipInvitesClient) {
			if (fsInList.getFellowshipID().equals(fsID)) {
				inList = fsInList;
				break;
			}
		}
		if (inList != null) {
			inList.updateDataFrom(fs);
		} else {
			fellowshipInvitesClient.add(fs);
		}
	}

	public void addOrUpdateSharedCustomWaypoint(TESCustomWaypoint waypoint) {
		if (!waypoint.isShared()) {
			FMLLog.warning("Hummel009: Warning! Tried to cache a shared custom waypoint with no owner!");
			return;
		}
		if (waypoint.getSharingPlayerID().equals(playerUUID)) {
			FMLLog.warning("Hummel009: Warning! Tried to share a custom waypoint to its own player (%s)!", playerUUID.toString());
			return;
		}
		CWPSharedKey key = CWPSharedKey.keyFor(waypoint.getSharingPlayerID(), waypoint.getID());
		if (cwpSharedUnlocked.contains(key)) {
			waypoint.setSharedUnlocked();
		}
		waypoint.setSharedHidden(cwpSharedHidden.contains(key));
		TESCustomWaypoint existing = getSharedCustomWaypointByID(waypoint.getSharingPlayerID(), waypoint.getID());
		if (existing == null) {
			customWaypointsShared.add(waypoint);
		} else {
			if (existing.isSharedUnlocked()) {
				waypoint.setSharedUnlocked();
			}
			waypoint.setSharedHidden(existing.isSharedHidden());
			int i = customWaypointsShared.indexOf(existing);
			customWaypointsShared.set(i, waypoint);
		}
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketCreateCWPClient packet = waypoint.getClientPacketShared();
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	private void addSharedCustomWaypointsFrom(UUID onlyOneFellowshipID, Iterable<UUID> checkSpecificPlayers) {
		List<UUID> checkFellowshipIDs;
		if (onlyOneFellowshipID != null) {
			checkFellowshipIDs = new ArrayList<>();
			checkFellowshipIDs.add(onlyOneFellowshipID);
		} else {
			checkFellowshipIDs = fellowshipIDs;
		}
		Collection<UUID> checkFellowPlayerIDs = new ArrayList<>();
		if (checkSpecificPlayers != null) {
			for (UUID player : checkSpecificPlayers) {
				if (!player.equals(playerUUID)) {
					checkFellowPlayerIDs.add(player);
				}
			}
		} else {
			for (UUID fsID : checkFellowshipIDs) {
				TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
				if (fs != null) {
					Set<UUID> playerIDs = fs.getWaypointSharerUUIDs();
					for (UUID player : playerIDs) {
						if (!player.equals(playerUUID) && !checkFellowPlayerIDs.contains(player)) {
							checkFellowPlayerIDs.add(player);
						}
					}
				}
			}
		}
		for (UUID player : checkFellowPlayerIDs) {
			TESPlayerData pd = TESLevelData.getData(player);
			for (TESCustomWaypoint waypoint : pd.customWaypoints) {
				boolean inSharedFellowship = false;
				for (UUID fsID : checkFellowshipIDs) {
					if (waypoint.hasSharedFellowship(fsID)) {
						inSharedFellowship = true;
						break;
					}
				}
				if (inSharedFellowship) {
					addOrUpdateSharedCustomWaypoint(waypoint.createCopyOfShared(player));
				}
			}
		}
	}

	private void addSharedCustomWaypointsFromAllFellowships() {
		addSharedCustomWaypointsFrom(null, null);
	}

	private void addSharedCustomWaypointsFromAllIn(UUID onlyOneFellowshipID) {
		addSharedCustomWaypointsFrom(onlyOneFellowshipID, null);
	}

	public boolean anyMatchingFellowshipNames(String name, boolean client) {
		String name1 = StringUtils.strip(name).toLowerCase(Locale.ROOT);
		if (client) {
			for (TESFellowshipClient fs : fellowshipsClient) {
				String otherName = fs.getName();
				otherName = StringUtils.strip(otherName).toLowerCase(Locale.ROOT);
				if (name1.equals(otherName)) {
					return true;
				}
			}
		} else {
			for (UUID fsID : fellowshipIDs) {
				TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
				if (fs != null) {
					String otherName = fs.getName();
					otherName = StringUtils.strip(otherName).toLowerCase(Locale.ROOT);
					if (name1.equals(otherName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void cancelFastTravel() {
		if (targetFTWaypoint != null) {
			setTargetFTWaypoint(null);
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				entityplayer.addChatMessage(new ChatComponentTranslation("TES.fastTravel.motion"));
			}
		}
	}

	public boolean canCreateFellowships(boolean client) {
		int max = getMaxLeadingFellowships();
		int leading = 0;
		if (client) {
			for (TESFellowshipClient fs : fellowshipsClient) {
				if (fs.isOwned()) {
					leading++;
					if (leading >= max) {
						return false;
					}
				}
			}
		} else {
			for (UUID fsID : fellowshipIDs) {
				TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
				if (fs != null && fs.isOwner(playerUUID)) {
					leading++;
					if (leading >= max) {
						return false;
					}
				}
			}
		}
		return leading < max;
	}

	public boolean canFastTravel() {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null) {
			World world = entityplayer.worldObj;
			if (!entityplayer.capabilities.isCreativeMode) {
				double range = 16.0D;
				List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, entityplayer.boundingBox.expand(range, range, range));
				for (EntityLiving entity : entities) {
					if (entity.getAttackTarget() == entityplayer) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean canMakeNewPledge() {
		return pledgeBreakCooldown <= 0;
	}

	public boolean canPledgeTo(TESFaction fac) {
		return fac.isPlayableAlignmentFaction() && hasPledgeAlignment(fac);
	}

	private void checkAlignmentAchievements(TESFaction faction) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			faction.checkAlignmentAchievements(entityplayer);
		}
	}

	private float checkBonusForPledgeEnemyLimit(TESFaction fac, float bonus) {
		if (isPledgeEnemyAlignmentLimited(fac)) {
			float alignment = getAlignment(fac);
			float limit = 0.0f;
			if (alignment > limit) {
				return 0.0f;
			}
			if (alignment + bonus > limit) {
				return limit - alignment;
			}
		}
		return bonus;
	}

	private void checkCustomWaypointsSharedBy(Collection<UUID> checkSpecificPlayers) {
		Collection<TESCustomWaypoint> removes = new ArrayList<>();
		for (TESCustomWaypoint waypoint : customWaypointsShared) {
			UUID waypointSharingPlayer = waypoint.getSharingPlayerID();
			if (checkSpecificPlayers == null || checkSpecificPlayers.contains(waypointSharingPlayer)) {
				TESCustomWaypoint wpOriginal = TESLevelData.getData(waypointSharingPlayer).getCustomWaypointByID(waypoint.getID());
				if (wpOriginal != null) {
					List<UUID> sharedFellowPlayers = wpOriginal.getPlayersInAllSharedFellowships();
					if (!sharedFellowPlayers.contains(playerUUID)) {
						removes.add(waypoint);
					}
				}
			}
		}
		for (TESCustomWaypoint waypoint : removes) {
			removeSharedCustomWaypoint(waypoint);
		}
	}

	private void checkCustomWaypointsSharedFromFellowships() {
		checkCustomWaypointsSharedBy(null);
	}

	private void checkIfStillWaypointSharerForFellowship(TESFellowship fs) {
		if (!hasAnyWaypointsSharedToFellowship(fs)) {
			fs.markIsWaypointSharer(playerUUID, false);
		}
	}

	public void completeMiniQuest(TESMiniQuest quest) {
		if (miniQuests.remove(quest)) {
			addMiniQuestCompleted(quest);
			completedMiniquestCount++;
			getFactionData(quest.getEntityFaction()).completeMiniQuest();
			markDirty();
			TES.proxy.setTrackedQuest(quest);
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketMiniquestRemove(quest, false, true);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		} else {
			FMLLog.warning("Warning: Attempted to remove a miniquest which does not belong to the player data");
		}
	}

	public void createFellowship(String name, boolean normalCreation) {
		if (normalCreation && (!TESConfig.enableFellowshipCreation || !canCreateFellowships(false))) {
			return;
		}
		if (!anyMatchingFellowshipNames(name, false)) {
			TESFellowship fellowship = new TESFellowship(playerUUID, name);
			fellowship.createAndRegister();
		}
	}

	public void customWaypointAddSharedFellowship(TESCustomWaypoint waypoint, TESFellowship fs) {
		UUID fsID = fs.getFellowshipID();
		waypoint.addSharedFellowship(fsID);
		markDirty();
		fs.markIsWaypointSharer(playerUUID, true);
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketShareCWPClient packet = waypoint.getClientAddFellowshipPacket(fsID);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
		TESCustomWaypoint shareCopy = waypoint.createCopyOfShared(playerUUID);
		for (UUID player : fs.getAllPlayerUUIDs()) {
			if (!player.equals(playerUUID)) {
				TESLevelData.getData(player).addOrUpdateSharedCustomWaypoint(shareCopy);
			}
		}
	}

	public void customWaypointRemoveSharedFellowship(TESCustomWaypoint waypoint, TESFellowship fs) {
		UUID fsID = fs.getFellowshipID();
		waypoint.removeSharedFellowship(fsID);
		markDirty();
		checkIfStillWaypointSharerForFellowship(fs);
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketShareCWPClient packet = waypoint.getClientRemoveFellowshipPacket(fsID);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
		TESCustomWaypoint shareCopy = waypoint.createCopyOfShared(playerUUID);
		for (UUID player : fs.getAllPlayerUUIDs()) {
			if (!player.equals(playerUUID)) {
				TESPlayerData pd = TESLevelData.getData(player);
				pd.addOrUpdateSharedCustomWaypoint(shareCopy);
				pd.checkCustomWaypointsSharedBy(ImmutableList.of(playerUUID));
			}
		}
	}

	public void disbandFellowship(TESFellowship fs, String disbanderUsername) {
		if (fs.isOwner(playerUUID)) {
			Iterable<UUID> memberUUIDs = new ArrayList<>(fs.getMemberUUIDs());
			fs.setDisbandedAndRemoveAllMembers();
			removeFellowship(fs);
			for (UUID memberID : memberUUIDs) {
				EntityPlayer member = getOtherPlayer(memberID);
				if (member != null && !member.worldObj.isRemote) {
					TESFellowship.sendNotification(member, "TES.gui.fellowships.notifyDisband", disbanderUsername);
				}
			}
		}
	}

	public void distributeMQEvent(TESMiniQuestEvent event) {
		for (TESMiniQuest quest : miniQuests) {
			if (quest.isActive()) {
				quest.handleEvent(event);
			}
		}
	}

	private void fastTravelTo(TESAbstractWaypoint waypoint) {
		EntityPlayer player = getPlayer();
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP entityplayer = (EntityPlayerMP) player;
			WorldServer world = (WorldServer) entityplayer.worldObj;
			int startX = MathHelper.floor_double(entityplayer.posX);
			int startZ = MathHelper.floor_double(entityplayer.posZ);
			double range = 256.0D;
			List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, entityplayer.boundingBox.expand(range, range, range));
			Collection<EntityLiving> entitiesToTransport = new HashSet<>();
			for (EntityLiving entity : entities) {
				if (!(entity instanceof TESEntityDragon)) {
					if (entity instanceof TESEntityNPC) {
						TESEntityNPC npc = (TESEntityNPC) entity;
						if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer() == entityplayer && npc.getHireableInfo().shouldFollowPlayer()) {
							entitiesToTransport.add(npc);
							continue;
						}
					}
					if (entity instanceof EntityTameable) {
						EntityTameable pet = (EntityTameable) entity;
						if (pet.getOwner() == entityplayer && !pet.isSitting()) {
							entitiesToTransport.add(pet);
							continue;
						}
					}
					if (entity.getLeashed() && entity.getLeashedToEntity() == entityplayer) {
						entitiesToTransport.add(entity);
					}
				}
			}
			Collection<EntityLiving> removes = new HashSet<>();
			for (EntityLiving entity : entitiesToTransport) {
				Entity rider = entity.riddenByEntity;
				if (rider != null && entitiesToTransport.contains(rider)) {
					removes.add(entity);
				}
			}
			entitiesToTransport.removeAll(removes);
			int i = waypoint.getCoordX();
			int k = waypoint.getCoordZ();
			world.theChunkProviderServer.provideChunk(i >> 4, k >> 4);
			int j = waypoint.getCoordY(world, i, k);
			Entity playerMount = entityplayer.ridingEntity;
			entityplayer.mountEntity(null);
			entityplayer.setPositionAndUpdate(i + 0.5D, j, k + 0.5D);
			entityplayer.fallDistance = 0.0F;
			if (playerMount instanceof EntityLiving) {
				playerMount = fastTravelEntity(world, (EntityLiving) playerMount, i, j, k);
			}
			if (playerMount != null) {
				setUUIDToMount(playerMount.getUniqueID());
			}
			for (EntityLiving entity : entitiesToTransport) {
				Entity mount = entity.ridingEntity;
				entity.mountEntity(null);
				entity = fastTravelEntity(world, entity, i, j, k);
				if (mount instanceof EntityLiving) {
					mount = fastTravelEntity(world, (EntityLiving) mount, i, j, k);
					entity.mountEntity(mount);
				}
			}
			sendFTPacket(entityplayer, waypoint, startX, startZ);
			setTimeSinceFTWithUpdate(0);
			incrementWPUseCount(waypoint);
			if (waypoint instanceof TESWaypoint) {
				lastWaypoint = (TESWaypoint) waypoint;
				markDirty();
			}
			if (waypoint instanceof TESCustomWaypoint) {
				TESCustomWaypointLogger.logTravel(entityplayer, (TESCustomWaypoint) waypoint);
			}
		}
	}

	public List<TESAchievement> getAchievements() {
		return achievements;
	}

	public List<TESMiniQuest> getActiveMiniQuests() {
		return selectMiniQuests(new TESMiniQuestSelector().setActiveOnly());
	}

	public boolean getAdminHideMap() {
		return adminHideMap;
	}

	public void setAdminHideMap(boolean flag) {
		adminHideMap = flag;
		markDirty();
	}

	public int getAlcoholTolerance() {
		return alcoholTolerance;
	}

	public void setAlcoholTolerance(int i) {
		alcoholTolerance = i;
		markDirty();
		if (alcoholTolerance >= 250) {
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				addAchievement(TESAchievement.gainHighAlcoholTolerance);
			}
		}
	}

	public float getAlignment(TESFaction faction) {
		if (faction.isHasFixedAlignment()) {
			return faction.getFixedAlignment();
		}
		Float alignment = alignments.get(faction);
		if (alignment != null) {
			return alignment;
		}
		return 0.0F;
	}

	public List<TESAbstractWaypoint> getAllAvailableWaypoints() {
		List<TESAbstractWaypoint> waypoints = new ArrayList<>(TESWaypoint.listAllWaypoints());
		waypoints.addAll(customWaypoints);
		waypoints.addAll(customWaypointsShared);
		return waypoints;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int b) {
		balance = b;
		markDirty();
	}

	public TESFaction getBrokenPledgeFaction() {
		return brokenPledgeFaction;
	}

	public void setBrokenPledgeFaction(TESFaction f) {
		brokenPledgeFaction = f;
		markDirty();
	}

	public TESCapes getCape() {
		return cape;
	}

	public void setCape(TESCapes cape) {
		this.cape = cape;
		markDirty();
	}

	public TESFellowship getChatBoundFellowship() {
		if (chatBoundFellowshipID != null) {
			return TESFellowshipData.getActiveFellowship(chatBoundFellowshipID);
		}
		return null;
	}

	public void setChatBoundFellowship(TESFellowship fs) {
		setChatBoundFellowshipID(fs.getFellowshipID());
	}

	public TESFellowshipClient getClientFellowshipByID(UUID fsID) {
		for (TESFellowshipClient fs : fellowshipsClient) {
			if (fs.getFellowshipID().equals(fsID)) {
				return fs;
			}
		}
		return null;
	}

	public TESFellowshipClient getClientFellowshipByName(String fsName) {
		for (TESFellowshipClient fs : fellowshipsClient) {
			if (fs.getName().equalsIgnoreCase(fsName)) {
				return fs;
			}
		}
		return null;
	}

	public List<TESFellowshipClient> getClientFellowshipInvites() {
		return fellowshipInvitesClient;
	}

	public List<TESFellowshipClient> getClientFellowships() {
		return fellowshipsClient;
	}

	public int getCompletedBountyQuests() {
		return completedBountyQuests;
	}

	public int getCompletedMiniQuestsTotal() {
		return completedMiniquestCount;
	}

	public TESCustomWaypoint getCustomWaypointByID(int ID) {
		for (TESCustomWaypoint waypoint : customWaypoints) {
			if (waypoint.getID() == ID) {
				return waypoint;
			}
		}
		return null;
	}

	public List<TESCustomWaypoint> getCustomWaypoints() {
		return customWaypoints;
	}

	public int getDeathDimension() {
		return deathDim;
	}

	public void setDeathDimension(int dim) {
		deathDim = dim;
		markDirty();
	}

	public ChunkCoordinates getDeathPoint() {
		return deathPoint;
	}

	public List<TESAchievement> getEarnedAchievements(TESDimension dimension) {
		List<TESAchievement> earnedAchievements = new ArrayList<>();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null) {
			for (TESAchievement achievement : achievements) {
				if (achievement.getDimension() == dimension && achievement.canPlayerEarn(entityplayer)) {
					earnedAchievements.add(achievement);
				}
			}
		}
		return earnedAchievements;
	}

	public boolean getEnableConquestKills() {
		return conquestKills;
	}

	public void setEnableConquestKills(boolean flag) {
		conquestKills = flag;
		markDirty();
		sendOptionsPacket(5, flag);
	}

	public boolean getEnableHiredDeathMessages() {
		return hiredDeathMessages;
	}

	public void setEnableHiredDeathMessages(boolean flag) {
		hiredDeathMessages = flag;
		markDirty();
		sendOptionsPacket(1, flag);
	}

	public TESFactionData getFactionData(TESFaction faction) {
		return factionDataMap.computeIfAbsent(faction, k -> new TESFactionData(this, faction));
	}

	public TESFellowship getFellowshipByName(String fsName) {
		for (UUID fsID : fellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null && fs.getName().equalsIgnoreCase(fsName)) {
				return fs;
			}
		}
		return null;
	}

	public List<UUID> getFellowshipIDs() {
		return fellowshipIDs;
	}

	public List<TESFellowship> getFellowships() {
		List<TESFellowship> fellowships = new ArrayList<>();
		for (UUID fsID : fellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null) {
				fellowships.add(fs);
			}
		}
		return fellowships;
	}

	public boolean getFeminineRanks() {
		return feminineRanks;
	}

	public void setFeminineRanks(boolean flag) {
		feminineRanks = flag;
		markDirty();
		sendOptionsPacket(4, flag);
	}

	public boolean getFriendlyFire() {
		return friendlyFire;
	}

	public void setFriendlyFire(boolean flag) {
		friendlyFire = flag;
		markDirty();
		sendOptionsPacket(0, flag);
	}

	public boolean getHideAlignment() {
		return hideAlignment;
	}

	public void setHideAlignment(boolean flag) {
		hideAlignment = flag;
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESLevelData.sendAlignmentToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
		}
	}

	public boolean getHideMapLocation() {
		return hideOnMap;
	}

	public void setHideMapLocation(boolean flag) {
		hideOnMap = flag;
		markDirty();
		sendOptionsPacket(3, flag);
	}

	public TESBiome getLastKnownBiome() {
		return lastBiome;
	}

	public TESWaypoint getLastKnownWaypoint() {
		return lastWaypoint;
	}

	public int getMaxCustomWaypoints() {
		int achievements = getEarnedAchievements(TESDimension.GAME_OF_THRONES).size();
		return 5 + achievements / 5;
	}

	private int getMaxLeadingFellowships() {
		int achievements = getEarnedAchievements(TESDimension.GAME_OF_THRONES).size();
		return 1 + achievements / 20;
	}

	public TESMiniQuest getMiniQuestForID(UUID id, boolean completed) {
		List<TESMiniQuest> threadSafe;
		if (completed) {
			threadSafe = new ArrayList<>(miniQuestsCompleted);
		} else {
			threadSafe = new ArrayList<>(miniQuests);
		}
		for (TESMiniQuest quest : threadSafe) {
			if (quest.getQuestUUID().equals(id)) {
				return quest;
			}
		}
		return null;
	}

	public List<TESMiniQuest> getMiniQuests() {
		return miniQuests;
	}

	public List<TESMiniQuest> getMiniQuestsCompleted() {
		return miniQuestsCompleted;
	}

	public List<TESMiniQuest> getMiniQuestsForEntity(Entity npc, boolean activeOnly) {
		return getMiniQuestsForEntityID(npc.getUniqueID(), activeOnly);
	}

	private List<TESMiniQuest> getMiniQuestsForEntityID(UUID npcID, boolean activeOnly) {
		TESMiniQuestSelector.EntityId sel = new TESMiniQuestSelector.EntityId(npcID);
		if (activeOnly) {
			sel.setActiveOnly();
		}
		return selectMiniQuests(sel);
	}

	public List<TESMiniQuest> getMiniQuestsForFaction(TESFaction f, boolean activeOnly) {
		TESMiniQuestSelector.Faction sel = new TESMiniQuestSelector.Faction(() -> f);
		if (activeOnly) {
			sel.setActiveOnly();
		}
		return selectMiniQuests(sel);
	}

	public int getNextCwpID() {
		return nextCwpID;
	}

	private EntityPlayer getPlayer() {
		World[] searchWorlds;
		if (TES.proxy.isClient()) {
			searchWorlds = new World[]{TES.proxy.getClientWorld()};
		} else {
			searchWorlds = MinecraftServer.getServer().worldServers;
		}
		for (World world : searchWorlds) {
			EntityPlayer entityplayer = world.func_152378_a(playerUUID);
			if (entityplayer != null) {
				return entityplayer;
			}
		}
		return null;
	}

	public TESTitle.PlayerTitle getPlayerTitle() {
		return playerTitle;
	}

	public void setPlayerTitle(TESTitle.PlayerTitle title) {
		playerTitle = title;
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketTitle(playerTitle);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
		for (UUID fsID : fellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null) {
				fs.updateForAllMembers(new FellowshipUpdateType.UpdatePlayerTitle(playerUUID, playerTitle));
			}
		}
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public int getPledgeBreakCooldown() {
		return pledgeBreakCooldown;
	}

	public void setPledgeBreakCooldown(int i) {
		int preCD = pledgeBreakCooldown;
		TESFaction preBroken = brokenPledgeFaction;
		pledgeBreakCooldown = Math.max(0, i);
		boolean bigChange = (pledgeBreakCooldown == 0 || preCD == 0) && pledgeBreakCooldown != preCD;
		if (pledgeBreakCooldown > pledgeBreakCooldownStart) {
			setPledgeBreakCooldownStart(pledgeBreakCooldown);
			bigChange = true;
		}
		if (pledgeBreakCooldown <= 0 && preBroken != null) {
			setPledgeBreakCooldownStart(0);
			setBrokenPledgeFaction(null);
			bigChange = true;
		}
		if (bigChange || isTimerAutosaveTick()) {
			markDirty();
		}
		if (bigChange || pledgeBreakCooldown % 5 == 0) {
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketBrokenPledge(pledgeBreakCooldown, pledgeBreakCooldownStart, brokenPledgeFaction);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		}
		if (pledgeBreakCooldown == 0 && preCD != pledgeBreakCooldown) {
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				String brokenName;
				if (preBroken == null) {
					brokenName = StatCollector.translateToLocal("TES.gui.factions.pledgeUnknown");
				} else {
					brokenName = preBroken.factionName();
				}
				IChatComponent chatComponentTranslation = new ChatComponentTranslation("TES.chat.pledgeBreakCooldown", brokenName);
				entityplayer.addChatMessage(chatComponentTranslation);
			}
		}
	}

	public int getPledgeBreakCooldownStart() {
		return pledgeBreakCooldownStart;
	}

	public void setPledgeBreakCooldownStart(int i) {
		pledgeBreakCooldownStart = i;
		markDirty();
	}

	public TESFaction getPledgeFaction() {
		return pledgeFaction;
	}

	public void setPledgeFaction(TESFaction fac) {
		pledgeFaction = fac;
		pledgeKillCooldown = 0;
		markDirty();
		if (fac != null) {
			checkAlignmentAchievements(fac);
			addAchievement(TESAchievement.pledgeService);
		}
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			if (fac != null) {
				World world = entityplayer.worldObj;
				world.playSoundAtEntity(entityplayer, "TES:event.pledge", 1.0F, 1.0F);
			}
			IMessage packet = new TESPacketPledge(fac);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public TESPlayerQuestData getQuestData() {
		return questData;
	}

	public TESFaction getRegionLastViewedFaction(TESDimension.DimensionRegion region) {
		return prevRegionFactions.computeIfAbsent(region, key -> {
			TESFaction fac = region.getFactionList().get(0);
			prevRegionFactions.put(region, fac);
			return fac;
		});
	}

	public TESCustomWaypoint getSharedCustomWaypointByID(UUID owner, int id) {
		for (TESCustomWaypoint waypoint : customWaypointsShared) {
			if (waypoint.getSharingPlayerID().equals(owner) && waypoint.getID() == id) {
				return waypoint;
			}
		}
		return null;
	}

	public TESShields getShield() {
		return shield;
	}

	public void setShield(TESShields TESshield) {
		shield = TESshield;
		markDirty();
	}

	public boolean getStructuresBanned() {
		return structuresBanned;
	}

	public void setStructuresBanned(boolean flag) {
		structuresBanned = flag;
		markDirty();
	}

	public boolean getTableSwitched() {
		return tableSwitched;
	}

	public void setTableSwitched(boolean flag) {
		tableSwitched = flag;
		markDirty();
		sendOptionsPacket(9, flag);
	}

	public boolean getTeleportedKW() {
		return teleportedKW;
	}

	public void setTeleportedKW(boolean flag) {
		teleportedKW = flag;
		markDirty();
	}

	public int getTimeSinceFT() {
		return ftSinceTick;
	}

	public void setTimeSinceFT(int i) {
		setTimeSinceFT(i, false);
	}

	public TESMiniQuest getTrackingMiniQuest() {
		if (trackingMiniQuestID == null) {
			return null;
		}
		return getMiniQuestForID(trackingMiniQuestID, false);
	}

	public void setTrackingMiniQuest(TESMiniQuest quest) {
		if (quest == null) {
			setTrackingMiniQuestID(null);
		} else {
			setTrackingMiniQuestID(quest.getQuestUUID());
		}
	}

	public TESFaction getViewingFaction() {
		return viewingFaction;
	}

	public void setViewingFaction(TESFaction faction) {
		if (faction != null) {
			viewingFaction = faction;
			markDirty();
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketUpdateViewingFaction(viewingFaction);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		}
	}

	public int getWaypointFTTime(TESAbstractWaypoint wp, Entity entityplayer) {
		int baseMin = TESLevelData.getWaypointCooldownMin();
		int baseMax = TESLevelData.getWaypointCooldownMax();
		int useCount = getWPUseCount(wp);
		double dist = entityplayer.getDistance(wp.getCoordX() + 0.5D, wp.getCoordYSaved(), wp.getCoordZ() + 0.5D);
		double time = baseMin;
		double added = (baseMax - baseMin) * Math.pow(0.9D, useCount);
		time += added;
		time *= Math.max(1.0D, dist * 1.2E-5D);
		int seconds = (int) Math.round(time);
		seconds = Math.max(seconds, 0);
		return seconds * 20;
	}

	private int getWPUseCount(TESAbstractWaypoint wp) {
		if (wp instanceof TESCustomWaypoint) {
			TESCustomWaypoint cwp = (TESCustomWaypoint) wp;
			int ID = cwp.getID();
			if (cwp.isShared()) {
				UUID sharingPlayer = cwp.getSharingPlayerID();
				CWPSharedKey key = CWPSharedKey.keyFor(sharingPlayer, ID);
				if (cwpSharedUseCounts.containsKey(key)) {
					return cwpSharedUseCounts.get(key);
				}
			} else if (cwpUseCounts.containsKey(ID)) {
				return cwpUseCounts.get(ID);
			}
		} else if (wpUseCounts.containsKey(wp)) {
			return wpUseCounts.get(wp);
		}
		return 0;
	}

	public void givePureConquestBonus(EntityPlayer entityplayer, TESFaction bonusFac, TESFaction enemyFac, float conq, String title, double posX, double posY, double posZ) {
		float conq1 = TESConquestGrid.onConquestKill(entityplayer, bonusFac, enemyFac, conq);
		getFactionData(bonusFac).addConquest(Math.abs(conq1));
		if (conq1 != 0.0F) {
			TESAlignmentValues.AlignmentBonus source = new TESAlignmentValues.AlignmentBonus(0.0F, title);
			IMessage packet = new TESPacketAlignmentBonus(bonusFac, getAlignment(bonusFac), new TESAlignmentBonusMap(), conq1, posX, posY, posZ, source);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public boolean hasAchievement(TESAchievement achievement) {
		for (TESAchievement a : achievements) {
			if (a.getCategory() == achievement.getCategory() && a.getId() == achievement.getId()) {
				return true;
			}
		}
		return false;
	}

	private boolean hasActiveOrCompleteMQType(Class<? extends TESMiniQuest> type) {
		Collection<TESMiniQuest> allQuests = new ArrayList<>();
		for (TESMiniQuest q : miniQuests) {
			if (q.isActive()) {
				allQuests.add(q);
			}
		}
		allQuests.addAll(miniQuestsCompleted);
		for (TESMiniQuest q : allQuests) {
			if (type.isAssignableFrom(q.getClass())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAnyJHQuest() {
		return hasActiveOrCompleteMQType(TESMiniQuestWelcome.class);
	}

	public boolean hasAnyWaypointsSharedToFellowship(TESFellowship fs) {
		for (TESCustomWaypoint waypoint : customWaypoints) {
			if (waypoint.hasSharedFellowship(fs)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPledgeAlignment(TESFaction fac) {
		float alignment = getAlignment(fac);
		return alignment >= fac.getPledgeAlignment();
	}

	public void hideOrUnhideSharedCustomWaypoint(TESCustomWaypoint waypoint, boolean hide) {
		if (!waypoint.isShared()) {
			FMLLog.warning("Hummel009: Warning! Tried to unlock a shared custom waypoint with no owner!");
			return;
		}
		waypoint.setSharedHidden(hide);
		CWPSharedKey key = CWPSharedKey.keyFor(waypoint.getSharingPlayerID(), waypoint.getID());
		if (hide) {
			cwpSharedHidden.add(key);
		} else {
			cwpSharedHidden.remove(key);
		}
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketCWPSharedHideClient packet = waypoint.getClientSharedHidePacket(hide);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public void incrementNextCwpID() {
		nextCwpID++;
		markDirty();
	}

	private void incrementWPUseCount(TESAbstractWaypoint wp) {
		setWPUseCount(wp, getWPUseCount(wp) + 1);
	}

	public void invitePlayerToFellowship(TESFellowship fs, UUID invitedPlayerUUID, String inviterUsername) {
		if (fs.isOwner(playerUUID) || fs.isAdmin(playerUUID)) {
			TESLevelData.getData(invitedPlayerUUID).addFellowshipInvite(fs, playerUUID, inviterUsername);
		}
	}

	public boolean isFTRegionUnlocked(Iterable<TESWaypoint.Region> regions) {
		for (TESWaypoint.Region region : regions) {
			if (unlockedFTRegions.contains(region)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPledgedTo(TESFaction fac) {
		return pledgeFaction == fac;
	}

	private boolean isPledgeEnemyAlignmentLimited(TESFaction fac) {
		return pledgeFaction != null && doesFactionPreventPledge(pledgeFaction, fac);
	}

	public boolean isSiegeActive() {
		return siegeActiveTime > 0;
	}

	public void setSiegeActive(int duration) {
		siegeActiveTime = Math.max(siegeActiveTime, duration);
	}

	public void leaveFellowship(TESFellowship fs) {
		if (!fs.isOwner(playerUUID)) {
			fs.removeMember(playerUUID);
			if (fellowshipIDs.contains(fs.getFellowshipID())) {
				removeFellowship(fs);
			}
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				EntityPlayer owner = getOtherPlayer(fs.getOwner());
				if (owner != null) {
					TESFellowship.sendNotification(owner, "TES.gui.fellowships.notifyLeave", entityplayer.getCommandSenderName());
				}
			}
		}
	}

	public List<String> listAllFellowshipNames() {
		List<String> list = new ArrayList<>();
		for (UUID fsID : fellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null && fs.containsPlayer(playerUUID)) {
				list.add(fs.getName());
			}
		}
		return list;
	}

	public List<String> listAllLeadingFellowshipNames() {
		List<String> list = new ArrayList<>();
		for (UUID fsID : fellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null && fs.isOwner(playerUUID)) {
				list.add(fs.getName());
			}
		}
		return list;
	}

	public void load(NBTTagCompound playerData) {
		alignments.clear();
		NBTTagList alignmentTags = playerData.getTagList("AlignmentMap", 10);
		for (int i = 0; i < alignmentTags.tagCount(); i++) {
			NBTTagCompound nbt = alignmentTags.getCompoundTagAt(i);
			TESFaction faction = TESFaction.forName(nbt.getString("Faction"));
			if (faction != null) {
				float alignment;
				if (nbt.hasKey("Alignment")) {
					alignment = nbt.getInteger("Alignment");
				} else {
					alignment = nbt.getFloat("AlignF");
				}
				alignments.put(faction, alignment);
			}
		}
		factionDataMap.clear();
		NBTTagList factionDataTags = playerData.getTagList("FactionData", 10);
		for (int j = 0; j < factionDataTags.tagCount(); j++) {
			NBTTagCompound nbt = factionDataTags.getCompoundTagAt(j);
			TESFaction faction = TESFaction.forName(nbt.getString("Faction"));
			if (faction != null) {
				TESFactionData data = new TESFactionData(this, faction);
				data.load(nbt);
				factionDataMap.put(faction, data);
			}
		}
		if (playerData.hasKey("CurrentFaction")) {
			TESFaction cur = TESFaction.forName(playerData.getString("CurrentFaction"));
			if (cur != null) {
				viewingFaction = cur;
			}
		}
		prevRegionFactions.clear();
		NBTTagList prevRegionFactionTags = playerData.getTagList("PrevRegionFactions", 10);
		for (int k = 0; k < prevRegionFactionTags.tagCount(); k++) {
			NBTTagCompound nbt = prevRegionFactionTags.getCompoundTagAt(k);
			TESDimension.DimensionRegion region = TESDimension.DimensionRegion.forName(nbt.getString("Region"));
			TESFaction faction = TESFaction.forName(nbt.getString("Faction"));
			if (region != null && faction != null) {
				prevRegionFactions.put(region, faction);
			}
		}
		hideAlignment = playerData.getBoolean("HideAlignment");
		takenAlignmentRewards.clear();
		NBTTagList takenRewardsTags = playerData.getTagList("TakenAlignmentRewards", 10);
		for (int m = 0; m < takenRewardsTags.tagCount(); m++) {
			NBTTagCompound nbt = takenRewardsTags.getCompoundTagAt(m);
			TESFaction faction = TESFaction.forName(nbt.getString("Faction"));
			if (faction != null) {
				takenAlignmentRewards.add(faction);
			}
		}
		pledgeFaction = null;
		if (playerData.hasKey("PledgeFac")) {
			pledgeFaction = TESFaction.forName(playerData.getString("PledgeFac"));
		}
		pledgeKillCooldown = playerData.getInteger("PledgeKillCD");
		pledgeBreakCooldown = playerData.getInteger("PledgeBreakCD");
		pledgeBreakCooldownStart = playerData.getInteger("PledgeBreakCDStart");
		brokenPledgeFaction = null;
		if (playerData.hasKey("BrokenPledgeFac")) {
			brokenPledgeFaction = TESFaction.forName(playerData.getString("BrokenPledgeFac"));
		}
		hideOnMap = playerData.getBoolean("HideOnMap");
		adminHideMap = playerData.getBoolean("AdminHideMap");
		if (playerData.hasKey("ShowWP")) {
			showWaypoints = playerData.getBoolean("ShowWP");
		}
		if (playerData.hasKey("ShowCWP")) {
			showCustomWaypoints = playerData.getBoolean("ShowCWP");
		}
		if (playerData.hasKey("ShowHiddenSWP")) {
			showHiddenSharedWaypoints = playerData.getBoolean("ShowHiddenSWP");
		}
		if (playerData.hasKey("ConquestKills")) {
			conquestKills = playerData.getBoolean("ConquestKills");
		}
		achievements.clear();
		NBTTagList achievementTags = playerData.getTagList("Achievements", 10);
		for (int n = 0; n < achievementTags.tagCount(); n++) {
			NBTTagCompound nbt = achievementTags.getCompoundTagAt(n);
			String category = nbt.getString("Category");
			int ID = nbt.getInteger("ID");
			TESAchievement achievement = TESAchievement.achievementForCategoryAndID(TESAchievement.categoryForName(category), ID);
			if (achievement != null && !achievements.contains(achievement)) {
				achievements.add(achievement);
			}
		}
		shield = null;
		if (playerData.hasKey("Shield")) {
			TESShields savedShield = TESShields.shieldForName(playerData.getString("Shield"));
			if (savedShield != null) {
				shield = savedShield;
			}
		}
		if (playerData.hasKey("FriendlyFire")) {
			friendlyFire = playerData.getBoolean("FriendlyFire");
		}
		if (playerData.hasKey("HiredDeathMessages")) {
			hiredDeathMessages = playerData.getBoolean("HiredDeathMessages");
		}
		deathPoint = null;
		//noinspection StreamToLoop
		if (Stream.of("DeathX", "DeathY", "DeathZ").allMatch(playerData::hasKey)) {
			deathPoint = new ChunkCoordinates(playerData.getInteger("DeathX"), playerData.getInteger("DeathY"), playerData.getInteger("DeathZ"));
			if (playerData.hasKey("DeathDim")) {
				deathDim = playerData.getInteger("DeathDim");
			} else {
				deathDim = TESDimension.GAME_OF_THRONES.getDimensionID();
			}
		}
		alcoholTolerance = playerData.getInteger("Alcohol");
		miniQuests.clear();
		NBTTagList miniquestTags = playerData.getTagList("MiniQuests", 10);
		for (int i1 = 0; i1 < miniquestTags.tagCount(); i1++) {
			NBTTagCompound nbt = miniquestTags.getCompoundTagAt(i1);
			TESMiniQuest quest = TESMiniQuest.loadQuestFromNBT(nbt, this);
			if (quest != null) {
				miniQuests.add(quest);
			}
		}
		miniQuestsCompleted.clear();
		NBTTagList miniquestCompletedTags = playerData.getTagList("MiniQuestsCompleted", 10);
		for (int i2 = 0; i2 < miniquestCompletedTags.tagCount(); i2++) {
			NBTTagCompound nbt = miniquestCompletedTags.getCompoundTagAt(i2);
			TESMiniQuest quest = TESMiniQuest.loadQuestFromNBT(nbt, this);
			if (quest != null) {
				miniQuestsCompleted.add(quest);
			}
		}
		completedMiniquestCount = playerData.getInteger("MQCompleteCount");
		completedBountyQuests = playerData.getInteger("MQCompletedBounties");
		trackingMiniQuestID = null;
		if (playerData.hasKey("MiniQuestTrack")) {
			String s = playerData.getString("MiniQuestTrack");
			trackingMiniQuestID = UUID.fromString(s);
		}
		bountiesPlaced.clear();
		NBTTagList bountyTags = playerData.getTagList("BountiesPlaced", 8);
		for (int i3 = 0; i3 < bountyTags.tagCount(); i3++) {
			String fName = bountyTags.getStringTagAt(i3);
			TESFaction fac = TESFaction.forName(fName);
			if (fac != null) {
				bountiesPlaced.add(fac);
			}
		}
		lastWaypoint = null;
		if (playerData.hasKey("LastWP")) {
			String lastWPName = playerData.getString("LastWP");
			lastWaypoint = TESWaypoint.waypointForName(lastWPName);
		}
		lastBiome = null;
		if (playerData.hasKey("LastBiome")) {
			short lastBiomeID = playerData.getShort("LastBiome");
			TESBiome[] biomeList = TESDimension.GAME_OF_THRONES.getBiomeList();
			if (lastBiomeID >= 0 && lastBiomeID < biomeList.length) {
				lastBiome = biomeList[lastBiomeID];
			}
		}
		sentMessageTypes.clear();
		NBTTagList sentMessageTags = playerData.getTagList("SentMessageTypes", 10);
		for (int i4 = 0; i4 < sentMessageTags.tagCount(); i4++) {
			NBTTagCompound nbt = sentMessageTags.getCompoundTagAt(i4);
			TESGuiMessageTypes message = TESGuiMessageTypes.forSaveName(nbt.getString("Message"));
			if (message != null) {
				boolean sent = nbt.getBoolean("Sent");
				sentMessageTypes.put(message, sent);
			}
		}
		playerTitle = null;
		if (playerData.hasKey("PlayerTitleID")) {
			TESTitle title = TESTitle.forID(playerData.getInteger("PlayerTitleID"));
			if (title != null) {
				int colorCode = playerData.getInteger("PlayerTitleColor");
				EnumChatFormatting color = TESTitle.PlayerTitle.colorForID(colorCode);
				playerTitle = new TESTitle.PlayerTitle(title, color);
			}
		}
		if (playerData.hasKey("FemRankOverride")) {
			feminineRanks = playerData.getBoolean("FemRankOverride");
		}
		if (playerData.hasKey("FTSince")) {
			ftSinceTick = playerData.getInteger("FTSince");
		}
		targetFTWaypoint = null;
		uuidToMount = null;
		if (playerData.hasKey("MountUUID")) {
			uuidToMount = UUID.fromString(playerData.getString("MountUUID"));
		}
		uuidToMountTime = playerData.getInteger("MountUUIDTime");
		if (playerData.hasKey("LastOnlineTime")) {
			lastOnlineTime = playerData.getLong("LastOnlineTime");
		}
		unlockedFTRegions.clear();
		NBTTagList unlockedFTRegionTags = playerData.getTagList("UnlockedFTRegions", 10);
		for (int i5 = 0; i5 < unlockedFTRegionTags.tagCount(); i5++) {
			NBTTagCompound nbt = unlockedFTRegionTags.getCompoundTagAt(i5);
			String regionName = nbt.getString("Name");
			TESWaypoint.Region region = TESWaypoint.regionForName(regionName);
			if (region != null) {
				unlockedFTRegions.add(region);
			}
		}
		customWaypoints.clear();
		NBTTagList customWaypointTags = playerData.getTagList("CustomWaypoints", 10);
		for (int i6 = 0; i6 < customWaypointTags.tagCount(); i6++) {
			NBTTagCompound nbt = customWaypointTags.getCompoundTagAt(i6);
			TESCustomWaypoint waypoint = TESCustomWaypoint.readFromNBT(nbt, this);
			customWaypoints.add(waypoint);
		}
		cwpSharedUnlocked.clear();
		NBTTagList cwpSharedUnlockedTags = playerData.getTagList("CWPSharedUnlocked", 10);
		for (int i7 = 0; i7 < cwpSharedUnlockedTags.tagCount(); i7++) {
			NBTTagCompound nbt = cwpSharedUnlockedTags.getCompoundTagAt(i7);
			UUID sharingPlayer = UUID.fromString(nbt.getString("SharingPlayer"));
			int ID = nbt.getInteger("CustomID");
			CWPSharedKey key = CWPSharedKey.keyFor(sharingPlayer, ID);
			cwpSharedUnlocked.add(key);
		}
		cwpSharedHidden.clear();
		NBTTagList cwpSharedHiddenTags = playerData.getTagList("CWPSharedHidden", 10);
		for (int i8 = 0; i8 < cwpSharedHiddenTags.tagCount(); i8++) {
			NBTTagCompound nbt = cwpSharedHiddenTags.getCompoundTagAt(i8);
			UUID sharingPlayer = UUID.fromString(nbt.getString("SharingPlayer"));
			int ID = nbt.getInteger("CustomID");
			CWPSharedKey key = CWPSharedKey.keyFor(sharingPlayer, ID);
			cwpSharedHidden.add(key);
		}
		wpUseCounts.clear();
		NBTTagList wpCooldownTags = playerData.getTagList("WPUses", 10);
		for (int i9 = 0; i9 < wpCooldownTags.tagCount(); i9++) {
			NBTTagCompound nbt = wpCooldownTags.getCompoundTagAt(i9);
			String name = nbt.getString("WPName");
			int count = nbt.getInteger("Count");
			TESWaypoint wp = TESWaypoint.waypointForName(name);
			if (wp != null) {
				wpUseCounts.put(wp, count);
			}
		}
		cwpUseCounts.clear();
		NBTTagList cwpCooldownTags = playerData.getTagList("CWPUses", 10);
		for (int i10 = 0; i10 < cwpCooldownTags.tagCount(); i10++) {
			NBTTagCompound nbt = cwpCooldownTags.getCompoundTagAt(i10);
			int ID = nbt.getInteger("CustomID");
			int count = nbt.getInteger("Count");
			cwpUseCounts.put(ID, count);
		}
		cwpSharedUseCounts.clear();
		NBTTagList cwpSharedCooldownTags = playerData.getTagList("CWPSharedUses", 10);
		for (int i11 = 0; i11 < cwpSharedCooldownTags.tagCount(); i11++) {
			NBTTagCompound nbt = cwpSharedCooldownTags.getCompoundTagAt(i11);
			UUID sharingPlayer = UUID.fromString(nbt.getString("SharingPlayer"));
			int ID = nbt.getInteger("CustomID");
			CWPSharedKey key = CWPSharedKey.keyFor(sharingPlayer, ID);
			int count = nbt.getInteger("Count");
			cwpSharedUseCounts.put(key, count);
		}
		nextCwpID = 20000;
		if (playerData.hasKey("NextCWPID")) {
			nextCwpID = playerData.getInteger("NextCWPID");
		}
		fellowshipIDs.clear();
		NBTTagList fellowshipTags = playerData.getTagList("Fellowships", 10);
		for (int i12 = 0; i12 < fellowshipTags.tagCount(); i12++) {
			NBTTagCompound nbt = fellowshipTags.getCompoundTagAt(i12);
			UUID fsID = UUID.fromString(nbt.getString("ID"));
			fellowshipIDs.add(fsID);
		}
		fellowshipInvites.clear();
		NBTTagList fellowshipInviteTags = playerData.getTagList("FellowshipInvites", 10);
		for (int i13 = 0; i13 < fellowshipInviteTags.tagCount(); i13++) {
			NBTTagCompound nbt = fellowshipInviteTags.getCompoundTagAt(i13);
			UUID fsID = UUID.fromString(nbt.getString("ID"));
			UUID inviterID = null;
			if (nbt.hasKey("InviterID")) {
				inviterID = UUID.fromString(nbt.getString("InviterID"));
			}
			fellowshipInvites.add(new TESFellowshipInvite(fsID, inviterID));
		}
		chatBoundFellowshipID = null;
		if (playerData.hasKey("ChatBoundFellowship")) {
			chatBoundFellowshipID = UUID.fromString(playerData.getString("ChatBoundFellowship"));
		}
		structuresBanned = playerData.getBoolean("StructuresBanned");
		teleportedKW = playerData.getBoolean("TeleportedKW");
		if (playerData.hasKey("QuestData")) {
			NBTTagCompound questNBT = playerData.getCompoundTag("QuestData");
			questData.load(questNBT);
		}
		askedForJaqen = playerData.getBoolean("AskedForJaqen");
		balance = playerData.getInteger("Balance");
		cape = null;
		if (playerData.hasKey("Cape")) {
			TESCapes savedCape = TESCapes.capeForName(playerData.getString("Cape"));
			if (savedCape != null) {
				cape = savedCape;
			}
		}
		checkedMenu = playerData.getBoolean("CheckedMenu");
		tableSwitched = playerData.getBoolean("TableSwitched");
	}

	public void lockFTRegion(TESWaypoint.Region region) {
		if (unlockedFTRegions.remove(region)) {
			markDirty();
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketWaypointRegion(region, false);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		}
	}

	public void markDirty() {
		needsSave = true;
	}

	public boolean needsSave() {
		return needsSave;
	}

	public void onPledgeKill(EntityPlayer entityplayer) {
		pledgeKillCooldown += 24000;
		markDirty();
		if (pledgeKillCooldown > 24000) {
			revokePledgeFaction(entityplayer, false);
		} else if (pledgeFaction != null) {
			IChatComponent chatComponentTranslation = new ChatComponentTranslation("TES.chat.pledgeKillWarn", pledgeFaction.factionName());
			entityplayer.addChatMessage(chatComponentTranslation);
		}
	}

	public void onUpdate(EntityPlayerMP entityplayer, WorldServer world) {
		pdTick++;
		TESDimension.DimensionRegion currentRegion = viewingFaction.getFactionRegion();
		TESDimension currentDim = TESDimension.GAME_OF_THRONES;
		if (currentRegion.getDimension() != currentDim) {
			currentRegion = currentDim.getDimensionRegions().get(0);
			setViewingFaction(getRegionLastViewedFaction(currentRegion));
		}
		if (!isSiegeActive()) {
			runAchievementChecks(entityplayer, world);
		}
		if (!checkedMenu) {
			IMessage packet = new TESPacketMenuPrompt(TESPacketMenuPrompt.Type.MENU);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
		}
		if (playerTitle != null && !playerTitle.getTitle().canPlayerUse(entityplayer)) {
			IChatComponent chatComponentTranslation = new ChatComponentTranslation("TES.chat.loseTitle", playerTitle.getFullTitleComponent(entityplayer));
			entityplayer.addChatMessage(chatComponentTranslation);
			setPlayerTitle(null);
		}
		if (pledgeKillCooldown > 0) {
			pledgeKillCooldown--;
			if (pledgeKillCooldown == 0 || isTimerAutosaveTick()) {
				markDirty();
			}
		}
		if (pledgeBreakCooldown > 0) {
			setPledgeBreakCooldown(pledgeBreakCooldown - 1);
		}
		if (trackingMiniQuestID != null && getTrackingMiniQuest() == null) {
			setTrackingMiniQuest(null);
		}
		List<TESMiniQuest> activeMiniquests = getActiveMiniQuests();
		for (TESMiniQuest quest : activeMiniquests) {
			quest.onPlayerTick();
		}
		if (!bountiesPlaced.isEmpty()) {
			for (TESFaction fac : bountiesPlaced) {
				IChatComponent chatComponentTranslation = new ChatComponentTranslation("TES.chat.bountyPlaced", fac.factionName());
				chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
				entityplayer.addChatMessage(chatComponentTranslation);
			}
			bountiesPlaced.clear();
			markDirty();
		}
		setTimeSinceFT(ftSinceTick + 1);
		if (targetFTWaypoint != null) {
			if (entityplayer.isPlayerSleeping()) {
				entityplayer.addChatMessage(new ChatComponentTranslation("TES.fastTravel.inBed"));
				setTargetFTWaypoint(null);
			} else if (ticksUntilFT > 0) {
				int seconds = ticksUntilFT / 20;
				if (ticksUntilFT == TICKS_UNTIL_FT_MAX) {
					entityplayer.addChatMessage(new ChatComponentTranslation("TES.fastTravel.travelTicksStart", seconds));
				} else if (ticksUntilFT % 20 == 0 && seconds <= 5) {
					entityplayer.addChatMessage(new ChatComponentTranslation("TES.fastTravel.travelTicks", seconds));
				}
				ticksUntilFT--;
				setTicksUntilFT(ticksUntilFT);
			} else {
				sendFTBouncePacket(entityplayer);
			}
		} else {
			setTicksUntilFT(0);
		}
		lastOnlineTime = getCurrentOnlineTime();
		if (uuidToMount != null) {
			if (uuidToMountTime > 0) {
				uuidToMountTime--;
			} else {
				double range = 32.0D;
				List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, entityplayer.boundingBox.expand(range, range, range));
				for (EntityLivingBase entity : entities) {
					if (entity.getUniqueID().equals(uuidToMount)) {
						entityplayer.mountEntity(entity);
						break;
					}
				}
				setUUIDToMount(null);
			}
		}
		if (pdTick % 24000 == 0 && alcoholTolerance > 0) {
			alcoholTolerance--;
			setAlcoholTolerance(alcoholTolerance);
		}
		unlockSharedCustomWaypoints(entityplayer);
		if (pdTick % 100 == 0 && world.provider instanceof TESWorldProvider) {
			int i = MathHelper.floor_double(entityplayer.posX);
			int k = MathHelper.floor_double(entityplayer.posZ);
			TESBiome biome = (TESBiome) TESCrashHandler.getBiomeGenForCoords(world.provider, i, k);
			if (biome.getBiomeDimension() == TESDimension.GAME_OF_THRONES) {
				TESBiome prevLastBiome = lastBiome;
				lastBiome = biome;
				if (prevLastBiome != biome) {
					markDirty();
				}
			}
		}
		if (adminHideMap) {
			boolean isOp = MinecraftServer.getServer().getConfigurationManager().func_152596_g(entityplayer.getGameProfile());
			if (!entityplayer.capabilities.isCreativeMode || !isOp) {
				setAdminHideMap(false);
				TESCommandAdminHideMap.notifyUnhidden(entityplayer);
			}
		}
		if (siegeActiveTime > 0) {
			siegeActiveTime--;
		}
	}

	public void placeBountyFor(TESFaction f) {
		bountiesPlaced.add(f);
		markDirty();
	}

	public void receiveFTBouncePacket() {
		if (targetFTWaypoint != null && ticksUntilFT <= 0) {
			fastTravelTo(targetFTWaypoint);
			setTargetFTWaypoint(null);
		}
	}

	public void rejectFellowshipInvite(TESFellowship fs) {
		UUID fsID = fs.getFellowshipID();
		TESFellowshipInvite existingInvite = null;
		for (TESFellowshipInvite invite : fellowshipInvites) {
			if (invite.getFellowshipID().equals(fsID)) {
				existingInvite = invite;
				break;
			}
		}
		if (existingInvite != null) {
			fellowshipInvites.remove(existingInvite);
			markDirty();
			sendFellowshipInviteRemovePacket(fs);
		}
	}

	public void removeAchievement(TESAchievement achievement) {
		if (!hasAchievement(achievement)) {
			return;
		}
		if (achievements.remove(achievement)) {
			markDirty();
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				sendAchievementRemovePacket((EntityPlayerMP) entityplayer, achievement);
			}
		}
	}

	public void removeClientFellowship(UUID fsID) {
		TESFellowshipClient inList = null;
		for (TESFellowshipClient fsInList : fellowshipsClient) {
			if (fsInList.getFellowshipID().equals(fsID)) {
				inList = fsInList;
				break;
			}
		}
		if (inList != null) {
			fellowshipsClient.remove(inList);
		}
	}

	public void removeClientFellowshipInvite(UUID fsID) {
		TESFellowshipClient inList = null;
		for (TESFellowshipClient fsInList : fellowshipInvitesClient) {
			if (fsInList.getFellowshipID().equals(fsID)) {
				inList = fsInList;
				break;
			}
		}
		if (inList != null) {
			fellowshipInvitesClient.remove(inList);
		}
	}

	public void removeCustomWaypoint(TESCustomWaypoint waypoint) {
		if (customWaypoints.remove(waypoint)) {
			markDirty();
			for (UUID fsID : waypoint.getSharedFellowshipIDs()) {
				TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
				if (fs != null) {
					checkIfStillWaypointSharerForFellowship(fs);
				}
			}
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				TESPacketDeleteCWPClient packet = waypoint.getClientDeletePacket();
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
				TESCustomWaypointLogger.logDelete(entityplayer, waypoint);
			}
			TESCustomWaypoint shareCopy = waypoint.createCopyOfShared(playerUUID);
			List<UUID> sharedPlayers = shareCopy.getPlayersInAllSharedFellowships();
			for (UUID sharedPlayerUUID : sharedPlayers) {
				EntityPlayer sharedPlayer = getOtherPlayer(sharedPlayerUUID);
				if (sharedPlayer != null && !sharedPlayer.worldObj.isRemote) {
					TESLevelData.getData(sharedPlayerUUID).removeSharedCustomWaypoint(shareCopy);
				}
			}
		}
	}

	public void removeCustomWaypointClient(TESCustomWaypoint waypoint) {
		customWaypoints.remove(waypoint);
	}

	public void removeFellowship(TESFellowship fs) {
		if (fs.isDisbanded() || !fs.containsPlayer(playerUUID)) {
			UUID fsID = fs.getFellowshipID();
			if (fellowshipIDs.contains(fsID)) {
				fellowshipIDs.remove(fsID);
				markDirty();
				sendFellowshipRemovePacket(fs);
				unshareFellowshipFromOwnCustomWaypoints(fs);
				checkCustomWaypointsSharedFromFellowships();
			}
		}
	}

	public void removeMiniQuest(TESMiniQuest quest, boolean completed) {
		List<TESMiniQuest> removeList;
		if (completed) {
			removeList = miniQuestsCompleted;
		} else {
			removeList = miniQuests;
		}
		if (removeList.remove(quest)) {
			markDirty();
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketMiniquestRemove(quest, quest.isCompleted(), false);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		} else {
			FMLLog.warning("Warning: Attempted to remove a miniquest which does not belong to the player data");
		}
	}

	public void removePlayerFromFellowship(TESFellowship fs, UUID player, String removerUsername) {
		if (fs.isOwner(playerUUID) || fs.isAdmin(playerUUID)) {
			fs.removeMember(player);
			EntityPlayer removed = getOtherPlayer(player);
			if (removed != null && !removed.worldObj.isRemote) {
				TESFellowship.sendNotification(removed, "TES.gui.fellowships.notifyRemove", removerUsername);
			}
		}
	}

	public void removeSharedCustomWaypoint(TESCustomWaypoint waypoint) {
		if (!waypoint.isShared()) {
			FMLLog.warning("Hummel009: Warning! Tried to remove a shared custom waypoint with no owner!");
			return;
		}
		TESCustomWaypoint existing;
		if (customWaypointsShared.contains(waypoint)) {
			existing = waypoint;
		} else {
			existing = getSharedCustomWaypointByID(waypoint.getSharingPlayerID(), waypoint.getID());
		}
		if (existing != null) {
			customWaypointsShared.remove(existing);
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				TESPacketDeleteCWPClient packet = waypoint.getClientDeletePacketShared();
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		} else {
			FMLLog.warning("Hummel009: Warning! Tried to remove a shared custom waypoint that does not exist!");
		}
	}

	public void renameCustomWaypoint(TESCustomWaypoint waypoint, String newName) {
		waypoint.rename(newName);
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketRenameCWPClient packet = waypoint.getClientRenamePacket();
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			TESCustomWaypointLogger.logRename(entityplayer, waypoint);
		}
		TESCustomWaypoint shareCopy = waypoint.createCopyOfShared(playerUUID);
		List<UUID> sharedPlayers = shareCopy.getPlayersInAllSharedFellowships();
		for (UUID sharedPlayerUUID : sharedPlayers) {
			EntityPlayer sharedPlayer = getOtherPlayer(sharedPlayerUUID);
			if (sharedPlayer != null && !sharedPlayer.worldObj.isRemote) {
				TESLevelData.getData(sharedPlayerUUID).renameSharedCustomWaypoint(shareCopy, newName);
			}
		}
	}

	public void renameFellowship(TESFellowship fs, String name) {
		if (fs.isOwner(playerUUID)) {
			fs.setName(name);
		}
	}

	public void renameSharedCustomWaypoint(TESCustomWaypoint waypoint, String newName) {
		if (!waypoint.isShared()) {
			FMLLog.warning("Hummel009: Warning! Tried to rename a shared custom waypoint with no owner!");
			return;
		}
		waypoint.rename(newName);
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketRenameCWPClient packet = waypoint.getClientRenamePacketShared();
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public void revokePledgeFaction(EntityPlayer entityplayer, boolean intentional) {
		TESFaction wasPledge = pledgeFaction;
		float pledgeLvl = wasPledge.getPledgeAlignment();
		float prevAlign = getAlignment(wasPledge);
		float diff = prevAlign - pledgeLvl;
		float cd = diff / 5000.0F;
		cd = MathHelper.clamp_float(cd, 0.0F, 1.0F);
		int cdTicks = 36000;
		cdTicks += Math.round(cd * 150.0F * 60.0F * 20.0F);
		setPledgeFaction(null);
		setBrokenPledgeFaction(wasPledge);
		setPledgeBreakCooldown(cdTicks);
		World world = entityplayer.worldObj;
		if (!world.isRemote) {
			TESFactionRank rank = wasPledge.getRank(prevAlign);
			TESFactionRank rankBelow = wasPledge.getRankBelow(rank);
			TESFactionRank rankBelow2 = wasPledge.getRankBelow(rankBelow);
			float newAlign = rankBelow2.getAlignment();
			newAlign = Math.max(newAlign, pledgeLvl / 2.0F);
			float alignPenalty = newAlign - prevAlign;
			if (alignPenalty < 0.0F) {
				TESAlignmentValues.AlignmentBonus penalty = TESAlignmentValues.createPledgePenalty(alignPenalty);
				double alignX;
				double alignY;
				double alignZ;
				double lookRange = 2.0D;
				Vec3 posEye = Vec3.createVectorHelper(entityplayer.posX, entityplayer.boundingBox.minY + entityplayer.getEyeHeight(), entityplayer.posZ);
				Vec3 look = entityplayer.getLook(1.0F);
				Vec3 posSight = posEye.addVector(look.xCoord * lookRange, look.yCoord * lookRange, look.zCoord * lookRange);
				MovingObjectPosition mop = world.rayTraceBlocks(posEye, posSight);
				if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					alignX = mop.blockX + 0.5D;
					alignY = mop.blockY + 0.5D;
					alignZ = mop.blockZ + 0.5D;
				} else {
					alignX = posSight.xCoord;
					alignY = posSight.yCoord;
					alignZ = posSight.zCoord;
				}
				addAlignment(entityplayer, penalty, wasPledge, alignX, alignY, alignZ);
			}
			world.playSoundAtEntity(entityplayer, "TES:event.unpledge", 1.0F, 1.0F);
			ChatComponentTranslation chatComponentTranslation;
			if (intentional) {
				chatComponentTranslation = new ChatComponentTranslation("TES.chat.unpledge", wasPledge.factionName());
			} else {
				chatComponentTranslation = new ChatComponentTranslation("TES.chat.autoUnpledge", wasPledge.factionName());
			}
			entityplayer.addChatMessage(chatComponentTranslation);
			checkAlignmentAchievements(wasPledge);
		}
	}

	private void runAchievementChecks(EntityPlayer entityplayer, World world) {
		int i = MathHelper.floor_double(entityplayer.posX);
		int k = MathHelper.floor_double(entityplayer.posZ);
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(world, i, k);
		if (biome instanceof TESBiome) {
			TESBiome TESbiome = (TESBiome) biome;
			TESAchievement ach = TESbiome.getBiomeAchievement();
			if (ach != null) {
				addAchievement(ach);
			}
			TESWaypoint.Region biomeRegion = TESbiome.getBiomeWaypoints();
			if (biomeRegion != null) {
				unlockFTRegion(biomeRegion);
			}
		}
		
	}

	public void save(NBTTagCompound playerData) {
		NBTTagList alignmentTags = new NBTTagList();
		for (Map.Entry<TESFaction, Float> entry : alignments.entrySet()) {
			TESFaction faction = entry.getKey();
			float alignment = entry.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Faction", faction.codeName());
			nbt.setFloat("AlignF", alignment);
			alignmentTags.appendTag(nbt);
		}
		playerData.setTag("AlignmentMap", alignmentTags);
		NBTTagList factionDataTags = new NBTTagList();
		for (Map.Entry<TESFaction, TESFactionData> entry : factionDataMap.entrySet()) {
			TESFaction faction = entry.getKey();
			TESFactionData data = entry.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Faction", faction.codeName());
			data.save(nbt);
			factionDataTags.appendTag(nbt);
		}
		playerData.setTag("FactionData", factionDataTags);
		playerData.setString("CurrentFaction", viewingFaction.codeName());
		NBTTagList prevRegionFactionTags = new NBTTagList();
		for (Map.Entry<TESDimension.DimensionRegion, TESFaction> entry : prevRegionFactions.entrySet()) {
			TESDimension.DimensionRegion region = entry.getKey();
			TESFaction faction = entry.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Region", region.codeName());
			nbt.setString("Faction", faction.codeName());
			prevRegionFactionTags.appendTag(nbt);
		}
		playerData.setTag("PrevRegionFactions", prevRegionFactionTags);
		playerData.setBoolean("HideAlignment", hideAlignment);
		NBTTagList takenRewardsTags = new NBTTagList();
		for (TESFaction faction : takenAlignmentRewards) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Faction", faction.codeName());
			takenRewardsTags.appendTag(nbt);
		}
		playerData.setTag("TakenAlignmentRewards", takenRewardsTags);
		if (pledgeFaction != null) {
			playerData.setString("PledgeFac", pledgeFaction.codeName());
		}
		playerData.setInteger("PledgeKillCD", pledgeKillCooldown);
		playerData.setInteger("PledgeBreakCD", pledgeBreakCooldown);
		playerData.setInteger("PledgeBreakCDStart", pledgeBreakCooldownStart);
		if (brokenPledgeFaction != null) {
			playerData.setString("BrokenPledgeFac", brokenPledgeFaction.codeName());
		}
		playerData.setBoolean("HideOnMap", hideOnMap);
		playerData.setBoolean("AdminHideMap", adminHideMap);
		playerData.setBoolean("ShowWP", showWaypoints);
		playerData.setBoolean("ShowCWP", showCustomWaypoints);
		playerData.setBoolean("ShowHiddenSWP", showHiddenSharedWaypoints);
		playerData.setBoolean("ConquestKills", conquestKills);
		NBTTagList achievementTags = new NBTTagList();
		for (TESAchievement achievement : achievements) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Category", achievement.getCategory().name());
			nbt.setInteger("ID", achievement.getId());
			achievementTags.appendTag(nbt);
		}
		playerData.setTag("Achievements", achievementTags);
		if (shield != null) {
			playerData.setString("Shield", shield.name());
		}
		playerData.setBoolean("FriendlyFire", friendlyFire);
		playerData.setBoolean("HiredDeathMessages", hiredDeathMessages);
		if (deathPoint != null) {
			playerData.setInteger("DeathX", deathPoint.posX);
			playerData.setInteger("DeathY", deathPoint.posY);
			playerData.setInteger("DeathZ", deathPoint.posZ);
			playerData.setInteger("DeathDim", deathDim);
		}
		playerData.setInteger("Alcohol", alcoholTolerance);
		NBTTagList miniquestTags = new NBTTagList();
		for (TESMiniQuest quest : miniQuests) {
			NBTTagCompound nbt = new NBTTagCompound();
			quest.writeToNBT(nbt);
			miniquestTags.appendTag(nbt);
		}
		playerData.setTag("MiniQuests", miniquestTags);
		NBTTagList miniquestCompletedTags = new NBTTagList();
		for (TESMiniQuest quest : miniQuestsCompleted) {
			NBTTagCompound nbt = new NBTTagCompound();
			quest.writeToNBT(nbt);
			miniquestCompletedTags.appendTag(nbt);
		}
		playerData.setTag("MiniQuestsCompleted", miniquestCompletedTags);
		playerData.setInteger("MQCompleteCount", completedMiniquestCount);
		playerData.setInteger("MQCompletedBounties", completedBountyQuests);
		if (trackingMiniQuestID != null) {
			playerData.setString("MiniQuestTrack", trackingMiniQuestID.toString());
		}
		NBTTagList bountyTags = new NBTTagList();
		for (TESFaction fac : bountiesPlaced) {
			String fName = fac.codeName();
			bountyTags.appendTag(new NBTTagString(fName));
		}
		playerData.setTag("BountiesPlaced", bountyTags);
		if (lastWaypoint != null) {
			String lastWPName = lastWaypoint.getCodeName();
			playerData.setString("LastWP", lastWPName);
		}
		if (lastBiome != null) {
			int lastBiomeID = lastBiome.biomeID;
			playerData.setShort("LastBiome", (short) lastBiomeID);
		}
		NBTTagList sentMessageTags = new NBTTagList();
		for (Map.Entry<TESGuiMessageTypes, Boolean> entry : sentMessageTypes.entrySet()) {
			TESGuiMessageTypes message = entry.getKey();
			boolean sent = entry.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Message", message.getSaveName());
			nbt.setBoolean("Sent", sent);
			sentMessageTags.appendTag(nbt);
		}
		playerData.setTag("SentMessageTypes", sentMessageTags);
		if (playerTitle != null) {
			playerData.setInteger("PlayerTitleID", playerTitle.getTitle().getTitleID());
			playerData.setInteger("PlayerTitleColor", playerTitle.getColor().getFormattingCode());
		}
		playerData.setBoolean("FemRankOverride", feminineRanks);
		playerData.setInteger("FTSince", ftSinceTick);
		if (uuidToMount != null) {
			playerData.setString("MountUUID", uuidToMount.toString());
		}
		playerData.setInteger("MountUUIDTime", uuidToMountTime);
		playerData.setLong("LastOnlineTime", lastOnlineTime);
		NBTTagList unlockedFTRegionTags = new NBTTagList();
		for (TESWaypoint.Region region : unlockedFTRegions) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Name", region.name());
			unlockedFTRegionTags.appendTag(nbt);
		}
		playerData.setTag("UnlockedFTRegions", unlockedFTRegionTags);
		NBTTagList customWaypointTags = new NBTTagList();
		for (TESCustomWaypoint waypoint : customWaypoints) {
			NBTTagCompound nbt = new NBTTagCompound();
			waypoint.writeToNBT(nbt, this);
			customWaypointTags.appendTag(nbt);
		}
		playerData.setTag("CustomWaypoints", customWaypointTags);
		NBTTagList cwpSharedUnlockedTags = new NBTTagList();
		for (CWPSharedKey key : cwpSharedUnlocked) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("SharingPlayer", key.getSharingPlayer().toString());
			nbt.setInteger("CustomID", key.getWaypointID());
			cwpSharedUnlockedTags.appendTag(nbt);
		}
		playerData.setTag("CWPSharedUnlocked", cwpSharedUnlockedTags);
		NBTTagList cwpSharedHiddenTags = new NBTTagList();
		for (CWPSharedKey key : cwpSharedHidden) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("SharingPlayer", key.getSharingPlayer().toString());
			nbt.setInteger("CustomID", key.getWaypointID());
			cwpSharedHiddenTags.appendTag(nbt);
		}
		playerData.setTag("CWPSharedHidden", cwpSharedHiddenTags);
		NBTTagList wpUseTags = new NBTTagList();
		for (Map.Entry<TESWaypoint, Integer> e : wpUseCounts.entrySet()) {
			TESAbstractWaypoint wp = e.getKey();
			int count = e.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("WPName", wp.getCodeName());
			nbt.setInteger("Count", count);
			wpUseTags.appendTag(nbt);
		}
		playerData.setTag("WPUses", wpUseTags);
		NBTTagList cwpUseTags = new NBTTagList();
		for (Map.Entry<Integer, Integer> e : cwpUseCounts.entrySet()) {
			int ID = e.getKey();
			int count = e.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("CustomID", ID);
			nbt.setInteger("Count", count);
			cwpUseTags.appendTag(nbt);
		}
		playerData.setTag("CWPUses", cwpUseTags);
		NBTTagList cwpSharedUseTags = new NBTTagList();
		for (Map.Entry<CWPSharedKey, Integer> e : cwpSharedUseCounts.entrySet()) {
			CWPSharedKey key = e.getKey();
			int count = e.getValue();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("SharingPlayer", key.getSharingPlayer().toString());
			nbt.setInteger("CustomID", key.getWaypointID());
			nbt.setInteger("Count", count);
			cwpSharedUseTags.appendTag(nbt);
		}
		playerData.setTag("CWPSharedUses", cwpSharedUseTags);
		playerData.setInteger("NextCWPID", nextCwpID);
		NBTTagList fellowshipTags = new NBTTagList();
		for (UUID fsID : fellowshipIDs) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("ID", fsID.toString());
			fellowshipTags.appendTag(nbt);
		}
		playerData.setTag("Fellowships", fellowshipTags);
		NBTTagList fellowshipInviteTags = new NBTTagList();
		for (TESFellowshipInvite invite : fellowshipInvites) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("ID", invite.getFellowshipID().toString());
			if (invite.getInviterID() != null) {
				nbt.setString("InviterID", invite.getInviterID().toString());
			}
			fellowshipInviteTags.appendTag(nbt);
		}
		playerData.setTag("FellowshipInvites", fellowshipInviteTags);
		if (chatBoundFellowshipID != null) {
			playerData.setString("ChatBoundFellowship", chatBoundFellowshipID.toString());
		}
		playerData.setBoolean("StructuresBanned", structuresBanned);
		playerData.setBoolean("TeleportedKW", teleportedKW);
		NBTTagCompound questNBT = new NBTTagCompound();
		questData.save(questNBT);
		playerData.setTag("QuestData", questNBT);

		playerData.setBoolean("AskedForJaqen", askedForJaqen);
		playerData.setInteger("Balance", balance);
		if (cape != null) {
			playerData.setString("Cape", cape.name());
		}
		playerData.setBoolean("CheckedMenu", checkedMenu);
		playerData.setBoolean("TableSwitched", tableSwitched);
		needsSave = false;
	}

	public List<TESMiniQuest> selectMiniQuests(TESMiniQuestSelector selector) {
		List<TESMiniQuest> ret = new ArrayList<>();
		Iterable<TESMiniQuest> threadSafe = new ArrayList<>(miniQuests);
		for (TESMiniQuest quest : threadSafe) {
			if (selector.include(quest)) {
				ret.add(quest);
			}
		}
		return ret;
	}

	private void sendAlignmentBonusPacket(TESAlignmentValues.AlignmentBonus source, TESFaction faction, float prevMainAlignment, TESAlignmentBonusMap factionMap, float conqBonus, double posX, double posY, double posZ) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null) {
			IMessage packet = new TESPacketAlignmentBonus(faction, prevMainAlignment, factionMap, conqBonus, posX, posY, posZ, source);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	private void sendFellowshipInvitePacket(TESFellowship fs) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketFellowship(this, fs, true);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	private void sendFellowshipInviteRemovePacket(TESFellowship fs) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketFellowshipRemove(fs, true);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	private void sendFellowshipPacket(TESFellowship fs) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketFellowship(this, fs, false);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	private void sendFellowshipRemovePacket(TESFellowship fs) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketFellowshipRemove(fs, false);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public void sendMessageIfNotReceived(TESGuiMessageTypes message) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			boolean sent = sentMessageTypes.computeIfAbsent(message, k -> false);
			if (!sent) {
				sentMessageTypes.put(message, true);
				markDirty();
				IMessage packet = new TESPacketMessage(message);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		}
	}

	private void sendOptionsPacket(int option, boolean flag) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketOptions(option, flag);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public void sendPlayerData(EntityPlayerMP entityplayer) {
		NBTTagCompound nbt = new NBTTagCompound();
		save(nbt);
		nbt.removeTag("Achievements");
		nbt.removeTag("MiniQuests");
		nbt.removeTag("MiniQuestsCompleted");
		nbt.removeTag("CustomWaypoints");
		nbt.removeTag("Fellowships");
		nbt.removeTag("FellowshipInvites");
		IMessage packet = new TESPacketLoginPlayerData(nbt);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
		for (TESAchievement achievement : achievements) {
			sendAchievementPacket(entityplayer, achievement, false);
		}
		for (TESMiniQuest quest : miniQuests) {
			sendMiniQuestPacket(entityplayer, quest, false);
		}
		for (TESMiniQuest quest : miniQuestsCompleted) {
			sendMiniQuestPacket(entityplayer, quest, true);
		}
		for (TESCustomWaypoint waypoint : customWaypoints) {
			TESPacketCreateCWPClient cwpPacket = waypoint.getClientPacket();
			TESPacketHandler.NETWORK_WRAPPER.sendTo(cwpPacket, entityplayer);
		}
		for (UUID fsID : fellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null) {
				sendFellowshipPacket(fs);
				fs.doRetroactiveWaypointSharerCheckIfNeeded();
				checkIfStillWaypointSharerForFellowship(fs);
			}
		}
		Collection<TESFellowshipInvite> staleFellowshipInvites = new HashSet<>();
		for (TESFellowshipInvite invite : fellowshipInvites) {
			TESFellowship fs = TESFellowshipData.getFellowship(invite.getFellowshipID());
			if (fs != null) {
				sendFellowshipInvitePacket(fs);
				continue;
			}
			staleFellowshipInvites.add(invite);
		}
		if (!staleFellowshipInvites.isEmpty()) {
			fellowshipInvites.removeAll(staleFellowshipInvites);
			markDirty();
		}
		addSharedCustomWaypointsFromAllFellowships();
	}

	public void setAlignment(TESFaction faction, float alignment) {
		EntityPlayer entityplayer = getPlayer();
		if (faction.isPlayableAlignmentFaction()) {
			alignments.put(faction, alignment);
			markDirty();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				TESLevelData.sendAlignmentToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
			}
			checkAlignmentAchievements(faction);
		}
		if (entityplayer != null && !entityplayer.worldObj.isRemote && pledgeFaction != null && !canPledgeTo(pledgeFaction)) {
			revokePledgeFaction(entityplayer, false);
		}
	}

	public void setAlignmentFromCommand(TESFaction faction, float set) {
		setAlignment(faction, set);
	}

	public void setDeathPoint(int i, int j, int k) {
		deathPoint = new ChunkCoordinates(i, j, k);
		markDirty();
	}

	public void setFellowshipAdmin(TESFellowship fs, UUID player, boolean flag, String granterUsername) {
		if (fs.isOwner(playerUUID)) {
			fs.setAdmin(player, flag);
			EntityPlayer subjectPlayer = getOtherPlayer(player);
			if (subjectPlayer != null && !subjectPlayer.worldObj.isRemote) {
				if (flag) {
					TESFellowship.sendNotification(subjectPlayer, "TES.gui.fellowships.notifyOp", granterUsername);
				} else {
					TESFellowship.sendNotification(subjectPlayer, "TES.gui.fellowships.notifyDeop", granterUsername);
				}
			}
		}
	}

	public void setFellowshipIcon(TESFellowship fs, ItemStack itemstack) {
		if (fs.isOwner(playerUUID) || fs.isAdmin(playerUUID)) {
			fs.setIcon(itemstack);
		}
	}

	public void setFellowshipPreventHiredFF(TESFellowship fs, boolean prevent) {
		if (fs.isOwner(playerUUID) || fs.isAdmin(playerUUID)) {
			fs.setPreventHiredFriendlyFire(prevent);
		}
	}

	public void setFellowshipPreventPVP(TESFellowship fs, boolean prevent) {
		if (fs.isOwner(playerUUID) || fs.isAdmin(playerUUID)) {
			fs.setPreventPVP(prevent);
		}
	}

	public void setFellowshipShowMapLocations(TESFellowship fs, boolean show) {
		if (fs.isOwner(playerUUID)) {
			fs.setShowMapLocations(show);
		}
	}

	public void setRegionLastViewedFaction(TESDimension.DimensionRegion region, TESFaction fac) {
		if (region.getFactionList().contains(fac)) {
			prevRegionFactions.put(region, fac);
			markDirty();
		}
	}

	private void setTimeSinceFT(int i, boolean forceUpdate) {
		int preTick = ftSinceTick;
		ftSinceTick = Math.max(0, i);
		boolean bigChange = (ftSinceTick == 0 || preTick == 0) && ftSinceTick != preTick || preTick < 0;
		if (bigChange || isTimerAutosaveTick() || forceUpdate) {
			markDirty();
		}
		if (bigChange || ftSinceTick % 5 == 0 || forceUpdate) {
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketFTTimer(ftSinceTick);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		}
	}

	public void setTimeSinceFTWithUpdate(int i) {
		setTimeSinceFT(i, true);
	}

	private void setUUIDToMount(UUID uuid) {
		uuidToMount = uuid;
		if (uuidToMount != null) {
			uuidToMountTime = 10;
		} else {
			uuidToMountTime = 0;
		}
		markDirty();
	}

	public void setWPUseCount(TESAbstractWaypoint wp, int count) {
		if (wp instanceof TESCustomWaypoint) {
			TESCustomWaypoint cwp = (TESCustomWaypoint) wp;
			int ID = cwp.getID();
			if (cwp.isShared()) {
				UUID sharingPlayer = cwp.getSharingPlayerID();
				CWPSharedKey key = CWPSharedKey.keyFor(sharingPlayer, ID);
				cwpSharedUseCounts.put(key, count);
			} else {
				cwpUseCounts.put(ID, count);
			}
		} else {
			wpUseCounts.put((TESWaypoint) wp, count);
		}
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage packet = new TESPacketWaypointUseCount(wp, count);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public boolean showCustomWaypoints() {
		return showCustomWaypoints;
	}

	public boolean showHiddenSharedWaypoints() {
		return showHiddenSharedWaypoints;
	}

	public boolean showWaypoints() {
		return showWaypoints;
	}

	public void transferFellowship(TESFellowship fs, UUID player, String prevOwnerUsername) {
		if (fs.isOwner(playerUUID)) {
			fs.setOwner(player);
			EntityPlayer newOwner = getOtherPlayer(player);
			if (newOwner != null && !newOwner.worldObj.isRemote) {
				TESFellowship.sendNotification(newOwner, "TES.gui.fellowships.notifyTransfer", prevOwnerUsername);
			}
		}
	}

	public void unlockFTRegion(TESWaypoint.Region region) {
		if (isSiegeActive()) {
			return;
		}
		if (unlockedFTRegions.add(region)) {
			markDirty();
			EntityPlayer entityplayer = getPlayer();
			if (entityplayer != null && !entityplayer.worldObj.isRemote) {
				IMessage packet = new TESPacketWaypointRegion(region, true);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			}
		}
	}

	public void unlockSharedCustomWaypoint(TESCustomWaypoint waypoint) {
		if (!waypoint.isShared()) {
			FMLLog.warning("Hummel009: Warning! Tried to unlock a shared custom waypoint with no owner!");
			return;
		}
		waypoint.setSharedUnlocked();
		CWPSharedKey key = CWPSharedKey.keyFor(waypoint.getSharingPlayerID(), waypoint.getID());
		cwpSharedUnlocked.add(key);
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			TESPacketCWPSharedUnlockClient packet = waypoint.getClientSharedUnlockPacket();
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	private void unlockSharedCustomWaypoints(EntityPlayer entityplayer) {
		if (pdTick % 20 == 0 && entityplayer.dimension == TESDimension.GAME_OF_THRONES.getDimensionID()) {
			Collection<TESCustomWaypoint> unlockWaypoints = new ArrayList<>();
			for (TESCustomWaypoint waypoint : customWaypointsShared) {
				if (waypoint.isShared() && !waypoint.isSharedUnlocked() && waypoint.canUnlockShared(entityplayer)) {
					unlockWaypoints.add(waypoint);
				}
			}
			for (TESCustomWaypoint waypoint : unlockWaypoints) {
				unlockSharedCustomWaypoint(waypoint);
			}
		}
	}

	private void unshareFellowshipFromOwnCustomWaypoints(TESFellowship fs) {
		for (TESCustomWaypoint waypoint : customWaypoints) {
			if (waypoint.hasSharedFellowship(fs)) {
				customWaypointRemoveSharedFellowship(waypoint, fs);
			}
		}
	}

	public void updateFactionData(TESFaction faction, TESFactionData factionData) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			markDirty();
			NBTTagCompound nbt = new NBTTagCompound();
			factionData.save(nbt);
			IMessage packet = new TESPacketFactionData(faction, nbt);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public void updateFastTravelClockFromLastOnlineTime(ICommandSender player) {
		if (lastOnlineTime <= 0L) {
			return;
		}
		MinecraftServer server = MinecraftServer.getServer();
		if (!server.isSinglePlayer()) {
			long currentOnlineTime = getCurrentOnlineTime();
			int diff = (int) (currentOnlineTime - lastOnlineTime);
			double offlineFactor = 0.1D;
			int ftClockIncrease = (int) (diff * offlineFactor);
			if (ftClockIncrease > 0) {
				setTimeSinceFTWithUpdate(ftSinceTick + ftClockIncrease);
				IChatComponent chatComponentTranslation = new ChatComponentTranslation("TES.chat.ft.offlineTick", TESLevelData.getHMSTime_Ticks(ftClockIncrease));
				player.addChatMessage(chatComponentTranslation);
			}
		}
	}

	public void updateFellowship(TESFellowship fs, FellowshipUpdateType updateType) {
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			IMessage updatePacket = updateType.createUpdatePacket(this, fs);
			if (updatePacket != null) {
				TESPacketHandler.NETWORK_WRAPPER.sendTo(updatePacket, (EntityPlayerMP) entityplayer);
			} else {
				TESLog.getLogger().error("No associated packet for fellowship update type {}", updateType.getClass().getName());
			}
		}
		List<UUID> playersToCheckSharedWaypointsFrom = updateType.getPlayersToCheckSharedWaypointsFrom(fs);
		if (playersToCheckSharedWaypointsFrom != null && !playersToCheckSharedWaypointsFrom.isEmpty()) {
			addSharedCustomWaypointsFrom(fs.getFellowshipID(), playersToCheckSharedWaypointsFrom);
			checkCustomWaypointsSharedBy(playersToCheckSharedWaypointsFrom);
		}
	}

	public void updateMiniQuest(TESMiniQuest quest) {
		markDirty();
		EntityPlayer entityplayer = getPlayer();
		if (entityplayer != null && !entityplayer.worldObj.isRemote) {
			sendMiniQuestPacket((EntityPlayerMP) entityplayer, quest, false);
		}
	}

	public void setTargetFTWaypoint(TESAbstractWaypoint wp) {
		targetFTWaypoint = wp;
		markDirty();
		if (wp != null) {
			setTicksUntilFT(TICKS_UNTIL_FT_MAX);
		} else {
			setTicksUntilFT(0);
		}
	}

	private static class CWPSharedKey {
		private final UUID sharingPlayer;
		private final int waypointID;

		private CWPSharedKey(UUID player, int id) {
			sharingPlayer = player;
			waypointID = id;
		}

		private static CWPSharedKey keyFor(UUID player, int id) {
			return new CWPSharedKey(player, id);
		}

		private UUID getSharingPlayer() {
			return sharingPlayer;
		}

		private int getWaypointID() {
			return waypointID;
		}
	}
}