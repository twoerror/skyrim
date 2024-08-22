package tes.common;

import com.mojang.authlib.GameProfile;
import tes.common.block.other.TESBlockFlowerPot;
import tes.common.database.TESAchievement;
import tes.common.database.TESBlocks;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.inanimate.TESEntityBanner;
import tes.common.faction.TESAlignmentBonusMap;
import tes.common.faction.TESFaction;
import tes.common.network.TESPacketFellowshipAcceptInviteResult;
import tes.common.network.TESPacketMenuPrompt;
import tes.common.quest.TESMiniQuest;
import tes.common.util.TESReflection;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESConquestZone;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TESCommonProxy {
	public void addMapPlayerLocation(GameProfile player, double posX, double posZ) {
	}

	public void cancelItemHighlight() {
	}

	public void clearMapPlayerLocations() {
	}

	public void clientReceiveSpeech(TESEntityNPC npc, String speech) {
	}

	public void displayAlignmentSee(String username, Map<TESFaction, Float> alignments) {
	}

	public void displayBannerGui(TESEntityBanner banner) {
	}

	public void displayFellowshipAcceptInvitationResult(UUID fellowshipID, String name, TESPacketFellowshipAcceptInviteResult.AcceptInviteResult result) {
	}

	public void displayFTScreen(TESAbstractWaypoint waypoint, int startX, int startZ) {
	}

	public void displayMenuPrompt(TESPacketMenuPrompt.Type type) {
	}

	public void displayMessage(TESGuiMessageTypes message) {
	}

	public void displayMiniquestOffer(TESMiniQuest quest, TESEntityNPC npc) {
	}

	public void displayNewDate() {
	}

	public void fillMugFromCauldron(World world, int i, int j, int k, int side, ItemStack itemstack) {
		world.setBlockMetadataWithNotify(i, j, k, world.getBlockMetadata(i, j, k) - 1, 3);
	}

	public int getBarrelRenderID() {
		return 0;
	}

	public int getBeaconRenderID() {
		return 0;
	}

	public int getBeamRenderID() {
		return 0;
	}

	public int getBirdCageRenderID() {
		return 0;
	}

	public int getBombRenderID() {
		return 0;
	}

	public int getButterflyJarRenderID() {
		return 0;
	}

	public int getChainRenderID() {
		return 0;
	}

	public int getChestRenderID() {
		return 0;
	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public int getCloverRenderID() {
		return 0;
	}

	public int getCommandTableRenderID() {
		return 0;
	}

	public int getCoralRenderID() {
		return 0;
	}

	public int getDoorRenderID() {
		return 0;
	}

	public int getDoublePlantRenderID() {
		return 0;
	}

	public int getDoubleTorchRenderID() {
		return 0;
	}

	public int getFallenLeavesRenderID() {
		return 0;
	}

	public int getFenceRenderID() {
		return 0;
	}

	public int getFlowerPotRenderID() {
		return 0;
	}

	public int getFlowerRenderID() {
		return 0;
	}

	public int getGrapevineRenderID() {
		return 0;
	}

	public int getGrassRenderID() {
		return 0;
	}

	public int getLeavesRenderID() {
		return 0;
	}

	public int getPlantainRenderID() {
		return 0;
	}

	public int getPlateRenderID() {
		return 0;
	}

	public int getReedsRenderID() {
		return 0;
	}

	public int getRopeRenderID() {
		return 0;
	}

	public int getStalactiteRenderID() {
		return 0;
	}

	public int getThatchFloorRenderID() {
		return 0;
	}

	public int getTrapdoorRenderID() {
		return 0;
	}

	public int getTreasureRenderID() {
		return 0;
	}

	public int getUnsmelteryRenderID() {
		return 0;
	}

	public int getVCauldronRenderID() {
		return 0;
	}

	public int getWasteRenderID() {
		return 0;
	}

	public int getWildFireJarRenderID() {
		return 0;
	}

	public void handleInvasionWatch(int invasionEntityID, boolean overrideAlreadyWatched) {
	}

	public boolean isClient() {
		return false;
	}

	public boolean isSingleplayer() {
		return false;
	}

	public void onInit() {
	}

	public void openHiredNPCGui(TESEntityNPC npc) {
	}

	public void placeFlowerInPot(World world, int i, int j, int k, int side, ItemStack itemstack) {
		world.setBlock(i, j, k, TESBlocks.flowerPot, 0, 3);
		TESBlockFlowerPot.setPlant(world, i, j, k, itemstack);
	}

	public void postInit() {
	}

	public void preInit() {
	}

	public void queueAchievement(TESAchievement achievement) {
	}

	public void queueConquestNotification(TESFaction fac, float conq, boolean isCleansing) {
	}

	public void queueFellowshipNotification(IChatComponent message) {
	}

	public void receiveConquestGrid(TESFaction conqFac, List<TESConquestZone> allZones) {
	}

	public void renderCustomPotionEffect(int x, int y, PotionEffect effect, Minecraft mc) {
	}

	public void setClientDifficulty(EnumDifficulty difficulty) {
	}

	public void setInPortal(EntityPlayer entityplayer) {
		if (!TESTickHandlerServer.PLAYERS_IN_PORTALS.containsKey(entityplayer)) {
			TESTickHandlerServer.PLAYERS_IN_PORTALS.put(entityplayer, 0);
		}
	}

	public void setMapCWPProtectionMessage(IChatComponent message) {
	}

	public void setMapIsOp(boolean isOp) {
	}

	public void setTrackedQuest(TESMiniQuest quest) {
	}

	public void setWaypointModes(boolean showWP, boolean showCWP, boolean showHiddenSWP) {
	}

	public void showBurnOverlay() {
	}

	public void showFrostOverlay() {
	}

	public void spawnAlignmentBonus(TESFaction faction, float prevMainAlignment, TESAlignmentBonusMap factionBonusMap, String name, float conquestBonus, double posX, double posY, double posZ) {
	}

	public void spawnParticle(String type, double d, double d1, double d2, double d3, double d4, double d5) {
	}

	public void testReflection(World world) {
		TESReflection.testAll(world);
	}

	public void validateBannerUsername(TESEntityBanner banner, int slot, String prevText, boolean valid) {
	}

}