package tes.common.world.genlayer;

import com.google.common.math.IntMath;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import tes.TES;
import tes.common.TESDimension;
import tes.common.world.biome.TESBiome;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TESGenLayerWorld extends TESGenLayer {
	public static final int ORIGIN_X = 653;
	public static final int ORIGIN_Z = 870;
	public static final int SCALE = IntMath.pow(2, 7);

	private static byte[] biomeImageData;
	private static int imageWidth;
	private static int imageHeight;

	private TESGenLayerWorld() {
		super(0L);
	}

	public static TESGenLayer[] createWorld(TESDimension dim, WorldType worldType) {
		int i;
		
		TESGenLayer rivers = new TESGenLayerRiverInit(100L);
		rivers = TESGenLayerZoom.magnify(1000L, rivers, 10);
		rivers = new TESGenLayerRiver(1L, rivers);
		rivers = new TESGenLayerSmooth(1000L, rivers);
		rivers = TESGenLayerZoom.magnify(1000L, rivers, 1);
		TESGenLayer biomeSubtypes = new TESGenLayerBiomeSubtypesInit(3000L);
		biomeSubtypes = TESGenLayerZoom.magnify(3000L, biomeSubtypes, 2);
		TESGenLayer biomes = new TESGenLayerWorld();
		tryLoadBiomeImage();
		if (worldType == TES.worldTypeTESClassic) {
			TESGenLayer oceans = new TESGenLayerClassicOcean(2012L);
			oceans = TESGenLayerZoom.magnify(200L, oceans, 3);
			oceans = new TESGenLayerClassicRemoveOcean(400L, oceans);
			biomes = new TESGenLayerClassicBiomes(2013L, oceans, dim);
			biomes = TESGenLayerZoom.magnify(300L, biomes, 2);
		}
		TESGenLayer mapRivers = new TESGenLayerExtractMapRivers(5000L, biomes);
		biomes = new TESGenLayerRemoveMapRivers(1000L, biomes, dim);
		biomes = new TESGenLayerBiomeSubtypes(1000L, biomes, biomeSubtypes);
		biomes = TESGenLayerZoom.magnify(1000L, biomes, 1);
		biomes = new TESGenLayerBeach(1000L, biomes, dim, TESBiome.ocean);
		biomes = TESGenLayerZoom.magnify(1000L, biomes, 2);
		biomes = TESGenLayerZoom.magnify(1000L, biomes, 2);
		biomes = new TESGenLayerSmooth(1000L, biomes);
		TESGenLayer variants = new TESGenLayerBiomeVariants(200L);
		variants = TESGenLayerZoom.magnify(200L, variants, 8);
		TESGenLayer variantsSmall = new TESGenLayerBiomeVariants(300L);
		variantsSmall = TESGenLayerZoom.magnify(300L, variantsSmall, 6);
		TESGenLayer lakes = new TESGenLayerBiomeVariantsLake(100L, null, 0).setLakeFlags(1);
		for (i = 1; i <= 5; ++i) {
			lakes = new TESGenLayerZoom(200L + i, lakes);
			if (i <= 2) {
				lakes = new TESGenLayerBiomeVariantsLake(300L + i, lakes, i).setLakeFlags(1);
			}
			if (i != 3) {
				continue;
			}
			lakes = new TESGenLayerBiomeVariantsLake(500L, lakes, i).setLakeFlags(2, 4);
		}
		for (i = 0; i < 4; ++i) {
			mapRivers = new TESGenLayerMapRiverZoom(4000L + i, mapRivers);
		}
		mapRivers = new TESGenLayerNarrowRivers(3000L, mapRivers, 6);
		mapRivers = TESGenLayerZoom.magnify(4000L, mapRivers, 1);
		rivers = new TESGenLayerIncludeMapRivers(5000L, rivers, mapRivers);
		return new TESGenLayer[]{biomes, variants, variantsSmall, lakes, rivers};
	}

	private static int getBiomeImageID(int x, int z) {
		int index = z * imageWidth + x;
		return biomeImageData[index] & 0xFF;
	}

	public static TESBiome getBiomeOrOcean(int mapX, int mapZ) {
		int biomeID = mapX >= 0 && mapX < imageWidth && mapZ >= 0 && mapZ < imageHeight ? getBiomeImageID(mapX, mapZ) : TESBiome.ocean.biomeID;
		return TESDimension.GAME_OF_THRONES.getBiomeList()[biomeID];
	}

	public static boolean loadedBiomeImage() {
		return biomeImageData != null;
	}

	public static int getImageHeight() {
		return imageHeight;
	}

	public static void setImageHeight(int imageHeight) {
		TESGenLayerWorld.imageHeight = imageHeight;
	}

	public static int getImageWidth() {
		return imageWidth;
	}

	public static void setImageWidth(int imageWidth) {
		TESGenLayerWorld.imageWidth = imageWidth;
	}

	public static byte[] getBiomeImageData() {
		return biomeImageData;
	}

	public static void setBiomeImageData(byte[] biomeImageData) {
		TESGenLayerWorld.biomeImageData = biomeImageData;
	}

	public static void tryLoadBiomeImage() {
		if (!loadedBiomeImage()) {
			try {
				BufferedImage biomeImage = null;
				String imageName = "assets/tes/textures/map/map.png";
				ModContainer mc = TES.getModContainer();
				if (mc.getSource().isFile()) {
					ZipFile zip = new ZipFile(mc.getSource());
					Enumeration<? extends ZipEntry> entries = zip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						if (!entry.getName().equals(imageName)) {
							continue;
						}
						biomeImage = ImageIO.read(zip.getInputStream(entry));
					}
					zip.close();
				} else {
					File file = new File(TES.class.getResource('/' + imageName).toURI());
					biomeImage = ImageIO.read(Files.newInputStream(file.toPath()));
				}
				if (biomeImage == null) {
					throw new RuntimeException("Could not init TES biome map image");
				}
				imageWidth = biomeImage.getWidth();
				imageHeight = biomeImage.getHeight();
				int[] colors = biomeImage.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);
				biomeImageData = new byte[imageWidth * imageHeight];
				for (int i = 0; i < colors.length; ++i) {
					int color = colors[i];
					Integer biomeID = TESDimension.GAME_OF_THRONES.getColorsToBiomeIDs().get(color);
					if (biomeID != null) {
						biomeImageData[i] = biomeID.byteValue();
						continue;
					}
					FMLLog.log(Level.ERROR, "Found unknown biome on map " + Integer.toHexString(color));
					biomeImageData[i] = (byte) TESBiome.ocean.biomeID;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int[] intArray = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				int i2 = i + i1 + ORIGIN_X;
				int k2 = k + k1 + ORIGIN_Z;
				intArray[i1 + k1 * xSize] = i2 < 0 || i2 >= imageWidth || k2 < 0 || k2 >= imageHeight ? TESBiome.ocean.biomeID : getBiomeImageID(i2, k2);
			}
		}
		return intArray;
	}
}