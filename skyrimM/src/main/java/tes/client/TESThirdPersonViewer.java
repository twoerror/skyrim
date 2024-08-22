package tes.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import tes.common.entity.animal.TESEntityElephant;
import tes.common.entity.animal.TESEntityMammoth;
import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class TESThirdPersonViewer {
	public static final TESThirdPersonViewer INSTANCE = new TESThirdPersonViewer();

	private static final String[] ENTITYRENDERER_THIRDPERSONDISTANCE = {"thirdPersonDistance", "field_78490_B"};
	private static final Minecraft MC = Minecraft.getMinecraft();

	private boolean ridingDragonPrev;
	private boolean ridingMammothPrev;
	private float defaultThirdPersonDistance;
	private int noticeTicks;

	private TESThirdPersonViewer() {
	}

	private static float getThirdPersonDistance() {
		return ReflectionHelper.getPrivateValue(EntityRenderer.class, MC.entityRenderer, ENTITYRENDERER_THIRDPERSONDISTANCE);
	}

	private static void setThirdPersonDistance(float thirdPersonDistance) {
		ReflectionHelper.setPrivateValue(EntityRenderer.class, MC.entityRenderer, thirdPersonDistance, ENTITYRENDERER_THIRDPERSONDISTANCE);
	}

	public void init() {
		defaultThirdPersonDistance = getThirdPersonDistance();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent evt) {
		if (evt.phase != TickEvent.Phase.START || MC.thePlayer == null) {
			return;
		}
		boolean ridingDragon = MC.thePlayer.ridingEntity instanceof TESEntityDragon;
		boolean ridingMammoth = MC.thePlayer.ridingEntity instanceof TESEntityMammoth || MC.thePlayer.ridingEntity instanceof TESEntityElephant;
		if (ridingMammoth && !ridingMammothPrev) {
			setThirdPersonDistance(9);
		} else if (!ridingMammoth && ridingMammothPrev) {
			setThirdPersonDistance(defaultThirdPersonDistance);
		}
		ridingMammothPrev = ridingMammoth;
		if (ridingDragon && !ridingDragonPrev) {
			setThirdPersonDistance(6);
			noticeTicks = 70;
		} else if (!ridingDragon && ridingDragonPrev) {
			setThirdPersonDistance(defaultThirdPersonDistance);
			noticeTicks = 0;
		} else {
			if (noticeTicks > 0) {
				noticeTicks--;
			}
			if (noticeTicks == 1) {
				String keyUpName = GameSettings.getKeyDisplayString(TESKeyHandler.KEY_BINDING_DRAGON_UP.getKeyCode());
				String keyDownName = GameSettings.getKeyDisplayString(TESKeyHandler.KEY_BINDING_DRAGON_DOWN.getKeyCode());
				MC.ingameGUI.func_110326_a(I18n.format("dragon.mountNotice", keyUpName, keyDownName), false);
			}
		}
		ridingDragonPrev = ridingDragon;
	}
}