package tes.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tes.client.render.other.*;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import tes.common.item.other.TESItemAnimalJar;
import tes.common.item.weapon.TESItemBow;
import tes.common.item.weapon.TESItemCrossbow;
import tes.common.item.weapon.TESItemSword;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.ArrayList;
import java.util.Collection;

public class TESItemRendererManager implements IResourceManagerReloadListener {
	public static final TESItemRendererManager INSTANCE = new TESItemRendererManager();

	private static final Collection<TESRenderLargeItem> LARGE_ITEM_RENDERERS = new ArrayList<>();

	private TESItemRendererManager() {
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		LARGE_ITEM_RENDERERS.clear();
		try {
			for (Item item : TESItems.CONTENT) {
				MinecraftForgeClient.registerItemRenderer(item, null);
				TESRenderLargeItem largeItemRenderer = TESRenderLargeItem.getRendererIfLarge(item);
				boolean isLarge = largeItemRenderer != null;
				if (item instanceof TESItemCrossbow) {
					MinecraftForgeClient.registerItemRenderer(item, new TESRenderCrossbow());
				} else if (item instanceof TESItemBow) {
					MinecraftForgeClient.registerItemRenderer(item, new TESRenderBow(largeItemRenderer));
				} else if (item instanceof TESItemSword && ((TESItemSword) item).isGlowing()) {
					MinecraftForgeClient.registerItemRenderer(item, new TESRenderBlade(largeItemRenderer));
				} else if (isLarge) {
					MinecraftForgeClient.registerItemRenderer(item, largeItemRenderer);
				}
				if (largeItemRenderer != null) {
					LARGE_ITEM_RENDERERS.add(largeItemRenderer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TESBlocks.commandTable), new TESRenderInvTableCommand());
		MinecraftForgeClient.registerItemRenderer(TESItems.pipe, new TESRenderBlownItem());
		MinecraftForgeClient.registerItemRenderer(TESItems.commandHorn, new TESRenderBlownItem());
		MinecraftForgeClient.registerItemRenderer(TESItems.conquestHorn, new TESRenderBlownItem());
		MinecraftForgeClient.registerItemRenderer(TESItems.banner, new TESRenderBannerItem());
		MinecraftForgeClient.registerItemRenderer(TESItems.skullStaff, new TESRenderSkullStaff());
		for (Item item : (Iterable<Item>) Item.itemRegistry) {
			if (item instanceof TESItemAnimalJar) {
				MinecraftForgeClient.registerItemRenderer(item, new TESRenderAnimalJar());
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void preTextureStitch(TextureStitchEvent.Pre event) {
		TextureMap map = event.map;
		if (map.getTextureType() == 1) {
			for (TESRenderLargeItem largeRenderer : LARGE_ITEM_RENDERERS) {
				largeRenderer.registerIcons(map);
			}
		}
	}
}