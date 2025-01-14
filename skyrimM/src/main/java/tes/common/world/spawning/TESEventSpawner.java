package tes.common.world.spawning;

import cpw.mods.fml.common.eventhandler.Event;
import tes.TES;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.database.TESInvasions;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityBanditBase;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.inanimate.TESEntityInvasionSpawner;
import tes.common.util.TESCrashHandler;
import tes.common.world.TESWorldProvider;
import tes.common.world.biome.TESBiome;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TESEventSpawner {
	private static final Set<ChunkCoordIntPair> ELIGIBLE_SPAWN_CHUNKS = new HashSet<>();

	private TESEventSpawner() {
	}

	public static void performSpawning(World world) {
		if (world.getTotalWorldTime() % 20L == 0L) {
			TESSpawnerNPCs.getSpawnableChunksWithPlayerInRange(world, ELIGIBLE_SPAWN_CHUNKS, 32);
			List<ChunkCoordIntPair> shuffled = TESSpawnerNPCs.shuffle(ELIGIBLE_SPAWN_CHUNKS);
			spawnBandits(world, shuffled);
			if (TESConfig.enableInvasions) {
				spawnInvasions(world, shuffled);
			}
		}
	}

	private static void spawnBandits(World world, Iterable<ChunkCoordIntPair> spawnChunks) {
		Random rand = world.rand;
		block0:
		for (ChunkCoordIntPair chunkCoords : spawnChunks) {
			int i;
			BiomeGenBase biome;
			int k;
			int range;
			ChunkPosition chunkposition = TESSpawnerNPCs.getRandomSpawningPointInChunk(world, chunkCoords);
			if (chunkposition == null || !((biome = TESCrashHandler.getBiomeGenForCoords(world, i = chunkposition.chunkPosX, k = chunkposition.chunkPosZ)) instanceof TESBiome)) {
				continue;
			}
			TESBiome tesbiome = (TESBiome) biome;
			Class<? extends TESEntityNPC> banditClass = tesbiome.getBanditEntityClass();
			double chance = tesbiome.getBanditChance().getChancesPerSecondPerChunk()[16];
			if (chance <= 0.0 || world.rand.nextDouble() >= chance || world.selectEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(i - (range = 48), 0.0, k - range, i + range, world.getHeight(), k + range), TES.selectNonCreativePlayers()).isEmpty()) {
				continue;
			}
			int banditsSpawned = 0;
			int maxBandits = MathHelper.getRandomIntegerInRange(world.rand, 1, 4);
			for (int attempts = 0; attempts < 32; ++attempts) {
				Block block;
				TESEntityBanditBase bandit;
				int k1;
				int i1 = i + MathHelper.getRandomIntegerInRange(rand, -32, 32);
				int j1 = world.getHeightValue(i1, k1 = k + MathHelper.getRandomIntegerInRange(rand, -32, 32));
				if (j1 <= 60 || (block = world.getBlock(i1, j1 - 1, k1)) != biome.topBlock && block != biome.fillerBlock || world.getBlock(i1, j1, k1).isNormalCube() || world.getBlock(i1, j1 + 1, k1).isNormalCube() || (bandit = (TESEntityBanditBase) EntityList.createEntityByName(TESEntityRegistry.getStringFromClass(banditClass), world)) == null) {
					continue;
				}
				bandit.setLocationAndAngles(i1 + 0.5, j1, k1 + 0.5, world.rand.nextFloat() * 360.0f, 0.0f);
				Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(bandit, world, (float) bandit.posX, (float) bandit.posY, (float) bandit.posZ);
				if (canSpawn != Event.Result.ALLOW && (canSpawn != Event.Result.DEFAULT || !bandit.getCanSpawnHere())) {
					continue;
				}
				bandit.onSpawnWithEgg(null);
				world.spawnEntityInWorld(bandit);
				bandit.setNPCPersistent(false);
				banditsSpawned++;
				if (banditsSpawned >= maxBandits) {
					continue block0;
				}
			}
		}
	}

	private static void spawnInvasions(World world, Iterable<ChunkCoordIntPair> spawnChunks) {
		Random rand = world.rand;
		block0:
		for (ChunkCoordIntPair chunkCoords : spawnChunks) {
			int i;
			BiomeGenBase biome;
			int k;
			ChunkPosition chunkposition = TESSpawnerNPCs.getRandomSpawningPointInChunk(world, chunkCoords);
			if (chunkposition == null || !((biome = TESCrashHandler.getBiomeGenForCoords(world, i = chunkposition.chunkPosX, k = chunkposition.chunkPosZ)) instanceof TESBiome)) {
				continue;
			}
			TESBiomeInvasionSpawns invasionSpawns = ((TESBiome) biome).getInvasionSpawns();
			for (EventChance invChance : EventChance.values()) {
				int range;
				List<TESInvasions> invList = invasionSpawns.getInvasionsForChance(invChance);
				if (invList.isEmpty()) {
					continue;
				}
				TESInvasions invasionType = invList.get(rand.nextInt(invList.size()));
				double chance = invChance.getChancesPerSecondPerChunk()[16];
				if (!world.isDaytime() && TESWorldProvider.isLunarEclipse()) {
					chance *= 5.0;
				}
				if (rand.nextDouble() >= chance || world.selectEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(i - (range = 48), 0.0, k - range, i + range, world.getHeight(), k + range), new EntitySelectorImpl(invasionType)).isEmpty()) {
					continue;
				}
				for (int attempts = 0; attempts < 16; ++attempts) {
					int k1;
					Block block;
					int i1 = i + MathHelper.getRandomIntegerInRange(rand, -32, 32);
					int j1 = world.getHeightValue(i1, k1 = k + MathHelper.getRandomIntegerInRange(rand, -32, 32));
					if (j1 <= 60 || (block = world.getBlock(i1, j1 - 1, k1)) != biome.topBlock && block != biome.fillerBlock || world.getBlock(i1, j1, k1).isNormalCube() || world.getBlock(i1, j1 + 1, k1).isNormalCube()) {
						continue;
					}
					TESEntityInvasionSpawner invasion = new TESEntityInvasionSpawner(world);
					invasion.setInvasionType(invasionType);
					invasion.setLocationAndAngles(i1 + 0.5, j1 + 3 + rand.nextInt(3), k1 + 0.5, 0.0f, 0.0f);
					if (!invasion.canInvasionSpawnHere()) {
						continue;
					}
					world.spawnEntityInWorld(invasion);
					invasion.selectAppropriateBonusFactions();
					invasion.startInvasion();
					continue block0;
				}
			}
		}
	}

	public enum EventChance {
		NEVER(0.0f, 0), RARE(0.1f, 3600), UNCOMMON(0.3f, 3600), COMMON(0.9f, 3600);

		private final double[] chancesPerSecondPerChunk;

		EventChance(float prob, int s) {
			double chancePerSecond = getChance(prob, s);
			chancesPerSecondPerChunk = new double[64];
			for (int i = 0; i < getChancesPerSecondPerChunk().length; ++i) {
				getChancesPerSecondPerChunk()[i] = getChance(chancePerSecond, i);
			}
		}

		private static double getChance(double prob, int trials) {
			if (prob == 0.0 || trials == 0) {
				return 0.0;
			}
			double d = prob;
			d = 1.0 - d;
			d = Math.pow(d, 1.0 / trials);
			return 1.0 - d;
		}

		private double[] getChancesPerSecondPerChunk() {
			return chancesPerSecondPerChunk;
		}
	}

	private static class EntitySelectorImpl implements IEntitySelector {
		private final TESInvasions invasionType;

		private EntitySelectorImpl(TESInvasions invasionType) {
			this.invasionType = invasionType;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			return entity instanceof EntityPlayer && entityplayer.isEntityAlive() && !entityplayer.capabilities.isCreativeMode && TESLevelData.getData(entityplayer).getAlignment(invasionType.getInvasionFaction()) < 0.0f;
		}
	}
}