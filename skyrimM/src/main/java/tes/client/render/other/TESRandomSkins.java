package tes.client.render.other;

import cpw.mods.fml.common.FMLLog;
import tes.client.TESTextures;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class TESRandomSkins implements IResourceManagerReloadListener {
	private static final Map<String, TESRandomSkins> ALL_RANDOM_SKINS = new HashMap<>();

	private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
	private static final Random RANDOM = new Random();

	private final String skinPath;

	private List<ResourceLocation> skins;

	private TESRandomSkins(String path, boolean register) {
		skinPath = path;
		if (register) {
			((IReloadableResourceManager) MINECRAFT.getResourceManager()).registerReloadListener(this);
		} else {
			loadAllRandomSkins();
		}
	}

	public static TESRandomSkins loadSkinsList(String path) {
		TESRandomSkins skins = ALL_RANDOM_SKINS.get(path);
		if (skins == null) {
			skins = new TESRandomSkins(path, true);
			ALL_RANDOM_SKINS.put(path, skins);
		}
		return skins;
	}

	public ResourceLocation getRandomSkin() {
		int i = RANDOM.nextInt(skins.size());
		return skins.get(i);
	}

	public ResourceLocation getRandomSkin(TESRandomSkinEntity rsEntity) {
		if (skins == null || skins.isEmpty()) {
			return TESTextures.MISSING_TEXTURE;
		}
		Entity entity = (Entity) rsEntity;
		long l = entity.getUniqueID().getLeastSignificantBits();
		long hash = skinPath.hashCode();
		l = l * 39603773L ^ l * 6583690632L ^ hash;
		l = l * hash * 2906920L + l * 65936063L;
		RANDOM.setSeed(l);
		int i = RANDOM.nextInt(skins.size());
		return skins.get(i);
	}

	private void loadAllRandomSkins() {
		skins = new ArrayList<>();
		int skinCount = 0;
		int skips = 0;
		int maxSkips = 10;
		boolean foundAfterSkip = false;
		do {
			ResourceLocation skin = new ResourceLocation(skinPath + '/' + skinCount + ".png");
			boolean noFile = false;
			try {
				if (MINECRAFT.getResourceManager().getResource(skin) == null) {
					noFile = true;
				}
			} catch (Exception e) {
				noFile = true;
			}
			if (noFile) {
				skips++;
				if (skips >= maxSkips) {
					break;
				}
				++skinCount;
			} else {
				skins.add(skin);
				++skinCount;
				if (skips > 0) {
					foundAfterSkip = true;
				}
			}
		} while (true);
		if (skins.isEmpty()) {
			FMLLog.warning("Hummel009: No random skins for %s", skinPath);
		}
		if (foundAfterSkip) {
			FMLLog.warning("Hummel009: Random skins %s skipped a number. This is not good - please number your skins from 0 and upwards, with no gaps!", skinPath);
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourcemanager) {
		loadAllRandomSkins();
	}
}