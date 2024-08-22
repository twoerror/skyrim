package tes.common;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import tes.TES;
import tes.common.entity.other.inanimate.TESEntityPortal;
import tes.common.faction.TESFactionBounties;
import tes.common.faction.TESFactionRelations;
import tes.common.fellowship.TESFellowshipData;
import tes.common.item.other.TESItemStructureSpawner;
import tes.common.util.TESReflection;
import tes.common.world.TESTeleporter;
import tes.common.world.TESWorldInfo;
import tes.common.world.TESWorldProvider;
import tes.common.world.biome.TESClimateType;
import tes.common.world.biome.variant.TESBiomeVariantStorage;
import tes.common.world.map.TESConquestGrid;
import tes.common.world.spawning.TESEventSpawner;
import tes.common.world.spawning.TESSpawnerNPCs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TESTickHandlerServer {
	public static final TESTickHandlerServer INSTANCE = new TESTickHandlerServer();

	public static final Map<EntityPlayer, Integer> PLAYERS_IN_PORTALS = new HashMap<>();

	private TESTickHandlerServer() {
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		World world = player.worldObj;
		if (world == null || world.isRemote) {
			return;
		}
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP entityplayer = (EntityPlayerMP) player;
			if (event.phase == TickEvent.Phase.START && entityplayer.playerNetServerHandler != null && !(entityplayer.playerNetServerHandler instanceof TESNetHandlerPlayServer)) {
				entityplayer.playerNetServerHandler = new TESNetHandlerPlayServer(MinecraftServer.getServer(), entityplayer.playerNetServerHandler.netManager, entityplayer);
			}
			if (event.phase == TickEvent.Phase.END) {
				TESLevelData.getData(entityplayer).onUpdate(entityplayer, (WorldServer) world);
				NetHandlerPlayServer netHandler = entityplayer.playerNetServerHandler;
				if (netHandler instanceof TESNetHandlerPlayServer) {
					((TESNetHandlerPlayServer) netHandler).update();
				}
				ItemStack heldItem = entityplayer.getHeldItem();
				if (heldItem != null && (heldItem.getItem() instanceof ItemWritableBook || heldItem.getItem() instanceof ItemEditableBook)) {
					entityplayer.func_143004_u();
				}
				if (entityplayer.dimension == 0 && TESLevelData.getMadePortal() == 0) {
					List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, entityplayer.boundingBox.expand(16.0D, 16.0D, 16.0D));
					for (EntityItem item : items) {
						if (TESLevelData.getMadePortal() == 0 && item.getEntityItem() != null && item.getEntityItem().getItem() == Items.iron_sword && item.isBurning()) {
							TESLevelData.setMadePortal(1);
							TESLevelData.markOverworldPortalLocation(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.posY), MathHelper.floor_double(item.posZ));
							item.setDead();
							world.createExplosion(entityplayer, item.posX, item.posY + 3.0D, item.posZ, 3.0F, true);
							TESEntityPortal portal = new TESEntityPortal(world);
							portal.setLocationAndAngles(item.posX, item.posY + 3.0D, item.posZ, 0.0F, 0.0F);
							world.spawnEntityInWorld(portal);
						}
					}
				}
				if ((entityplayer.dimension == 0 || entityplayer.dimension == TESDimension.GAME_OF_THRONES.getDimensionID()) && PLAYERS_IN_PORTALS.containsKey(entityplayer)) {
					List<TESEntityPortal> portals = world.getEntitiesWithinAABB(TESEntityPortal.class, entityplayer.boundingBox.expand(8.0D, 8.0D, 8.0D));
					boolean inPortal = false;
					int i;
					for (i = 0; i < portals.size(); i++) {
						TESEntityPortal portal = portals.get(i);
						if (portal.boundingBox.intersectsWith(entityplayer.boundingBox)) {
							inPortal = true;
							break;
						}
					}
					if (inPortal) {
						i = PLAYERS_IN_PORTALS.get(entityplayer);
						i++;
						PLAYERS_IN_PORTALS.put(entityplayer, i);
						if (i >= 100) {
							int dimension = 0;
							if (entityplayer.dimension == 0) {
								dimension = TESDimension.GAME_OF_THRONES.getDimensionID();
							}
							MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(entityplayer, dimension, new TESTeleporter(DimensionManager.getWorld(dimension), true));
							PLAYERS_IN_PORTALS.remove(entityplayer);
						}
					} else {
						PLAYERS_IN_PORTALS.remove(entityplayer);
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		World world = event.world;
		if (world.isRemote) {
			return;
		}
		if (event.phase == TickEvent.Phase.START && world == DimensionManager.getWorld(0)) {
			if (TESLevelData.isNeedsLoad()) {
				TESLevelData.load();
			}
			if (TESTime.isNeedsLoad()) {
				TESTime.load();
			}
			if (TESFellowshipData.isNeedsLoad()) {
				TESFellowshipData.loadAll();
			}
			if (TESFactionBounties.isNeedsLoad()) {
				TESFactionBounties.loadAll();
			}
			if (TESFactionRelations.isNeedsLoad()) {
				TESFactionRelations.load();
			}
			if (TESConquestGrid.isNeedsLoad()) {
				TESConquestGrid.loadAllZones();
			}
			for (WorldServer dimWorld : MinecraftServer.getServer().worldServers) {
				if (dimWorld.provider instanceof TESWorldProvider) {
					WorldInfo prevWorldInfo = dimWorld.getWorldInfo();
					if (prevWorldInfo.getClass() != TESWorldInfo.class) {
						TESWorldInfo lOTRWorldInfo = new TESWorldInfo(world.getWorldInfo());
						lOTRWorldInfo.setWorldName(prevWorldInfo.getWorldName());
						TESReflection.setWorldInfo(dimWorld, lOTRWorldInfo);
						FMLLog.info("Hummel009: Successfully replaced world info in %s", TESDimension.GAME_OF_THRONES.getDimensionName());
					}
				}
			}
			TESBannerProtection.updateWarningCooldowns();
			TESInterModComms.update();
		}
		if (event.phase == TickEvent.Phase.END) {
			if (world == DimensionManager.getWorld(0)) {
				if (TESLevelData.anyDataNeedsSave()) {
					TESLevelData.save();
				}
				if (TESFellowshipData.anyDataNeedsSave()) {
					TESFellowshipData.saveAll();
				}
				TESFactionBounties.updateAll();
				if (TESFactionBounties.anyDataNeedsSave()) {
					TESFactionBounties.saveAll();
				}
				if (TESFactionRelations.needsSave()) {
					TESFactionRelations.save();
				}
				if (TESConquestGrid.anyChangedZones()) {
					TESConquestGrid.saveChangedZones();
				}
				if (world.getTotalWorldTime() % 600L == 0L) {
					TESLevelData.save();
					TESFellowshipData.saveAll();
					TESFactionBounties.saveAll();
					TESFactionRelations.save();
				}
				int playerDataClearingInterval = TESConfig.playerDataClearingInterval;
				playerDataClearingInterval = Math.max(playerDataClearingInterval, 600);
				if (world.getTotalWorldTime() % playerDataClearingInterval == 0L) {
					TESLevelData.saveAndClearUnusedPlayerData();
				}
				if (TESItemStructureSpawner.getLastStructureSpawnTick() > 0) {
					TESItemStructureSpawner.setLastStructureSpawnTick(TESItemStructureSpawner.getLastStructureSpawnTick() - 1);
				}
				TESTime.update();
			}
			if (world == DimensionManager.getWorld(TESDimension.GAME_OF_THRONES.getDimensionID())) {
				TESDate.update(world);
				if (TES.canSpawnMobs(world)) {
					TESSpawnerNPCs.performSpawning(world);
					TESEventSpawner.performSpawning(world);
				}
				TESConquestGrid.updateZones(world);
				if (!world.playerEntities.isEmpty() && world.getTotalWorldTime() % 20L == 0L) {
					List<EntityPlayer> players = world.playerEntities;
					for (EntityPlayer entityplayer : players) {
						TESLevelData.sendPlayerLocationsToPlayer(entityplayer, world);
					}
				}
			}
			if (world.provider instanceof TESWorldProvider && world.getTotalWorldTime() % 100L == 0L) {
				TESBiomeVariantStorage.performCleanup((WorldServer) world);
			}
			if (world.getTotalWorldTime() % 20L == 0L) {
				TESClimateType.performSeasonalChangesServerSide();
				TESClimateType.performSeasonalChangesClientSide();
			}
		}
	}
}