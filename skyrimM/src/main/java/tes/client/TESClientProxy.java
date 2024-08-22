package tes.client;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import tes.client.effect.*;
import tes.client.gui.*;
import tes.client.model.TESArmorModel;
import tes.client.render.TESRender;
import tes.client.render.other.*;
import tes.common.TESCommonProxy;
import tes.common.TESDimension;
import tes.common.TESGuiMessageTypes;
import tes.common.TESTickHandlerServer;
import tes.common.database.TESAchievement;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.inanimate.TESEntityBanner;
import tes.common.entity.other.inanimate.TESEntityInvasionSpawner;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.entity.other.utils.TESInvasionStatus;
import tes.common.faction.TESAlignmentBonusMap;
import tes.common.faction.TESFaction;
import tes.common.network.TESPacketClientInfo;
import tes.common.network.TESPacketFellowshipAcceptInviteResult;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketMenuPrompt;
import tes.common.quest.TESMiniQuest;
import tes.common.tileentity.*;
import tes.common.util.TESFunctions;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESConquestZone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TESClientProxy extends TESCommonProxy {
	public static final ResourceLocation ENCHANTMENT_TEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	public static final ResourceLocation ALIGNMENT_TEXTURE = new ResourceLocation("tes:textures/gui/alignment.png");
	public static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("tes:textures/misc/particles.png");
	public static final ResourceLocation PARTICLES_2_TEXTURE = new ResourceLocation("tes:textures/misc/particles2.png");

	public static final int TESSELLATOR_MAX_BRIGHTNESS = 15728880;

	private static final ResourceLocation CUSTOM_POTIONS_TEXTURE = new ResourceLocation("tes:textures/gui/effects.png");

	private int beaconRenderID;
	private int barrelRenderID;
	private int bombRenderID;
	private int doubleTorchRenderID;
	private int plateRenderID;
	private int stalactiteRenderID;
	private int flowerPotRenderID;
	private int cloverRenderID;
	private int plantainRenderID;
	private int fenceRenderID;
	private int grassRenderID;
	private int fallenLeavesRenderID;
	private int leavesRenderID;
	private int commandTableRenderID;
	private int butterflyJarRenderID;
	private int unsmelteryRenderID;
	private int chestRenderID;
	private int reedsRenderID;
	private int wasteRenderID;
	private int beamRenderID;
	private int vCauldronRenderID;
	private int grapevineRenderID;
	private int thatchFloorRenderID;
	private int treasureRenderID;
	private int flowerRenderID;
	private int doublePlantRenderID;
	private int birdCageRenderID;
	private int wildFireJarRenderID;
	private int coralRenderID;
	private int doorRenderID;
	private int ropeRenderID;
	private int chainRenderID;
	private int trapdoorRenderID;

	public static boolean doesClientChunkExist(World world, int i, int k) {
		int chunkX = i >> 4;
		int chunkZ = k >> 4;
		Chunk chunk = world.getChunkProvider().provideChunk(chunkX, chunkZ);
		return !(chunk instanceof EmptyChunk);
	}

	public static int getAlphaInt(float alphaF) {
		int alphaI = (int) (alphaF * 255.0F);
		return MathHelper.clamp_int(alphaI, 4, 255);
	}

	public static void renderEnchantmentEffect() {
		Tessellator tessellator = Tessellator.instance;
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		GL11.glDepthFunc(514);
		GL11.glDisable(2896);
		texturemanager.bindTexture(ENCHANTMENT_TEXTURE);
		GL11.glEnable(3042);
		GL11.glBlendFunc(768, 1);
		float shade = 0.76F;
		GL11.glColor4f(0.5F * shade, 0.25F * shade, 0.8F * shade, 1.0F);
		GL11.glMatrixMode(5890);
		GL11.glPushMatrix();
		float scale = 0.125F;
		GL11.glScalef(scale, scale, scale);
		float randomShift = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
		GL11.glTranslatef(randomShift, 0.0F, 0.0F);
		GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		randomShift = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
		GL11.glTranslatef(-randomShift, 0.0F, 0.0F);
		GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
		ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glDisable(3042);
		GL11.glEnable(2896);
		GL11.glDepthFunc(515);
	}

	public static void sendClientInfoPacket(TESFaction viewingFaction, Map<TESDimension.DimensionRegion, TESFaction> changedRegionMap) {
		boolean showWP = TESGuiMap.isShowWP();
		boolean showCWP = TESGuiMap.isShowCWP();
		boolean showHiddenSWP = TESGuiMap.isShowHiddenSWP();
		IMessage packet = new TESPacketClientInfo(viewingFaction, changedRegionMap, showWP, showCWP, showHiddenSWP);
		TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
	}

	@Override
	public void addMapPlayerLocation(GameProfile player, double posX, double posZ) {
		TESGuiMap.addPlayerLocationInfo(player, posX, posZ);
	}

	@Override
	public void cancelItemHighlight() {
		TESClientFactory.getTickHandlerClient().setCancelItemHighlight(true);
	}

	@Override
	public void clearMapPlayerLocations() {
		TESGuiMap.clearPlayerLocations();
	}

	@Override
	public void clientReceiveSpeech(TESEntityNPC npc, String speech) {
		TESSpeechClient.receiveSpeech(npc, speech);
	}

	@Override
	public void displayAlignmentSee(String username, Map<TESFaction, Float> alignments) {
		TESGuiFactions gui = new TESGuiFactions();
		gui.setOtherPlayer(username, alignments);
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(gui);
	}

	@Override
	public void displayBannerGui(TESEntityBanner banner) {
		Minecraft mc = Minecraft.getMinecraft();
		TESGuiBanner gui = new TESGuiBanner(banner);
		mc.displayGuiScreen(gui);
	}

	@Override
	public void displayFellowshipAcceptInvitationResult(UUID fellowshipID, String name, TESPacketFellowshipAcceptInviteResult.AcceptInviteResult result) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if (gui instanceof TESGuiFellowships) {
			((TESGuiFellowships) gui).displayAcceptInvitationResult(fellowshipID, name, result);
		}
	}

	@Override
	public void displayFTScreen(TESAbstractWaypoint waypoint, int startX, int startZ) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(new TESGuiFastTravel(waypoint, startX, startZ));
	}

	@Override
	public void displayMenuPrompt(TESPacketMenuPrompt.Type type) {
		if (type == TESPacketMenuPrompt.Type.MENU) {
			TESTickHandlerClient.setRenderMenuPrompt(true);
		}
	}

	@Override
	public void displayMessage(TESGuiMessageTypes message) {
		Minecraft.getMinecraft().displayGuiScreen(new TESGuiMessage(message));
	}

	@Override
	public void displayMiniquestOffer(TESMiniQuest quest, TESEntityNPC npc) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(new TESGuiMiniquestOffer(quest, npc));
	}

	@Override
	public void displayNewDate() {
		TESClientFactory.getTickHandlerClient().updateDate();
	}

	@Override
	public void fillMugFromCauldron(World world, int i, int j, int k, int side, ItemStack itemstack) {
		if (world.isRemote) {
			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(i, j, k, side, itemstack, 0.0F, 0.0F, 0.0F));
		} else {
			super.fillMugFromCauldron(world, i, j, k, side, itemstack);
		}
	}

	@Override
	public int getBarrelRenderID() {
		return barrelRenderID;
	}

	@Override
	public int getBeaconRenderID() {
		return beaconRenderID;
	}

	@Override
	public int getBeamRenderID() {
		return beamRenderID;
	}

	@Override
	public int getBirdCageRenderID() {
		return birdCageRenderID;
	}

	@Override
	public int getBombRenderID() {
		return bombRenderID;
	}

	@Override
	public int getButterflyJarRenderID() {
		return butterflyJarRenderID;
	}

	@Override
	public int getChainRenderID() {
		return chainRenderID;
	}

	@Override
	public int getChestRenderID() {
		return chestRenderID;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public int getCloverRenderID() {
		return cloverRenderID;
	}

	@Override
	public int getCommandTableRenderID() {
		return commandTableRenderID;
	}

	@Override
	public int getCoralRenderID() {
		return coralRenderID;
	}

	@Override
	public int getDoorRenderID() {
		return doorRenderID;
	}

	@Override
	public int getDoublePlantRenderID() {
		return doublePlantRenderID;
	}

	@Override
	public int getDoubleTorchRenderID() {
		return doubleTorchRenderID;
	}

	@Override
	public int getFallenLeavesRenderID() {
		return fallenLeavesRenderID;
	}

	@Override
	public int getFenceRenderID() {
		return fenceRenderID;
	}

	@Override
	public int getFlowerPotRenderID() {
		return flowerPotRenderID;
	}

	@Override
	public int getFlowerRenderID() {
		return flowerRenderID;
	}

	@Override
	public int getGrapevineRenderID() {
		return grapevineRenderID;
	}

	@Override
	public int getGrassRenderID() {
		return grassRenderID;
	}

	@Override
	public int getLeavesRenderID() {
		return leavesRenderID;
	}

	@Override
	public int getPlantainRenderID() {
		return plantainRenderID;
	}

	@Override
	public int getPlateRenderID() {
		return plateRenderID;
	}

	@Override
	public int getReedsRenderID() {
		return reedsRenderID;
	}

	@Override
	public int getRopeRenderID() {
		return ropeRenderID;
	}

	@Override
	public int getStalactiteRenderID() {
		return stalactiteRenderID;
	}

	@Override
	public int getThatchFloorRenderID() {
		return thatchFloorRenderID;
	}

	@Override
	public int getTrapdoorRenderID() {
		return trapdoorRenderID;
	}

	@Override
	public int getTreasureRenderID() {
		return treasureRenderID;
	}

	@Override
	public int getUnsmelteryRenderID() {
		return unsmelteryRenderID;
	}

	@Override
	public int getVCauldronRenderID() {
		return vCauldronRenderID;
	}

	@Override
	public int getWasteRenderID() {
		return wasteRenderID;
	}

	@Override
	public int getWildFireJarRenderID() {
		return wildFireJarRenderID;
	}

	@Override
	public void handleInvasionWatch(int invasionEntityID, boolean overrideAlreadyWatched) {
		TESInvasionStatus status = TESTickHandlerClient.WATCHED_INVASION;
		if (overrideAlreadyWatched || !status.isActive()) {
			World world = getClientWorld();
			Entity e = world.getEntityByID(invasionEntityID);
			if (e instanceof TESEntityInvasionSpawner) {
				status.setWatchedInvasion((TESEntityInvasionSpawner) e);
			}
		}
	}

	@Override
	public boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}

	@Override
	public boolean isSingleplayer() {
		return Minecraft.getMinecraft().isSingleplayer();
	}


	@Override
	public void onInit() {
		TESClientFactory.onInit();
		TESTextures.onInit();
		TESRender.onInit();
		for (Map.Entry<Class<? extends Entity>, Render> cl : TESRender.RENDERS.entrySet()) {
			RenderingRegistry.registerEntityRenderingHandler(cl.getKey(), cl.getValue());
		}
		beaconRenderID = RenderingRegistry.getNextAvailableRenderId();
		barrelRenderID = RenderingRegistry.getNextAvailableRenderId();
		bombRenderID = RenderingRegistry.getNextAvailableRenderId();
		doubleTorchRenderID = RenderingRegistry.getNextAvailableRenderId();
		plateRenderID = RenderingRegistry.getNextAvailableRenderId();
		stalactiteRenderID = RenderingRegistry.getNextAvailableRenderId();
		flowerPotRenderID = RenderingRegistry.getNextAvailableRenderId();
		cloverRenderID = RenderingRegistry.getNextAvailableRenderId();
		fenceRenderID = RenderingRegistry.getNextAvailableRenderId();
		grassRenderID = RenderingRegistry.getNextAvailableRenderId();
		fallenLeavesRenderID = RenderingRegistry.getNextAvailableRenderId();
		commandTableRenderID = RenderingRegistry.getNextAvailableRenderId();
		butterflyJarRenderID = RenderingRegistry.getNextAvailableRenderId();
		unsmelteryRenderID = RenderingRegistry.getNextAvailableRenderId();
		chestRenderID = RenderingRegistry.getNextAvailableRenderId();
		reedsRenderID = RenderingRegistry.getNextAvailableRenderId();
		wasteRenderID = RenderingRegistry.getNextAvailableRenderId();
		beamRenderID = RenderingRegistry.getNextAvailableRenderId();
		vCauldronRenderID = RenderingRegistry.getNextAvailableRenderId();
		grapevineRenderID = RenderingRegistry.getNextAvailableRenderId();
		thatchFloorRenderID = RenderingRegistry.getNextAvailableRenderId();
		treasureRenderID = RenderingRegistry.getNextAvailableRenderId();
		flowerRenderID = RenderingRegistry.getNextAvailableRenderId();
		doublePlantRenderID = RenderingRegistry.getNextAvailableRenderId();
		birdCageRenderID = RenderingRegistry.getNextAvailableRenderId();
		wildFireJarRenderID = RenderingRegistry.getNextAvailableRenderId();
		coralRenderID = RenderingRegistry.getNextAvailableRenderId();
		doorRenderID = RenderingRegistry.getNextAvailableRenderId();
		ropeRenderID = RenderingRegistry.getNextAvailableRenderId();
		chainRenderID = RenderingRegistry.getNextAvailableRenderId();
		trapdoorRenderID = RenderingRegistry.getNextAvailableRenderId();
		plantainRenderID = RenderingRegistry.getNextAvailableRenderId();
		leavesRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(plantainRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(leavesRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(beaconRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(barrelRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(bombRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(doubleTorchRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(plateRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(stalactiteRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(flowerPotRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(cloverRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(fenceRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(grassRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(fallenLeavesRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(commandTableRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(butterflyJarRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(unsmelteryRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(chestRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(reedsRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(wasteRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(beamRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(vCauldronRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(grapevineRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(thatchFloorRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(treasureRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(flowerRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(doublePlantRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(birdCageRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(wildFireJarRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(coralRenderID, new TESRenderBlocks(true));
		RenderingRegistry.registerBlockHandler(doorRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(ropeRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(chainRenderID, new TESRenderBlocks(false));
		RenderingRegistry.registerBlockHandler(trapdoorRenderID, new TESRenderBlocks(true));
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityBeacon.class, new TESRenderBeacon());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityPlate.class, new TESRenderPlateFood());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntitySpawnerChest.class, new TESRenderSpawnerChest());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityMug.class, new TESRenderMug());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityCommandTable.class, new TESRenderCommandTable());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityAnimalJar.class, new TESRenderAnimalJar());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityUnsmeltery.class, new TESRenderUnsmeltery());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntitySarbacaneTrap.class, new TESRenderDartTrap());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityChest.class, new TESRenderChest());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityWeaponRack.class, new TESRenderWeaponRack());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntityKebabStand.class, new TESRenderKebabStand());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntitySignCarved.class, new TESRenderSignCarved());
		ClientRegistry.bindTileEntitySpecialRenderer(TESTileEntitySignCarvedValyrian.class, new TESRenderSignCarvedValyrian());
		TESClientFactory.getThirdPersonViewer().init();
	}

	@Override
	public void openHiredNPCGui(TESEntityNPC npc) {
		Minecraft mc = Minecraft.getMinecraft();
		if (npc.getHireableInfo().getHiredTask() == TESHireableInfo.Task.WARRIOR) {
			mc.displayGuiScreen(new TESGuiHiredWarrior(npc));
		} else if (npc.getHireableInfo().getHiredTask() == TESHireableInfo.Task.FARMER) {
			mc.displayGuiScreen(new TESGuiHiredFarmer(npc));
		}
	}

	@Override
	public void placeFlowerInPot(World world, int i, int j, int k, int side, ItemStack itemstack) {
		if (world.isRemote) {
			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(i, j, k, side, itemstack, 0.0F, 0.0F, 0.0F));
		} else {
			super.placeFlowerInPot(world, i, j, k, side, itemstack);
		}
	}

	@Override
	public void postInit() {
		TESClientFactory.postInit();
	}

	@Override
	public void preInit() {
		System.setProperty("fml.skipFirstTextureLoad", "false");
		TESClientFactory.preInit();
		TESArmorModel.setupArmorModels();
	}

	@Override
	public void queueAchievement(TESAchievement achievement) {
		TESClientFactory.getNotificationDisplay().queueAchievement(achievement);
	}

	@Override
	public void queueConquestNotification(TESFaction fac, float conq, boolean isCleansing) {
		TESClientFactory.getNotificationDisplay().queueConquest(fac, conq, isCleansing);
	}

	@Override
	public void queueFellowshipNotification(IChatComponent message) {
		TESClientFactory.getNotificationDisplay().queueFellowshipNotification(message);
	}

	@Override
	public void receiveConquestGrid(TESFaction conqFac, List<TESConquestZone> allZones) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if (gui instanceof TESGuiMap) {
			((TESGuiMap) gui).receiveConquestGrid(conqFac, allZones);
		}
	}

	@Override
	public void renderCustomPotionEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		Potion potion = Potion.potionTypes[effect.getPotionID()];
		mc.getTextureManager().bindTexture(CUSTOM_POTIONS_TEXTURE);
		int l = potion.getStatusIconIndex();
		GuiScreen screen = mc.currentScreen;
		if (screen != null) {
			screen.drawTexturedModalRect(x + 6, y + 7, l % 8 * 18, l / 8 * 18, 18, 18);
		}
	}

	@Override
	public void setClientDifficulty(EnumDifficulty difficulty) {
		Minecraft.getMinecraft().gameSettings.difficulty = difficulty;
	}

	@Override
	public void setInPortal(EntityPlayer entityplayer) {
		if (!TESTickHandlerClient.PLAYERS_IN_PORTALS.containsKey(entityplayer)) {
			TESTickHandlerClient.PLAYERS_IN_PORTALS.put(entityplayer, 0);
		}
		if (Minecraft.getMinecraft().isSingleplayer() && !TESTickHandlerServer.PLAYERS_IN_PORTALS.containsKey(entityplayer)) {
			TESTickHandlerServer.PLAYERS_IN_PORTALS.put(entityplayer, 0);
		}
	}

	@Override
	public void setMapCWPProtectionMessage(IChatComponent message) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if (gui instanceof TESGuiMap) {
			((TESGuiMap) gui).setCWPProtectionMessage(message);
		}
	}

	@Override
	public void setMapIsOp(boolean isOp) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if (gui instanceof TESGuiMap) {
			TESGuiMap map = (TESGuiMap) gui;
			map.setPlayerOp(isOp);
		}
	}

	@Override
	public void setTrackedQuest(TESMiniQuest quest) {
		TESClientFactory.getMiniquestTracker().setTrackedQuest(quest);
	}

	@Override
	public void setWaypointModes(boolean showWP, boolean showCWP, boolean showHiddenSWP) {
		TESGuiMap.setShowWP(showWP);
		TESGuiMap.setShowCWP(showCWP);
		TESGuiMap.setShowHiddenSWP(showHiddenSWP);
	}

	@Override
	public void showBurnOverlay() {
		TESClientFactory.getTickHandlerClient().onBurnDamage();
	}

	@Override
	public void showFrostOverlay() {
		TESClientFactory.getTickHandlerClient().onFrostDamage();
	}

	@Override
	public void spawnAlignmentBonus(TESFaction faction, float prevMainAlignment, TESAlignmentBonusMap factionBonusMap, String name, float conquestBonus, double posX, double posY, double posZ) {
		World world = getClientWorld();
		if (world != null) {
			TESEntityAlignmentBonus entity = new TESEntityAlignmentBonus(world, posX, posY, posZ, name, faction, prevMainAlignment, factionBonusMap, conquestBonus);
			world.spawnEntityInWorld(entity);
		}
	}

	@Override
	public void spawnParticle(String type, double d, double d1, double d2, double d3, double d4, double d5) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.renderViewEntity != null && mc.theWorld != null) {
			WorldClient worldClient = mc.theWorld;
			Random rand = worldClient.rand;
			int i = mc.gameSettings.particleSetting;
			if (i == 1 && rand.nextInt(3) == 0) {
				i = 2;
			}
			if (i > 1) {
				return;
			}
			if ("angry".equals(type)) {
				TESClientFactory.getEffectRenderer().addEffect(new TESEntityAngryFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("chill".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityChillFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("largeStone".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityLargeBlockFX(worldClient, d, d1, d2, d3, d4, d5, Blocks.stone, 0));
			} else if (type.startsWith("leaf")) {
				String s = type.substring("leaf".length());
				int[] texIndices = null;
				if (s.startsWith("Gold")) {
					if (rand.nextBoolean()) {
						texIndices = TESFunctions.intRange(0, 5);
					} else {
						texIndices = TESFunctions.intRange(8, 13);
					}
				} else if (s.startsWith("Red")) {
					if (rand.nextBoolean()) {
						texIndices = TESFunctions.intRange(16, 21);
					} else {
						texIndices = TESFunctions.intRange(24, 29);
					}
				} else if (s.startsWith("Mirk")) {
					if (rand.nextBoolean()) {
						texIndices = TESFunctions.intRange(32, 37);
					} else {
						texIndices = TESFunctions.intRange(40, 45);
					}
				} else if (s.startsWith("Green")) {
					if (rand.nextBoolean()) {
						texIndices = TESFunctions.intRange(48, 53);
					} else {
						texIndices = TESFunctions.intRange(56, 61);
					}
				}
				if (texIndices != null) {
					if (type.indexOf('_') > -1) {
						int age = Integer.parseInt(type.substring(type.indexOf('_') + 1));
						TESClientFactory.getEffectRenderer().addEffect(new TESEntityLeafFX(worldClient, d, d1, d2, d3, d4, d5, texIndices, age));
					} else {
						TESClientFactory.getEffectRenderer().addEffect(new TESEntityLeafFX(worldClient, d, d1, d2, d3, d4, d5, texIndices));
					}
				}
			} else if ("marshFlame".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityMarshFlameFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("marshLight".equals(type)) {
				TESClientFactory.getEffectRenderer().addEffect(new TESEntityMarshLightFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("ulthosWater".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityRiverWaterFX(worldClient, d, d1, d2, d3, d4, d5, TESBiome.ulthosForest.getWaterColorMultiplier()));
			} else if ("asshaiTorch".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityAsshaiTorchFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("asshaiWater".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityRiverWaterFX(worldClient, d, d1, d2, d3, d4, d5, TESBiome.shadowLand.getWaterColorMultiplier()));
			} else if ("pickpocket".equals(type)) {
				TESClientFactory.getEffectRenderer().addEffect(new TESEntityPickpocketFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("pickpocketFail".equals(type)) {
				TESClientFactory.getEffectRenderer().addEffect(new TESEntityPickpocketFailFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("wave".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityWaveFX(worldClient, d, d1, d2, d3, d4, d5));
			} else if ("whiteSmoke".equals(type)) {
				mc.effectRenderer.addEffect(new TESEntityWhiteSmokeFX(worldClient, d, d1, d2, d3, d4, d5));
			}
		}
	}

	@Override
	public void testReflection(World world) {
		super.testReflection(world);
		TESReflectionClient.testAll(Minecraft.getMinecraft());
	}

	@Override
	public void validateBannerUsername(TESEntityBanner banner, int slot, String prevText, boolean valid) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if (gui instanceof TESGuiBanner) {
			TESGuiBanner guiBanner = (TESGuiBanner) gui;
			if (guiBanner.getTheBanner() == banner) {
				guiBanner.validateUsername(slot, prevText, valid);
			}
		}
	}
}