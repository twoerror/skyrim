package tes.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import tes.TES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class TESCommonFactory {
	private static TESEventHandler eventHandler;
	private static TESFuelHandler fuelHandler;
	private static TESGuiHandler guiHandler;
	private static TESTickHandlerServer tickHandlerServer;

	private TESCommonFactory() {
	}

	public static void preInit() {
		Minecraft mc = Minecraft.getMinecraft();
		IResourceManager resourceManager = mc.getResourceManager();
		EventBus forgeBus = MinecraftForge.EVENT_BUS;
		EventBus terrainBus = MinecraftForge.TERRAIN_GEN_BUS;
		EventBus fmlBus = FMLCommonHandler.instance().bus();

		guiHandler = TESGuiHandler.INSTANCE;
		NetworkRegistry.INSTANCE.registerGuiHandler(TES.instance, guiHandler);

		eventHandler = TESEventHandler.INSTANCE;
		fmlBus.register(eventHandler);
		forgeBus.register(eventHandler);
		terrainBus.register(eventHandler);

		fuelHandler = TESFuelHandler.INSTANCE;
		GameRegistry.registerFuelHandler(fuelHandler);

		tickHandlerServer = TESTickHandlerServer.INSTANCE;
		fmlBus.register(tickHandlerServer);
	}

	public static TESEventHandler getEventHandler() {
		return eventHandler;
	}

	public static IFuelHandler getFuelHandler() {
		return fuelHandler;
	}

	public static TESGuiHandler getGuiHandler() {
		return guiHandler;
	}

	public static TESTickHandlerServer getTickHandlerServer() {
		return tickHandlerServer;
	}
}