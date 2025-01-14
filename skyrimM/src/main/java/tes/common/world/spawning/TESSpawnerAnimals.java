package tes.common.world.spawning;

import cpw.mods.fml.common.eventhandler.Event;
import tes.common.TESConfig;
import tes.common.TESSpawnDamping;
import tes.common.entity.animal.TESEntityBear;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.variant.TESBiomeVariant;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.*;

public class TESSpawnerAnimals {
	private static final Set<ChunkCoordIntPair> ELIGIBLE_SPAWN_CHUNKS = new HashSet<>();
	private static final Map<Integer, Integer> TICKS_SINCE_CYCLE = new HashMap<>();
	private static final Map<Integer, DimInfo> DIM_INFOS = new HashMap<>();

	private TESSpawnerAnimals() {
	}

	private static TypeInfo forDimAndType(World world, EnumCreatureType type) {
		return DIM_INFOS.computeIfAbsent(world.provider.dimensionId, k -> new DimInfo()).getTypes().computeIfAbsent(type, k -> new TypeInfo());
	}

	public static int performSpawning(WorldServer world, boolean hostiles, boolean peacefuls, boolean rareTick) {
		int interval = rareTick ? 0 : TESConfig.mobSpawnInterval;
		if (interval > 0) {
			int ticks = 0;
			int dimID = world.provider.dimensionId;
			if (TICKS_SINCE_CYCLE.containsKey(dimID)) {
				ticks = TICKS_SINCE_CYCLE.get(dimID);
			}
			ticks--;
			TICKS_SINCE_CYCLE.put(dimID, ticks);
			if (ticks > 0) {
				return 0;
			}
			ticks = interval;
			TICKS_SINCE_CYCLE.put(dimID, ticks);
		}
		if (!hostiles && !peacefuls) {
			return 0;
		}
		int totalSpawned = 0;
		TESSpawnerNPCs.getSpawnableChunks(world, ELIGIBLE_SPAWN_CHUNKS);
		ChunkCoordinates spawnPoint = world.getSpawnPoint();
		label99:
		for (EnumCreatureType creatureType : EnumCreatureType.values()) {
			TypeInfo typeInfo = forDimAndType(world, creatureType);
			boolean canSpawnType;
			if (creatureType.getPeacefulCreature()) {
				canSpawnType = peacefuls;
			} else {
				canSpawnType = hostiles;
			}
			if (creatureType.getAnimal()) {
				canSpawnType = rareTick;
			}
			if (canSpawnType) {
				int count = world.countEntities(creatureType, true);
				int maxCount = TESSpawnDamping.getCreatureSpawnCap(creatureType, world) * ELIGIBLE_SPAWN_CHUNKS.size() / 196;
				if (count <= maxCount) {
					int cycles = Math.max(1, interval);
					for (int c = 0; c < cycles; c++) {
						if (typeInfo.getBlockedCycles() > 0) {
							typeInfo.setBlockedCycles(typeInfo.getBlockedCycles() - 1);
						} else {
							int newlySpawned = 0;
							List<ChunkCoordIntPair> shuffled = TESSpawnerNPCs.shuffle(ELIGIBLE_SPAWN_CHUNKS);
							label97:
							for (ChunkCoordIntPair chunkCoords : shuffled) {
								ChunkPosition chunkposition = TESSpawnerNPCs.getRandomSpawningPointInChunk(world, chunkCoords);
								if (chunkposition != null) {
									int i = chunkposition.chunkPosX;
									int j = chunkposition.chunkPosY;
									int k = chunkposition.chunkPosZ;
									if (world.spawnRandomCreature(creatureType, i, j, k) == null) {
										continue;
									}
									if (!world.getBlock(i, j, k).isNormalCube() && world.getBlock(i, j, k).getMaterial() == creatureType.getCreatureMaterial()) {
										int groupsSpawned = 0;
										while (groupsSpawned < 3) {
											int i1 = i;
											int j1 = j;
											int k1 = k;
											int range = 6;
											BiomeGenBase.SpawnListEntry spawnEntry = null;
											IEntityLivingData entityData = null;
											int attempts = 0;
											while (attempts < 4) {
												i1 += world.rand.nextInt(range) - world.rand.nextInt(range);
												j1 += 0;
												k1 += world.rand.nextInt(range) - world.rand.nextInt(range);
												if (world.blockExists(i1, j1, k1) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(creatureType, world, i1, j1, k1)) {
													float f = i1 + 0.5F;
													float f2 = k1 + 0.5F;
													if (world.getClosestPlayer(f, j1, f2, 24.0D) == null) {
														float f3 = f - spawnPoint.posX;
														float f4 = (float) j1 - spawnPoint.posY;
														float f5 = f2 - spawnPoint.posZ;
														float distSq = f3 * f3 + f4 * f4 + f5 * f5;
														if (distSq >= 576.0F) {
															EntityLiving entity;
															if (spawnEntry == null) {
																spawnEntry = world.spawnRandomCreature(creatureType, i1, j1, k1);
																if (spawnEntry == null) {
																	continue label97;
																}
															}
															try {
																entity = (EntityLiving) spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
															} catch (Exception e) {
																e.printStackTrace();
																return totalSpawned;
															}
															entity.setLocationAndAngles(f, j1, f2, world.rand.nextFloat() * 360.0F, 0.0F);
															Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, f, j1, f2);
															if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere()) {
																totalSpawned++;
																world.spawnEntityInWorld(entity);
																if (!ForgeEventFactory.doSpecialSpawn(entity, world, f, j1, f2)) {
																	entityData = entity.onSpawnWithEgg(entityData);
																}
																newlySpawned++;
																count++;
																if (c > 0 && count > maxCount) {
																	continue label99;
																}
																if (groupsSpawned >= ForgeEventFactory.getMaxSpawnPackSize(entity)) {
																	continue label97;
																}
															}
														}
													}
												}
												attempts++;
											}
											groupsSpawned++;
										}
									}
								}
							}
							if (newlySpawned == 0) {
								typeInfo.setFailedCycles(typeInfo.getFailedCycles() + 1);
								if (typeInfo.getFailedCycles() >= 10) {
									typeInfo.setFailedCycles(0);
									typeInfo.setBlockedCycles(100);
								}
							} else if (typeInfo.getFailedCycles() > 0) {
								typeInfo.setFailedCycles(typeInfo.getFailedCycles() - 1);
							}
						}
					}
				}
			}
		}
		return totalSpawned;
	}

	public static void worldGenSpawnAnimals(World world, TESBiome biome, TESBiomeVariant variant, int i, int k, Random rand) {
		int spawnRange = 16;
		int spawnFuzz = 5;
		List<BiomeGenBase.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.creature);
		if (!spawnList.isEmpty()) {
			while (rand.nextFloat() < biome.getSpawningChance()) {
				BiomeGenBase.SpawnListEntry spawnEntry = (BiomeGenBase.SpawnListEntry) WeightedRandom.getRandomItem(world.rand, spawnList);
				int count = MathHelper.getRandomIntegerInRange(rand, spawnEntry.minGroupCount, spawnEntry.maxGroupCount);
				IEntityLivingData entityData = null;
				int packX = i + rand.nextInt(spawnRange);
				int packZ = k + rand.nextInt(spawnRange);
				int i1 = packX;
				int k1 = packZ;
				for (int l = 0; l < count; ++l) {
					int attempts = 4;
					boolean spawned = false;
					for (int a = 0; !spawned && a < attempts; ++a) {
						int j1 = world.getTopSolidOrLiquidBlock(i1, k1);
						if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, world, i1, j1, k1)) {
							EntityLiving entity;
							float f = i1 + 0.5f;
							float f2 = k1 + 0.5f;
							try {
								entity = (EntityLiving) spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
							} catch (Exception exception) {
								exception.printStackTrace();
								continue;
							}
							boolean canSpawn = !(entity instanceof TESEntityBear) || TESEntityBear.canWorldGenSpawnAt(biome, variant);
							if (canSpawn) {
								entity.setLocationAndAngles(f, j1, f2, rand.nextFloat() * 360.0f, 0.0f);
								world.spawnEntityInWorld(entity);
								entityData = entity.onSpawnWithEgg(entityData);
								spawned = true;
							}
						}
						i1 += rand.nextInt(spawnFuzz) - rand.nextInt(spawnFuzz);
						k1 += rand.nextInt(spawnFuzz) - rand.nextInt(spawnFuzz);
						while (i1 < i || i1 >= i + spawnRange || k1 < k || k1 >= k + spawnRange) {
							i1 = packX + rand.nextInt(spawnFuzz) - rand.nextInt(spawnFuzz);
							k1 = packZ + rand.nextInt(spawnFuzz) - rand.nextInt(spawnFuzz);
						}
					}
				}
			}
		}
	}

	private static class DimInfo {
		private final Map<EnumCreatureType, TypeInfo> types = new EnumMap<>(EnumCreatureType.class);

		private Map<EnumCreatureType, TypeInfo> getTypes() {
			return types;
		}
	}

	private static class TypeInfo {
		private int failedCycles;
		private int blockedCycles;

		private int getBlockedCycles() {
			return blockedCycles;
		}

		private void setBlockedCycles(int blockedCycles) {
			this.blockedCycles = blockedCycles;
		}

		private int getFailedCycles() {
			return failedCycles;
		}

		private void setFailedCycles(int failedCycles) {
			this.failedCycles = failedCycles;
		}
	}
}