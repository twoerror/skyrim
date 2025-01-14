package tes.common.util;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.client.TESTextures;
import tes.common.TESDimension;
import tes.common.TESLore;
import tes.common.database.*;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionRelations;
import tes.common.faction.TESMapRegion;
import tes.common.item.other.TESItemBanner;
import tes.common.quest.TESMiniQuestFactory;
import tes.common.world.biome.TESBiome;
import tes.common.world.feature.TESTreeType;
import tes.common.world.genlayer.TESGenLayerWorld;
import tes.common.world.map.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("all")
public class TESAPI {
	private TESAPI() {
	}

	/**
	 * @param enumName - name of the new achievement category in enum.
	 * @param biome    - biome name turns into the category name.
	 * @apiNote Creates new achievement category.
	 */
	public static TESAchievement.Category addAchievementCategory(String enumName, TESBiome biome) {
		Class<? extends TESBiome>[] classArr = new Class[]{TESBiome.class};
		Object[] args = {biome};
		return EnumHelper.addEnum(TESAchievement.Category.class, enumName, classArr, args);
	}

	/**
	 * @param enumName - name of the new achievement category in enum.
	 * @param faction  - faction name turns into the category name.
	 * @apiNote Creates new achievement category.
	 */
	public static TESAchievement.Category addAchievementCategory(String enumName, TESFaction faction) {
		Class<TESFaction>[] classArr = new Class[]{TESFaction.class};
		Object[] args = {faction};
		return EnumHelper.addEnum(TESAchievement.Category.class, enumName, classArr, args);
	}

	/**
	 * @param enumName - name of the new cape in enum.
	 * @param faction  - cape belongs to this faction.
	 * @apiNote Creates new faction cape.
	 */
	public static TESCapes addAlignmentCape(String enumName, TESFaction faction) {
		Class<TESFaction>[] classArr = new Class[]{TESFaction.class};
		Object[] args = {faction};
		return EnumHelper.addEnum(TESCapes.class, enumName, classArr, args);
	}

	/**
	 * @param enumName - name of the new shield in enum.
	 * @param faction  - shield belongs to this faction.
	 * @apiNote Creates new faction shield.
	 */
	public static TESShields addAlignmentShield(String enumName, TESFaction faction) {
		Class<TESFaction>[] classArr = new Class[]{TESFaction.class};
		Object[] args = {faction};
		return EnumHelper.addEnum(TESShields.class, enumName, classArr, args);
	}

	/**
	 * @param enumName   - name of the new banner in enum.
	 * @param id         - should be unique, so use ids from 700.
	 * @param bannerName - texture name of the banner.
	 * @param faction    - banner's faction. Affects on private, conquest, icon
	 *                   color.
	 * @apiNote Creates new faction banner.
	 */
	public static TESItemBanner.BannerType addBanner(String enumName, int id, String bannerName, TESFaction faction) {
		Class<?>[] classArr = {Integer.TYPE, String.class, TESFaction.class};
		Object[] args = {id, bannerName, faction};
		TESItemBanner.BannerType banner = EnumHelper.addEnum(TESItemBanner.BannerType.class, enumName, classArr, args);
		TESItemBanner.BannerType.BANNER_FOR_ID.put(banner.getBannerID(), banner);
		TESItemBanner.BannerType.BANNER_TYPES.add(banner);
		return banner;
	}

	/**
	 * @param enumName - name of the new cape in enum.
	 * @param hidden   - will be displayed in the GUI or not.
	 * @param players  - UUIDs of the owners.
	 * @apiNote Creates new exclusive cape.
	 */
	public static TESCapes addCape(String enumName, boolean hidden, List<String> players) {
		return addCape(enumName, TESCapes.CapeType.EXCLUSIVE, hidden, players);
	}

	/**
	 * @param enumName - name of the new cape in enum.
	 * @param type     - exclusive/achievement/faction.
	 * @param hidden   - will be displayed in the GUI or not.
	 * @param players  - UUIDs of the owners.
	 * @apiNote Creates new exclusive cape.
	 */
	public static TESCapes addCape(String enumName, TESCapes.CapeType type, boolean hidden, List<String> players) {
		Class<?>[] classArr = {TESCapes.CapeType.class, Boolean.TYPE, ArrayList.class};
		Object[] args = {type, hidden, players};
		return EnumHelper.addEnum(TESCapes.class, enumName, classArr, args);
	}

