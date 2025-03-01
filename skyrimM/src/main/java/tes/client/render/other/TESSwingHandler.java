package tes.client.render.other;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import tes.client.TESAttackTiming;
import tes.common.item.TESWeaponStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TESSwingHandler {
	public static final TESSwingHandler INSTANCE = new TESSwingHandler();
	private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	private static final Map<EntityLivingBase, SwingTime> ENTITY_SWINGS = new HashMap<>();

	private static final float SWING_FACTOR = 0.8f;

	private static void tryUpdateSwing(EntityLivingBase entity) {
		if (entity == MINECRAFT.thePlayer) {
			if (TESAttackTiming.getFullAttackTime() > 0) {
				float max = TESAttackTiming.getFullAttackTime();
				float swing = (max - TESAttackTiming.getAttackTime()) / max;
				float pre = (max - TESAttackTiming.getPrevAttackTime()) / max;
				swing /= SWING_FACTOR;
				pre /= SWING_FACTOR;
				if (swing <= 1.0f) {
					entity.swingProgress = swing;
					entity.prevSwingProgress = pre;
				}
			}
		} else {
			SwingTime swt = ENTITY_SWINGS.get(entity);
			if (swt != null) {
				entity.swingProgress = (float) swt.getSwing() / swt.getSwingMax();
				entity.prevSwingProgress = (float) swt.getSwingPrev() / swt.getSwingMax();
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (MINECRAFT.theWorld == null) {
				ENTITY_SWINGS.clear();
			} else if (!MINECRAFT.isGamePaused()) {
				Collection<EntityLivingBase> removes = new ArrayList<>();
				for (Map.Entry<EntityLivingBase, SwingTime> e : ENTITY_SWINGS.entrySet()) {
					EntityLivingBase entity = e.getKey();
					SwingTime swt = e.getValue();
					swt.setSwingPrev(swt.getSwing());
					swt.setSwing(swt.getSwing() + 1);
					if (swt.getSwing() > swt.getSwingMax()) {
						removes.add(entity);
					}
				}
				for (EntityLivingBase entity : removes) {
					ENTITY_SWINGS.remove(entity);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		ItemStack item;
		SwingTime swt;
		EntityLivingBase entity = event.entityLiving;
		World world = entity.worldObj;
		if (world.isRemote && ENTITY_SWINGS.get(entity) == null && entity.isSwingInProgress && entity.swingProgressInt == 0 && TESWeaponStats.isMeleeWeapon(item = entity.getHeldItem())) {
			int time;
			time = TESWeaponStats.getAttackTimePlayer(item);
			time = Math.round(time * SWING_FACTOR);
			swt = new SwingTime();
			swt.setSwing(1);
			swt.setSwingPrev(0);
			swt.setSwingMax(time);
			ENTITY_SWINGS.put(entity, swt);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		EntityClientPlayerMP entityplayer;
		if (event.phase == TickEvent.Phase.START && (entityplayer = MINECRAFT.thePlayer) != null) {
			tryUpdateSwing(entityplayer);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void preRenderLiving(RenderLivingEvent.Pre event) {
		tryUpdateSwing(event.entity);
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void preRenderPlayer(RenderPlayerEvent.Pre event) {
		tryUpdateSwing(event.entityPlayer);
	}

	private static class SwingTime {
		private int swingPrev;
		private int swing;
		private int swingMax;

		private int getSwingPrev() {
			return swingPrev;
		}

		private void setSwingPrev(int swingPrev) {
			this.swingPrev = swingPrev;
		}

		private int getSwing() {
			return swing;
		}

		private void setSwing(int swing) {
			this.swing = swing;
		}

		private int getSwingMax() {
			return swingMax;
		}

		private void setSwingMax(int swingMax) {
			this.swingMax = swingMax;
		}
	}
}