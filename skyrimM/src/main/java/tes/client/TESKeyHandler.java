package tes.client;

import com.google.common.math.IntMath;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.TES;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESGuiId;
import tes.common.faction.TESFaction;
import tes.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.BitSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class TESKeyHandler {
	public static final TESKeyHandler INSTANCE = new TESKeyHandler();

	public static final KeyBinding KEY_BINDING_MENU = new KeyBinding("Menu", 38, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_MAP_TELEPORT = new KeyBinding("Map Teleport", 50, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_FAST_TRAVEL = new KeyBinding("Fast Travel", 33, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_ALIGNMENT_CYCLE_LEFT = new KeyBinding("Alignment Cycle Left", 203, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_ALIGNMENT_CYCLE_RIGHT = new KeyBinding("Alignment Cycle Right", 205, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_ALIGNMENT_GROUP_PREV = new KeyBinding("Alignment Group Prev", 200, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_ALIGNMENT_GROUP_NEXT = new KeyBinding("Alignment Group Next", 208, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_DRAGON_UP = new KeyBinding("Dragon: Fly Up", Keyboard.KEY_G, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_DRAGON_DOWN = new KeyBinding("Dragon: Fly Down", Keyboard.KEY_H, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_RETURN = new KeyBinding("Clear", Keyboard.KEY_RETURN, "Game of Thrones");
	public static final KeyBinding KEY_BINDING_CARGO_CART = new KeyBinding("Enable cargocart", 19, "Game of Thrones");

	private static final Minecraft MC = Minecraft.getMinecraft();

	private static int alignmentChangeTick;

	static {
		ClientRegistry.registerKeyBinding(KEY_BINDING_MENU);
		ClientRegistry.registerKeyBinding(KEY_BINDING_MAP_TELEPORT);
		ClientRegistry.registerKeyBinding(KEY_BINDING_FAST_TRAVEL);
		ClientRegistry.registerKeyBinding(KEY_BINDING_ALIGNMENT_CYCLE_LEFT);
		ClientRegistry.registerKeyBinding(KEY_BINDING_ALIGNMENT_CYCLE_RIGHT);
		ClientRegistry.registerKeyBinding(KEY_BINDING_ALIGNMENT_GROUP_PREV);
		ClientRegistry.registerKeyBinding(KEY_BINDING_ALIGNMENT_GROUP_NEXT);
		ClientRegistry.registerKeyBinding(KEY_BINDING_DRAGON_UP);
		ClientRegistry.registerKeyBinding(KEY_BINDING_DRAGON_DOWN);
		ClientRegistry.registerKeyBinding(KEY_BINDING_CARGO_CART);
	}

	private final TESPacketDragonControl dcm = new TESPacketDragonControl();

	public static void update() {
		if (alignmentChangeTick > 0) {
			--alignmentChangeTick;
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		TESAttackTiming.doAttackTiming();
		if (KEY_BINDING_MENU.getIsKeyPressed() && MC.currentScreen == null) {
			MC.thePlayer.openGui(TES.instance, TESGuiId.MENU.ordinal(), MC.theWorld, 0, 0, 0);
		}
		TESPlayerData pd = TESLevelData.getData(MC.thePlayer);
		boolean usedAlignmentKeys = false;
		boolean skippedHelp = false;
		Map<TESDimension.DimensionRegion, TESFaction> lastViewedRegions = new EnumMap<>(TESDimension.DimensionRegion.class);
		TESDimension currentDimension = TESDimension.GAME_OF_THRONES;
		TESFaction currentFaction = pd.getViewingFaction();
		TESDimension.DimensionRegion currentRegion = currentFaction.getFactionRegion();
		List<TESDimension.DimensionRegion> regionList = currentDimension.getDimensionRegions();
		List<TESFaction> factionList = currentRegion.getFactionList();
		if (MC.currentScreen == null && alignmentChangeTick <= 0) {
			int i;
			if (KEY_BINDING_RETURN.getIsKeyPressed()) {
				skippedHelp = true;
			}
			if (KEY_BINDING_ALIGNMENT_CYCLE_LEFT.getIsKeyPressed()) {
				i = factionList.indexOf(currentFaction);
				--i;
				i = IntMath.mod(i, factionList.size());
				currentFaction = factionList.get(i);
				usedAlignmentKeys = true;
			}
			if (KEY_BINDING_ALIGNMENT_CYCLE_RIGHT.getIsKeyPressed()) {
				i = factionList.indexOf(currentFaction);
				++i;
				i = IntMath.mod(i, factionList.size());
				currentFaction = factionList.get(i);
				usedAlignmentKeys = true;
			}
			if (regionList != null) {
				if (KEY_BINDING_ALIGNMENT_GROUP_PREV.getIsKeyPressed()) {
					pd.setRegionLastViewedFaction(currentRegion, currentFaction);
					lastViewedRegions.put(currentRegion, currentFaction);
					i = regionList.indexOf(currentRegion);
					--i;
					i = IntMath.mod(i, regionList.size());
					currentRegion = regionList.get(i);
					currentFaction = pd.getRegionLastViewedFaction(currentRegion);
					usedAlignmentKeys = true;
				}
				if (KEY_BINDING_ALIGNMENT_GROUP_NEXT.getIsKeyPressed()) {
					pd.setRegionLastViewedFaction(currentRegion, currentFaction);
					lastViewedRegions.put(currentRegion, currentFaction);
					i = regionList.indexOf(currentRegion);
					++i;
					i = IntMath.mod(i, regionList.size());
					currentRegion = regionList.get(i);
					currentFaction = pd.getRegionLastViewedFaction(currentRegion);
					usedAlignmentKeys = true;
				}
			}
		}
		if (skippedHelp && TESTickHandlerClient.isRenderMenuPrompt()) {
			TESTickHandlerClient.setRenderMenuPrompt(false);
			IMessage packet = new TESPacketCheckMenuPrompt(TESPacketMenuPrompt.Type.MENU);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
		if (usedAlignmentKeys) {
			TESClientProxy.sendClientInfoPacket(currentFaction, lastViewedRegions);
			alignmentChangeTick = 2;
		}
		if (KEY_BINDING_CARGO_CART.isPressed()) {
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(new TESPacketCargocartControl());
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		TESAttackTiming.doAttackTiming();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent evt) {
		BitSet flags = dcm.getFlags();
		flags.set(0, KEY_BINDING_DRAGON_UP.getIsKeyPressed());
		flags.set(1, KEY_BINDING_DRAGON_DOWN.getIsKeyPressed());
		if (dcm.hasChanged()) {
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(dcm);
		}
	}
}