	/**
	 * @param enumName   - name of the new dimension region in enum.
	 * @param regionName - name of the region, should be translated.
	 * @apiNote Creates new dimension region.
	 */
	public static TESDimension.DimensionRegion addDimensionRegion(String enumName, String regionName) {
		Class<String>[] classArr = new Class[]{String.class};
		Object[] args = {regionName};
		return EnumHelper.addEnum(TESDimension.DimensionRegion.class, enumName, classArr, args);
	}

	/**
	 * @param enumName - name of the new faction in enum.
	 * @param color    - 0xHHEEXX. For the bar, banners, eggs, etc.
	 * @param region   - dimension region.
	 * @param mapInfo  - square of the map displayed on the faction page.
	 * @apiNote Creates new faction
	 */
	public static TESFaction addFaction(String enumName, int color, TESDimension.DimensionRegion region, TESMapRegion mapInfo) {
		Class<?>[] classArr = {Integer.TYPE, TESDimension.class, TESDimension.DimensionRegion.class, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, TESMapRegion.class};
		Object[] args = {color, TESDimension.GAME_OF_THRONES, region, true, true, Integer.MIN_VALUE, mapInfo};
		return EnumHelper.addEnum(TESFaction.class, enumName, classArr, args);
	}

	/**
	 * @param enumName  - name of the new faction in enum.
	 * @param color     - 0xHHEEXX. For the bar, banners, eggs, etc.
	 * @param dim       - dimension.
	 * @param region    - dimension region.
	 * @param player    - playability (reputation gaining, ...).
	 * @param registry  - allows entity registry (attack settings).
	 * @param alignment - fixed alignment.
	 * @param mapInfo   - square of the map displayed on the faction page.
	 * @apiNote Creates new faction with lots of technical settings.
	 * @deprecated Complex way, only for advanced developers.
	 */
	@Deprecated
	public static TESFaction addFaction(String enumName, int color, TESDimension dim, TESDimension.DimensionRegion region, boolean player, boolean registry, int alignment, TESMapRegion mapInfo) {
		Class<?>[] classArr = {Integer.TYPE, TESDimension.class, TESDimension.DimensionRegion.class, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, TESMapRegion.class};
		Object[] args = {color, dim, region, player, registry, alignment, mapInfo};
		return EnumHelper.addEnum(TESFaction.class, enumName, classArr, args);
	}

	/**
	 * @param enumName - name of the new invasion in enum.
	 * @param faction  - invasion belongs to this faction.
	 * @apiNote Creates new faction invasion.
	 */
	public static TESInvasions addInvasion(String enumName, TESFaction faction) {
		return addInvasion(enumName, faction, null);
	}

	/**
	 * @param enumName   - name of the new invasion in enum.
	 * @param faction    - invasion belongs to this faction.
	 * @param subfaction - invasion's name in lang file.
	 * @apiNote Creates new secondary faction invasion
	 */
	public static TESInvasions addInvasion(String enumName, TESFaction faction, String subfaction) {
		Class<?>[] classArr = {TESFaction.class, String.class};
		Object[] args = {faction, subfaction};
		return EnumHelper.addEnum(TESInvasions.class, enumName, classArr, args);
	}

	/**
	 * @param enumName - name of the new lore category in enum.
	 * @param name     - codename that is to be written in books.
	 * @apiNote Creates new lore category with its books.
	 */
	public static TESLore.LoreCategory addLoreCategory(String enumName, String name) {
		Class<String>[] classArr = new Class[]{String.class};
		Object[] args = {name};
		return EnumHelper.addEnum(TESLore.LoreCategory.class, enumName, classArr, args);
	}

	/**
	 * @param category - lore category.
	 * @param lore     - lore.
	 * @apiNote Adds lore to existing lore category.
	 */
	public static void addLoreToLoreCategory(TESLore.LoreCategory category, TESLore lore) {
		category.getLoreList().add(lore);
	}

