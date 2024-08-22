package tes.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import tes.client.effect.TESEffectRenderer;
import tes.client.gui.TESGuiMiniquestTracker;
import tes.client.gui.TESGuiNotificationDisplay;
import tes.client.render.other.TESRenderPlayer;
import tes.client.render.other.TESSwingHandler;
import tes.client.sound.TESAmbience;
import tes.client.sound.TESMusic;
import tes.common.database.TESArmorModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class TESClientFactory {
	private static TESAmbience ambienceTicker;
	private static TESArmorModels armorModels;
	private static TESEffectRenderer effectRenderer;
	private static TESGuiEventHandler guiEventHandler;
	private static TESGuiMiniquestTracker miniquestTracker;
	private static TESGuiNotificationDisplay notificationDisplay;
	private static TESItemRendererManager itemRendererManager;
	private static TESKeyHandler keyHandler;
	private static TESMusic musicHandler;
	private static TESRenderPlayer renderPlayer;
	private static TESSwingHandler swingHandler;
	private static TESThirdPersonViewer thirdPersonViewer;
	private static TESTickHandlerClient tickHandlerClient;
	private static TESTextures textures;

	private TESClientFactory() {
	}

	public static void preInit() {
		Minecraft mc = Minecraft.getMinecraft();
		IResourceManager resourceManager = mc.getResourceManager();
		EventBus forgeBus = MinecraftForge.EVENT_BUS;
		EventBus fmlBus = FMLCommonHandler.instance().bus();

		renderPlayer = TESRenderPlayer.INSTANCE;
		fmlBus.register(renderPlayer);
		forgeBus.register(renderPlayer);

		swingHandler = TESSwingHandler.INSTANCE;
		fmlBus.register(swingHandler);
		forgeBus.register(swingHandler);

		tickHandlerClient = TESTickHandlerClient.INSTANCE;
		fmlBus.register(tickHandlerClient);
		forgeBus.register(tickHandlerClient);

		miniquestTracker = TESGuiMiniquestTracker.INSTANCE;
		notificationDisplay = TESGuiNotificationDisplay.INSTANCE;

		ambienceTicker = TESAmbience.INSTANCE;
		fmlBus.register(ambienceTicker);
		forgeBus.register(ambienceTicker);

		guiEventHandler = TESGuiEventHandler.INSTANCE;
		fmlBus.register(guiEventHandler);
		forgeBus.register(guiEventHandler);

		itemRendererManager = TESItemRendererManager.INSTANCE;
		itemRendererManager.onResourceManagerReload(resourceManager);
		((IReloadableResourceManager) resourceManager).registerReloadListener(itemRendererManager);
		forgeBus.register(itemRendererManager);
		armorModels = TESArmorModels.INSTANCE;
		forgeBus.register(armorModels);
	}

	public static void onInit() {
		Minecraft mc = Minecraft.getMinecraft();
		IResourceManager resourceManager = mc.getResourceManager();
		EventBus forgeBus = MinecraftForge.EVENT_BUS;
		EventBus fmlBus = FMLCommonHandler.instance().bus();

		textures = TESTextures.INSTANCE;
		textures.onResourceManagerReload(resourceManager);
		((IReloadableResourceManager) resourceManager).registerReloadListener(textures);
		forgeBus.register(textures);

		effectRenderer = TESEffectRenderer.INSTANCE;

		thirdPersonViewer = TESThirdPersonViewer.INSTANCE;
		fmlBus.register(thirdPersonViewer);

		keyHandler = TESKeyHandler.INSTANCE;
		fmlBus.register(keyHandler);
	}

	public static void postInit() {
		Minecraft mc = Minecraft.getMinecraft();
		IResourceManager resourceManager = mc.getResourceManager();
		EventBus forgeBus = MinecraftForge.EVENT_BUS;
		EventBus fmlBus = FMLCommonHandler.instance().bus();

		musicHandler = TESMusic.INSTANCE;
		((IReloadableResourceManager) resourceManager).registerReloadListener(musicHandler);
		forgeBus.register(musicHandler);
	}

	public static TESAmbience getAmbienceTicker() {
		return ambienceTicker;
	}

	public static TESArmorModels getArmorModels() {
		return armorModels;
	}

	public static TESEffectRenderer getEffectRenderer() {
		return effectRenderer;
	}

	public static TESGuiEventHandler getGuiEventHandler() {
		return guiEventHandler;
	}

	public static TESGuiMiniquestTracker getMiniquestTracker() {
		return miniquestTracker;
	}

	public static TESGuiNotificationDisplay getNotificationDisplay() {
		return notificationDisplay;
	}

	public static TESItemRendererManager getItemRendererManager() {
		return itemRendererManager;
	}

	public static TESKeyHandler getKeyHandler() {
		return keyHandler;
	}

	public static TESMusic getMusicHandler() {
		return musicHandler;
	}

	public static TESRenderPlayer getRenderPlayer() {
		return renderPlayer;
	}

	public static TESSwingHandler getSwingHandler() {
		return swingHandler;
	}

	public static TESThirdPersonViewer getThirdPersonViewer() {
		return thirdPersonViewer;
	}

	public static TESTickHandlerClient getTickHandlerClient() {
		return tickHandlerClient;
	}

	public static TESTextures getTextures() {
		return textures;
	}
}