	/**
	 * @param enumName   - name of the new map label in enum.
	 * @param biomeLabel - name of this biome will be used in label.
	 * @param x          - coord of the pixel on map.png.
	 * @param y          - coord of the pixel on map.png.
	 * @param scale      - font size.
	 * @param angle      - rotation angle (hour hand).
	 * @param zoomMin    - label will be seen after this scale.
	 * @param zoomMax    - label will be seen before this scale.
	 * @apiNote Creates new map label on the gui screen.
	 */
	public static TESMapLabels addMapLabel(String enumName, TESBiome biomeLabel, int x, int y, float scale, int angle, float zoomMin, float zoomMax) {
		return addMapLabel(enumName, (Object) biomeLabel, x, y, scale, angle, zoomMin, zoomMax);
	}

	private static TESMapLabels addMapLabel(String enumName, Object label, int x, int y, float scale, int angle, float zoomMin, float zoomMax) {
		Class<?>[] classArr = {Object.class, Integer.TYPE, Integer.TYPE, Float.TYPE, Integer.TYPE, Float.TYPE, Float.TYPE};
		Object[] args = {label, x, y, scale, angle, zoomMin, zoomMax};
		return EnumHelper.addEnum(TESMapLabels.class, enumName, classArr, args);
	}

	/**
	 * @param enumName    - name of the new map label in enum.
	 * @param stringLabel - name of the label for translation.
	 * @param x           - coord of the pixel on map.png.
	 * @param y           - coord of the pixel on map.png.
	 * @param scale       - font size.
	 * @param angle       - angle of the label on the map. 0 is horisontal.
	 * @param zoomMin     - label will be seen after this scale.
	 * @param zoomMax     - label will be seen before this scale.
	 * @apiNote Creates new map label on the gui screen.
	 */
	public static TESMapLabels addMapLabel(String enumName, String stringLabel, int x, int y, float scale, int angle, float zoomMin, float zoomMax) {
		return addMapLabel(enumName, (Object) stringLabel, x, y, scale, angle, zoomMin, zoomMax);
	}

	/**
	 * @param enumName - name of the new miniquest category in enum.
	 * @param name     - name of the subfolder.
	 * @apiNote Creates new miniquest category.
	 */
	public static TESMiniQuestFactory addMiniQuestFactory(String enumName, String name) {
		Class<String>[] classArr = new Class[]{String.class};
		Object[] args = {name};
		return EnumHelper.addEnum(TESMiniQuestFactory.class, enumName, classArr, args);
	}

	/**
	 * @param name - name of the new mountain in enum.
	 * @param x    - coord of the pixel on map.png.
	 * @param z    - coord of the pixel on map.png.
	 * @param h    - height.
	 * @param r    - radius.
	 * @apiNote Creates new mountain.
	 */
	public static TESMountains addMountain(String name, double x, double z, float h, int r) {
		return addMountain(name, x, z, h, r, 0);
	}

	/**
	 * @param name - name of the new mountain in enum.
	 * @param x    - coord of the pixel on map.png.
	 * @param z    - coord of the pixel on map.png.
	 * @param h    - height.
	 * @param r    - radius.
	 * @param lava - lava crater radius. Usual mountain = 0.
	 * @apiNote Creates new mountain.
	 */
	public static TESMountains addMountain(String name, double x, double z, float h, int r, int lava) {
		Class<?>[] classArr = {Double.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Integer.TYPE};
		Object[] args = {x, z, h, r, lava};
		return EnumHelper.addEnum(TESMountains.class, name, classArr, args);
	}

	/**
	 * @param enumName - name of the new shield in enum.
	 * @param hidden   - will be displayed in the GUI or not.
	 * @param players  - UUIDs of the owners.
	 * @apiNote Creates new exclusive shield.
	 */
	public static TESShields addShield(String enumName, boolean hidden, List<String> players) {
		return addShield(enumName, TESShields.ShieldType.EXCLUSIVE, hidden, players);
	}

	/**
	 * @param enumName - name of the new shield in enum.
	 * @param type     - exclusive/achievement/faction.
	 * @param hidden   - will be displayed in the GUI or not.
	 * @param players  - UUIDs of the owners.
	 * @apiNote Creates new exclusive shield.
	 */
	public static TESShields addShield(String enumName, TESShields.ShieldType type, boolean hidden, List<String> players) {
		Class<?>[] classArr = {TESShields.ShieldType.class, Boolean.TYPE, ArrayList.class};
		Object[] args = {type, hidden, players};
		return EnumHelper.addEnum(TESShields.class, enumName, classArr, args);
	}

	/**
	 * @param enumName    - name of the new tree type in enum.
	 * @param treeFactory - lambda constructions, see in TESTreeType.java.
	 * @apiNote Creates new tree, that can be added in the biome.
	 */
	public static TESTreeType addTreeType(String enumName, Object treeFactory) {
		Class<TESTreeType.ITreeFactory>[] classArr = new Class[]{TESTreeType.ITreeFactory.class};
		Object[] args = {treeFactory};
		return EnumHelper.addEnum(TESTreeType.class, enumName, classArr, args);
	}

	/**
	 * @param name    - name of the new waypoint in enum.
	 * @param region  - region of unlocking.
	 * @param faction - you should have 0+ rep with this faction to travel here.
	 * @param x       - coord of the pixel on map.png.
	 * @param z       - coord of the pixel on map.png.
	 * @param hidden  - hidden point is invisible before unlocking.
	 * @apiNote Creates new waypoint.
	 */
	public static TESWaypoint addWaypoint(String name, TESWaypoint.Region region, TESFaction faction, double x, double z, boolean hidden) {
		Class<?>[] classArr = {TESWaypoint.Region.class, TESFaction.class, Double.TYPE, Double.TYPE, Boolean.TYPE};
		Object[] args = {region, faction, x, z, hidden};
		return EnumHelper.addEnum(TESWaypoint.class, name, classArr, args);
	}

	/**
	 * @param name    - name of the new waypoint in enum.
	 * @param region  - region of unlocking.
	 * @param faction - you should have 0+ rep with this faction to travel here.
	 * @param x       - coord of the pixel on map.png.
	 * @param z       - coord of the pixel on map.png.
	 * @apiNote Creates new waypoint.
	 */
	public static TESWaypoint addWaypoint(String name, TESWaypoint.Region region, TESFaction faction, int x, int z) {
		return addWaypoint(name, region, faction, x, z, false);
	}

	/**
	 * @param name - name of the new waypoint region in enum.
	 * @apiNote Creates new waypoint region for unlocking.
	 * @apiNote Don't forget to point out this region in needed biome.
	 * @apiNote Init regions BEGORE adding waypoints.
	 */
	public static TESWaypoint.Region addWaypointRegion(String name) {
		Class<?>[] classArr = {};
		Object[] args = {};
		return EnumHelper.addEnum(TESWaypoint.Region.class, name, classArr, args);
	}

	/**
	 * @param faction   - faction that will be moved.
	 * @param newRegion - new dimension region.
	 * @apiNote Moves faction from one region category to the another.
	 */
	public static void changeDimensionRegion(TESFaction faction, TESDimension.DimensionRegion newRegion) {
		faction.getFactionRegion().getFactionList().remove(faction);
		newRegion.getFactionList().add(faction);
		faction.setFactionRegion(newRegion);
	}

	/**
	 * @apiNote Changes all the faction relations between factions to neutral.
	 */
	public static void clearAllRelations() {
		TESFactionRelations.DEFAULT_MAP.clear();
	}

	/**
	 * @apiNote Clears all the roads and walls in the world.
	 * @apiNote Should be used at the FMLInitializationEvent or later.
	 */
	public static void clearBezierDataBase() {
		TESBeziers.CONTENT.clear();
		TESBeziers.setRoadPointDatabase(new TESBeziers.BezierPointDatabase());
		TESBeziers.setWallPointDatabase(new TESBeziers.BezierPointDatabase());
		TESBeziers.setLinkerPointDatabase(new TESBeziers.BezierPointDatabase());
	}

	/**
	 * @apiNote Creates waypoint at needed X, Z (pixel coords on map.png)
	 */
	public static TESAbstractWaypoint createAnonymousWaypoint(double targetX, double targetY) {
		double minDistance = Double.POSITIVE_INFINITY;
		TESWaypoint closestWaypoint = null;

		for (TESWaypoint waypoint : TESWaypoint.values()) {
			double distance = Math.sqrt(Math.pow(waypoint.getImgX() - targetX, 2) + Math.pow(waypoint.getImgY() - targetY, 2));
			if (distance < minDistance) {
				minDistance = distance;
				closestWaypoint = waypoint;
			}
		}
		return new TESWaypointInfo(closestWaypoint, targetX - closestWaypoint.getImgX(), targetY - closestWaypoint.getImgY(), 0);
	}

	/**
	 * @param factory - miniquest factory that will be cleared.
	 * @apiNote Clears miniquest factory.
	 */
	public static void clearMiniQuestFactory(TESMiniQuestFactory factory) {
		factory.setBaseSpeechGroup(null);
		factory.setQuestAchievement(null);
		factory.getLoreCategories().clear();
		factory.setAlignmentRewardOverride(null);
		factory.setNoAlignRewardForEnemy(false);
		factory.getQuestFactories().clear();
	}

	/**
	 * @apiNote TES won't spam about the new update in the chat.
	 * @apiNote Should be used at the FMLPostInitializationEvent or later.
	 */
	public static void disableTESUpdateChecker() {
		TESVersionChecker.setCheckedUpdate(true);
	}

	private static <T, E> void findAndInvokeMethod(Object[] args, Class<? super E> clazz, E instance, String[] methodNames, Class<?>... methodTypes) {
		Method method = ReflectionHelper.findMethod(clazz, instance, methodNames, methodTypes);
		try {
			method.invoke(instance, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			TESLog.getLogger().error("Error when getting method {} from class {}", methodNames[0], clazz.getSimpleName());
			e.printStackTrace();
		}
	}

	private static ModContainer getContainer(ResourceLocation res) {
		ModContainer modContainer = Loader.instance().getIndexedModList().get(res.getResourceDomain());
		if (modContainer == null) {
			throw new IllegalArgumentException("Can't find the mod container for the domain " + res.getResourceDomain());
		}
		return modContainer;
	}

	private static BufferedImage getImage(InputStream in) {
		try {
			return ImageIO.read(in);
		} catch (IOException e) {
			TESLog.getLogger().error("Failed to convert a input stream into a buffered image.");
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				TESLog.getLogger().error("Failed to convert a input stream into a buffered image.");
			}
		}
		return null;
	}

	private static InputStream getInputStream(ModContainer container, String path) {
		return container.getClass().getResourceAsStream(path);
	}

	private static InputStream getInputStream(ResourceLocation res) {
		return getInputStream(getContainer(res), getPath(res));
	}

	/**
	 * @param clazz - class file with fields.
	 * @param type  - needed type.
	 * @apiNote Returns the list of fields of needed type from class file.
	 */
	public static <E, T> Set<T> getObjectFieldsOfType(Class<? extends E> clazz, Class<? extends T> type) {
		return getObjectFieldsOfType(clazz, null, type);
	}

	private static <E, T> Set<T> getObjectFieldsOfType(Class<? extends E> clazz, E instance, Class<? extends T> type) {
		Collection<Object> list = new HashSet<>();
		try {
			for (Field field : clazz.getDeclaredFields()) {
				if (field == null) {
					continue;
				}
				Object fieldObj = null;
				if (Modifier.isStatic(field.getModifiers())) {
					fieldObj = field.get(null);
				} else if (instance != null) {
					fieldObj = field.get(instance);
				}
				if (fieldObj == null || !type.isAssignableFrom(fieldObj.getClass())) {
					continue;
				}
				list.add(fieldObj);
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			TESLog.getLogger().error("Errored when getting all field from: {} of type: {}", clazz.getName(), type.getName());
		}
		return (Set<T>) list;
	}

	private static String getPath(ResourceLocation res) {
		return "/assets/" + res.getResourceDomain() + '/' + res.getResourcePath();
	}

	@SideOnly(Side.CLIENT)
	private static ResourceLocation getTextureResourceLocation(InputStream in, String textureName) {
		BufferedImage img = getImage(in);
		if (img != null) {
			return Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(textureName, new DynamicTexture(img));
		}
		return null;
	}

	/**
	 * @param content - multiple blocks or items.
	 * @apiNote Removes blocks or items from the inventory.
	 */
	public static void hideFromInventory(Object... content) {
		for (Object obj : content) {
			if (obj instanceof Block) {
				((Block) obj).setCreativeTab(null);
			} else if (obj instanceof Item) {
				((Item) obj).setCreativeTab(null);
			}
		}
	}

	/**
	 * @param content - multiple capes.
	 * @apiNote Removes selected capes from the GUI.
	 */
	public static void removeCapes(TESCapes... content) {
		for (TESCapes removal : content) {
			removal.setHidden(true);
		}
	}

	/**
	 * @param content - multiple capes.
	 * @apiNote Removes capes selected selected from the GUI.
	 */
	public static void removeCapesExcept(TESCapes... content) {
		for (TESCapes removal : TESCapes.values()) {
			for (TESCapes excluded : content) {
				if (excluded != removal) {
					removal.setHidden(true);
					break;
				}
			}
		}
	}

	private static void removeFaction(TESFaction faction) {
		faction.setAllowPlayer(false);
		faction.setFixedAlignment(0);
		if (faction.getFactionDimension() != null) {
			faction.getFactionDimension().getFactionList().remove(faction);
		}
		if (faction.getFactionRegion() != null) {
			faction.getFactionRegion().getFactionList().remove(faction);
		}
		faction.setFactionDimension(null);
		faction.setFactionRegion(null);
		faction.getControlZones().clear();
	}

	/**
	 * @param content - multiple factions.
	 * @apiNote Removes selected factions from the GUI.
	 */
	public static void removeFactions(TESFaction... content) {
		for (TESFaction removal : content) {
			removeFaction(removal);
		}
	}

	/**
	 * @param content - multiple factions.
	 * @apiNote Removes factions except selected from the GUI.
	 */
	public static void removeFactionsExcept(TESFaction... content) {
		for (TESFaction removal : TESFaction.values()) {
			for (TESFaction excluded : content) {
				if (excluded != removal) {
					removeFaction(removal);
				}
			}
		}
	}

	private static void removeMapLabel(TESMapLabels label) {
		label.setMaxZoom(1.0E-4f);
		label.setMinZoom(0);
	}

	/**
	 * @param content - multiple map labels.
	 * @apiNote Removes selected map labels from the GUI.
	 */
	public static void removeMapLabels(TESMapLabels... content) {
		for (TESMapLabels removal : content) {
			removeMapLabel(removal);
		}
	}

	/**
	 * @param content - multiple map labels.
	 * @apiNote Removes map labels except selected from the GUI.
	 */
	public static void removeMapLabelsExcept(TESMapLabels... content) {
		for (TESMapLabels removal : TESMapLabels.values()) {
			for (TESMapLabels excluded : content) {
				if (excluded != removal) {
					removeMapLabel(removal);
				}
			}
		}
	}

	/**
	 * @param content - multiple shields.
	 * @apiNote Removes selected shields from the GUI.
	 */
	public static void removeShields(TESShields... content) {
		for (TESShields removal : content) {
			removal.setHidden(true);
		}
	}

	/**
	 * @param content - multiple shields.
	 * @apiNote Removes shields except selected from the GUI.
	 */
	public static void removeShieldsExcept(TESShields... content) {
		for (TESShields removal : TESShields.values()) {
			for (TESShields excluded : content) {
				if (excluded != removal) {
					removal.setHidden(true);
					break;
				}
			}
		}
	}

	private static void removeTitle(TESTitle title) {
		if (title.getTitleType() == TESTitle.TitleType.ACHIEVEMENT) {
			title.getTitleAchievement().setAchievementTitle(null);
			title.setTitleAchievement(null);
		}
		title.setHidden(true);
		TESTitle.CONTENT.remove(title);
	}

	/**
	 * @param content - multiple titles.
	 * @apiNote Removes selected titles from the GUI.
	 */
	public static void removeTitles(TESTitle... content) {
		for (TESTitle removal : content) {
			removeTitle(removal);
		}
	}

	/**
	 * @param content - multiple titles.
	 * @apiNote Removes titles except selected from the GUI.
	 */
	public static void removeTitlesExcept(TESTitle... content) {
		Set<TESTitle> set = getObjectFieldsOfType(TESTitle.class, TESTitle.class);
		for (TESTitle removal : set) {
			for (TESTitle excluded : content) {
				if (excluded != removal) {
					removeTitle(removal);
				}
			}
		}
	}

	private static void removeWaypoint(TESWaypoint wp) {
		wp.setHidden(true);
		wp.setFaction(TESFaction.HOSTILE);
	}

	/**
	 * @param content - multiple waypoints.
	 * @apiNote Removes selected waypoints from the GUI.
	 */
	public static void removeWaypoints(TESWaypoint... content) {
		for (TESWaypoint removal : content) {
			removeWaypoint(removal);
		}
	}

	/**
	 * @param content - multiple waypoints.
	 * @apiNote Removes waypoints except selected from the GUI.
	 */
	public static void removeWaypointsExcept(TESWaypoint... content) {
		for (TESWaypoint removal : TESWaypoint.values()) {
			for (TESWaypoint excluded : content) {
				if (excluded != removal) {
					removeWaypoint(removal);
				}
			}
		}
	}

	/**
	 * @param mapTexture - path to map file.
	 * @apiNote Changes map and sepia map in the GUI.
	 * @apiNote YourClientProxy should implement IResourceManagerReloadListener.
	 * @apiNote Add onInit() void to the YourClientProxy file, if doesn't exist.
	 * @apiNote Add next line into the onInit():
	 * @apiNote ((
	 *IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new
	 * YourClientProxy());
	 * @apiNote Add unimplemented onResourceManagerReload to the
	 * YourClientProxy.
	 * @apiNote Use setClientMapImage void in the onResourceManagerReload.
	 */
	@SideOnly(Side.CLIENT)
	public static void setClientMapImage(ResourceLocation mapTexture) {
		ResourceLocation sepiaMapTexture;
		TESTextures.setMapTexture(mapTexture);
		try {
			BufferedImage mapImage = getImage(Minecraft.getMinecraft().getResourceManager().getResource(mapTexture).getInputStream());
			sepiaMapTexture = TESTextures.convertToSepia(mapImage, new ResourceLocation("tes:map_sepia"));
		} catch (IOException e) {
			FMLLog.severe("Failed to generate TES sepia map");
			e.printStackTrace();
			sepiaMapTexture = mapTexture;
		}
		TESTextures.setSepiaMapTexture(sepiaMapTexture);
	}

	/**
	 * @param region    - dimension region.
	 * @param dimension - new region's dimension.
	 * @apiNote Changes region's dimension.
	 * @deprecated No sense: only GAME_OF_THRONES dimension is available.
	 */
	@Deprecated
	public static void setDimensionForRegion(TESDimension.DimensionRegion region, TESDimension dimension) {
		region.setDimension(dimension);
		dimension.getDimensionRegions().add(region);
	}

	/**
	 * @param entity - mob, that will be rescaled.
	 * @param width  - new width of the mob.
	 * @param height - new height of the mob.
	 * @apiNote Changes entity scale.
	 */
	public static void setEntitySize(Entity entity, float width, float height) {
		findAndInvokeMethod(new Object[]{width, height}, Entity.class, entity, new String[]{"setSize", "func_70105_a", "a"}, Float.TYPE, Float.TYPE);
	}

	/**
	 * @param res - ResourceLocation with path to map file.
	 * @apiNote Changes map for the world generation.
	 * @apiNote Should be used at the FMLInitializationEvent or later.
	 */
	public static void setServerMapImage(ResourceLocation res) {
		BufferedImage img = getImage(getInputStream(res));
		if (img != null) {
			TESGenLayerWorld.setImageWidth(img.getWidth());
			TESGenLayerWorld.setImageHeight(img.getHeight());
			int[] colors = img.getRGB(0, 0, TESGenLayerWorld.getImageWidth(), TESGenLayerWorld.getImageHeight(), null, 0, TESGenLayerWorld.getImageWidth());
			TESGenLayerWorld.setBiomeImageData(new byte[TESGenLayerWorld.getImageWidth() * TESGenLayerWorld.getImageHeight()]);
			for (int i = 0; i < colors.length; ++i) {
				int color = colors[i];
				Integer biomeID = TESDimension.GAME_OF_THRONES.getColorsToBiomeIDs().get(color);
				if (biomeID != null) {
					TESGenLayerWorld.getBiomeImageData()[i] = (byte) biomeID.intValue();
					continue;
				}
				TESLog.getLogger().error("Found unknown biome on map: {} at location: {}, {}", Integer.toHexString(color), i % TESGenLayerWorld.getImageWidth(), i / TESGenLayerWorld.getImageWidth());
				TESGenLayerWorld.getBiomeImageData()[i] = (byte) TESBiome.ocean.biomeID;
			}
		}
	}
}