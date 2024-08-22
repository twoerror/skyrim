package tes;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import tes.common.TESCommonFactory;
import tes.common.TESCommonProxy;
import tes.common.TESGuiMessageTypes;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.command.TESCommandAchievement;
import tes.common.command.TESCommandAdminHideMap;
import tes.common.command.TESCommandAlignment;
import tes.common.command.TESCommandAlignmentSee;
import tes.common.command.TESCommandAllowStructures;
import tes.common.command.TESCommandBanStructures;
import tes.common.command.TESCommandConquest;
import tes.common.command.TESCommandDate;
import tes.common.command.TESCommandDragon;
import tes.common.command.TESCommandEnableAlignmentZones;
import tes.common.command.TESCommandEnchant;
import tes.common.command.TESCommandFactionRelations;
import tes.common.command.TESCommandFastTravelClock;
import tes.common.command.TESCommandFellowship;
import tes.common.command.TESCommandFellowshipMessage;
import tes.common.command.TESCommandInvasion;
import tes.common.command.TESCommandMessageFixed;
import tes.common.command.TESCommandPledgeCooldown;
import tes.common.command.TESCommandSpawnDamping;
import tes.common.command.TESCommandSummon;
import tes.common.command.TESCommandTime;
import tes.common.command.TESCommandTimeVanilla;
import tes.common.command.TESCommandWaypointCooldown;
import tes.common.command.TESCommandWaypoints;
import tes.common.command.TESCommandWikiGen;
import tes.common.database.TESAchievement;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.inanimate.TESEntityPortal;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.faction.TESFaction;
import tes.common.fellowship.TESFellowship;
import tes.common.item.other.TESItemBanner;
import tes.common.util.TESLog;
import tes.common.util.TESReflection;
import tes.common.world.TESWorldType;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.TESBeziers;
import tes.common.world.map.TESWaypoint;
import tes.common.world.structure.TESStructureRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandTime;
import net.minecraft.command.IEntitySelector;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;

@SuppressWarnings({"WeakerAccess", "PublicField"})
@Mod(modid = "tes", useMetadata = true)
public class TES {
	public static final String NAME = "Game of Thrones";
	public static final String VERSION = "24.07.10";
	public static final String LANGUAGES = "Русский (ru), Українська (uk), English (en), Français (fr), Deutsch (de), Polska (pl), Türkçe (tr), 中文 (zh)";

	public static final List<String> DEVS = new ArrayList<String>();

	@SidedProxy(clientSide = "tes.client.TESClientProxy", serverSide = "tes.common.TESCommonProxy")
	public static TESCommonProxy proxy;

	@Mod.Instance("tes")
	public static TES instance;

	public static WorldType worldTypeTES;
	public static WorldType worldTypeTESEmpty;
	public static WorldType worldTypeTESClassic;

	static {
		DEVS.add("76ae4f2f-e70a-4680-b7cd-3100fa8b567b");
		DEVS.add("40cd453d-4c71-4afe-9ae3-a2b8cb2b6f00");
		DEVS.add("ce6eec82-0678-4be3-933d-05acb902d558");
		DEVS.add("ce924ff6-8450-41ad-865e-89c5897837c4");
		DEVS.add("9aee5b32-8e19-4d4b-a2d6-1318af62733d");
		DEVS.add("694406b3-10e4-407d-99bb-17218696627a");
		DEVS.add("1f63e38e-4059-4a4f-b7c4-0fac4a48e744");
		DEVS.add("72fd4cfd-064e-4cf1-874d-74000c152f48");
		DEVS.add("a05ba4aa-2cd0-43b1-957c-7971c9af53d4");
		DEVS.add("22be67c2-ba43-48db-b2ba-32857e78ddad");
		DEVS.add("c52f6daa-1479-4304-b8de-30b7b1903b23");
		DEVS.add("56c71aab-8a68-465d-b386-5f721dd68df6");
		DEVS.add("188e4e9c-8c67-443d-9b6c-a351076a43e3");
		DEVS.add("f8cc9b45-509a-4034-8740-0b84ce7e4492");
	}

	private static Map<String, Integer> getModContentInfo() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("achievements", TESAchievement.CONTENT.size());
		map.put("banners", TESItemBanner.BannerType.values().length);
		map.put("mobs", TESEntityRegistry.CONTENT.size());
		map.put("structures", TESStructureRegistry.CONTENT.size());
		map.put("biomes", TESBiome.CONTENT.size());
		map.put("beziers", TESBeziers.CONTENT.size());
		map.put("waypoints", TESWaypoint.values().length);
		map.put("factions", TESFaction.values().length);
		map.put("items", TESItems.CONTENT.size());
		map.put("blocks", TESBlocks.CONTENT.size());
		return map;
	}

	public static boolean canDropLoot(World world) {
		return world.getGameRules().getGameRuleBooleanValue("doMobLoot");
	}

	public static boolean canGrief(World world) {
		return world.getGameRules().getGameRuleBooleanValue("mobGriefing");
	}

	public static boolean canNPCAttackEntity(EntityCreature attacker, EntityLivingBase target, boolean isPlayerDirected) {
		if (target == null || !target.isEntityAlive()) {
			return false;
		}
		TESFaction attackerFaction = getNPCFaction(attacker);
		if (attacker instanceof TESEntityNPC) {
			TESEntityNPC npc = (TESEntityNPC) attacker;
			EntityPlayer hiringPlayer = npc.getHireableInfo().getHiringPlayer();
			if (hiringPlayer != null) {
				if (target == hiringPlayer || target.riddenByEntity == hiringPlayer) {
					return false;
				}
				TESEntityNPC targetNPC = null;
				if (target instanceof TESEntityNPC) {
					targetNPC = (TESEntityNPC) target;
				} else if (target.riddenByEntity instanceof TESEntityNPC) {
					targetNPC = (TESEntityNPC) target.riddenByEntity;
				}
				if (targetNPC != null && targetNPC.getHireableInfo().isActive()) {
					UUID hiringPlayerUUID = npc.getHireableInfo().getHiringPlayerUUID();
					UUID targetHiringPlayerUUID = targetNPC.getHireableInfo().getHiringPlayerUUID();
					if (hiringPlayerUUID != null && hiringPlayerUUID.equals(targetHiringPlayerUUID) && !attackerFaction.isBadRelation(getNPCFaction(targetNPC))) {
						return false;
					}
				}
			}
		}
		if (attackerFaction.isAllowEntityRegistry()) {
			if (attackerFaction.isGoodRelation(getNPCFaction(target)) && attacker.getAttackTarget() != target) {
				return false;
			}
			if (target.riddenByEntity != null && attackerFaction.isGoodRelation(getNPCFaction(target.riddenByEntity)) && attacker.getAttackTarget() != target && attacker.getAttackTarget() != target.riddenByEntity) {
				return false;
			}
			if (!isPlayerDirected) {
				return (!(target instanceof EntityPlayer) || !(TESLevelData.getData((EntityPlayer) target).getAlignment(attackerFaction) >= 0.0f) || attacker.getAttackTarget() == target) && (!(target.riddenByEntity instanceof EntityPlayer) || TESLevelData.getData((EntityPlayer) target.riddenByEntity).getAlignment(attackerFaction) < 0.0f || attacker.getAttackTarget() == target || attacker.getAttackTarget() == target.riddenByEntity);
			}
		}
		return true;
	}

	public static boolean canPlayerAttackEntity(EntityPlayer attacker, EntityLivingBase target, boolean warnFriendlyFire) {
		if (target == null || !target.isEntityAlive()) {
			return false;
		}
		TESPlayerData playerData = TESLevelData.getData(attacker);
		boolean friendlyFire = false;
		boolean friendlyFireEnabled = playerData.getFriendlyFire();
		if (target instanceof EntityPlayer && target != attacker) {
			EntityPlayer targetPlayer = (EntityPlayer) target;
			if (!playerData.isSiegeActive()) {
				List<TESFellowship> fellowships = playerData.getFellowships();
				for (TESFellowship fs : fellowships) {
					if (!fs.getPreventPVP() || !fs.containsPlayer(targetPlayer.getUniqueID())) {
						continue;
					}
					return false;
				}
			}
		}
		Entity targetNPC = null;
		TESFaction targetNPCFaction;
		if (getNPCFaction(target.riddenByEntity) != TESFaction.UNALIGNED) {
			targetNPC = target.riddenByEntity;
		}
		if (targetNPC != null) {
			targetNPCFaction = getNPCFaction(targetNPC);
			if (targetNPC instanceof TESEntityNPC) {
				TESEntityNPC targettesNPC = (TESEntityNPC) targetNPC;
				TESHireableInfo hiredInfo = targettesNPC.getHireableInfo();
				if (hiredInfo.isActive()) {
					if (hiredInfo.getHiringPlayer() == attacker) {
						return false;
					}
					if (targettesNPC.getAttackTarget() != attacker && !playerData.isSiegeActive()) {
						UUID hiringPlayerID = hiredInfo.getHiringPlayerUUID();
						List<TESFellowship> fellowships = playerData.getFellowships();
						for (TESFellowship fs : fellowships) {
							if (!fs.getPreventHiredFriendlyFire() || !fs.containsPlayer(hiringPlayerID)) {
								continue;
							}
							return false;
						}
					}
				}
			}
			if (targetNPC instanceof EntityLiving && ((EntityLiving) targetNPC).getAttackTarget() != attacker && TESLevelData.getData(attacker).getAlignment(targetNPCFaction) > 0.0f) {
				friendlyFire = true;
			}
		}
		if (!friendlyFireEnabled && friendlyFire) {
			if (warnFriendlyFire) {
				TESLevelData.getData(attacker).sendMessageIfNotReceived(TESGuiMessageTypes.FRIENDLY_FIRE);
			}
			return false;
		}
		return true;
	}

	public static boolean canSpawnMobs(World world) {
		return world.getGameRules().getGameRuleBooleanValue("doMobSpawning");
	}

	public static boolean doDayCycle(World world) {
		return world.getGameRules().getGameRuleBooleanValue("doDaylightCycle");
	}

	public static boolean doFireTick(World world) {
		return world.getGameRules().getGameRuleBooleanValue("doFireTick");
	}

	public static void dropContainerItems(IInventory container, World world, int i, int j, int k) {
		for (int l = 0; l < container.getSizeInventory(); ++l) {
			ItemStack item = container.getStackInSlot(l);
			if (item == null) {
				continue;
			}
			float f = world.rand.nextFloat() * 0.8f + 0.1f;
			float f1 = world.rand.nextFloat() * 0.8f + 0.1f;
			float f2 = world.rand.nextFloat() * 0.8f + 0.1f;
			while (item.stackSize > 0) {
				int i1 = world.rand.nextInt(21) + 10;
				if (i1 > item.stackSize) {
					i1 = item.stackSize;
				}
				item.stackSize -= i1;
				EntityItem entityItem = new EntityItem(world, i + f, j + f1, k + f2, new ItemStack(item.getItem(), i1, item.getItemDamage()));
				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}
				entityItem.motionX = world.rand.nextGaussian() * 0.05000000074505806;
				entityItem.motionY = world.rand.nextGaussian() * 0.05000000074505806 + 0.20000000298023224;
				entityItem.motionZ = world.rand.nextGaussian() * 0.05000000074505806;
				world.spawnEntityInWorld(entityItem);
			}
		}
	}

	public static EntityPlayer getDamagingPlayerIncludingUnits(DamageSource damagesource) {
		if (damagesource.getEntity() instanceof EntityPlayer) {
			return (EntityPlayer) damagesource.getEntity();
		}
		if (damagesource.getEntity() instanceof TESEntityNPC) {
			TESEntityNPC npc = (TESEntityNPC) damagesource.getEntity();
			if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer() != null) {
				return npc.getHireableInfo().getHiringPlayer();
			}
		}
		return null;
	}

	public static ModContainer getModContainer() {
		return FMLCommonHandler.instance().findContainerFor(instance);
	}

	public static TESFaction getNPCFaction(Entity entity) {
		if (entity == null) {
			return TESFaction.UNALIGNED;
		}
		if (entity instanceof TESEntityNPC) {
			return ((TESEntityNPC) entity).getFaction();
		}
		EntityList.getEntityString(entity);
		return TESFaction.UNALIGNED;
	}

	public static int getTrueTopBlock(World world, int i, int k) {
		Chunk chunk = world.getChunkProvider().provideChunk(i >> 4, k >> 4);
		for (int j = chunk.getTopFilledSegment() + 15; j > 0; --j) {
			Block block = world.getBlock(i, j, k);
			if (!block.getMaterial().blocksMovement() || block.getMaterial() == Material.leaves || block.isFoliage(world, i, j, k)) {
				continue;
			}
			return j + 1;
		}
		return -1;
	}

	public static boolean isAprilFools() {
		LocalDate calendar = LocalDate.now();
		return calendar.getMonth() == Month.APRIL && calendar.getDayOfMonth() == 1;
	}

	public static boolean isGuyFawkes() {
		LocalDate calendar = LocalDate.now();
		return calendar.getMonth() == Month.NOVEMBER && calendar.getDayOfMonth() == 5;
	}

	public static boolean isNewYear() {
		LocalDate calendar = LocalDate.now();
		return calendar.getMonth() == Month.JANUARY && calendar.getDayOfMonth() == 1 || calendar.getMonth() == Month.DECEMBER && calendar.getDayOfMonth() == 31;
	}

	public static boolean isOpaque(IBlockAccess world, int i, int j, int k) {
		return world.getBlock(i, j, k).isOpaqueCube();
	}

	public static boolean isOreNameEqual(ItemStack itemstack, String name) {
		Iterable<ItemStack> list = OreDictionary.getOres(name);
		for (ItemStack obj : list) {
			if (!OreDictionary.itemMatches(obj, itemstack, false)) {
				continue;
			}
			return true;
		}
		return false;
	}

	public static boolean isUkraine() {
		LocalDate calendar = LocalDate.now();
		return calendar.getMonth() == Month.AUGUST && calendar.getDayOfMonth() == 24;
	}

	public static IEntitySelector selectLivingExceptCreativePlayers() {
		return new EntitySelectorImpl1();
	}

	public static IEntitySelector selectNonCreativePlayers() {
		return new EntitySelectorImpl2();
	}

	public static void transferEntityToDimension(Entity entity, int newDimension, Teleporter teleporter) {
		if (entity instanceof TESEntityPortal) {
			return;
		}
		if (!entity.worldObj.isRemote && !entity.isDead) {
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			int oldDimension = entity.dimension;
			WorldServer oldWorld = minecraftserver.worldServerForDimension(oldDimension);
			WorldServer newWorld = minecraftserver.worldServerForDimension(newDimension);
			entity.dimension = newDimension;
			entity.worldObj.removeEntity(entity);
			entity.isDead = false;
			minecraftserver.getConfigurationManager().transferEntityToWorld(entity, oldDimension, oldWorld, newWorld, teleporter);
			Entity newEntity = EntityList.createEntityByName(EntityList.getEntityString(entity), newWorld);
			if (newEntity != null) {
				newEntity.copyDataFrom(entity, true);
				newWorld.spawnEntityInWorld(newEntity);
			}
			entity.isDead = true;
			oldWorld.resetUpdateEntityTick();
			newWorld.resetUpdateEntityTick();
			if (newEntity != null) {
				newEntity.timeUntilPortal = newEntity.getPortalCooldown();
			}
		}
	}

	@Mod.EventHandler
	@SuppressWarnings("MethodMayBeStatic")
	public void preInit(FMLPreInitializationEvent event) {
		TESLog.findLogger();

		TESCommonFactory.preInit();

		worldTypeTES = new TESWorldType("tes");
		worldTypeTESEmpty = new TESWorldType("tesEmpty");
		worldTypeTESClassic = new TESWorldType("tesClassic");

		TESLoader.preInit();

		proxy.preInit();
	}

	@Mod.EventHandler
	@SuppressWarnings("MethodMayBeStatic")
	public void onInit(FMLInitializationEvent event) {
		TESLoader.onInit();

		proxy.onInit();
	}

	@Mod.EventHandler
	@SuppressWarnings("MethodMayBeStatic")
	public void postInit(FMLPostInitializationEvent event) {
		TESLoader.postInit();

		Map<String, Integer> info = getModContentInfo();

		for (Map.Entry<String, Integer> entry : info.entrySet()) {
			TESLog.getLogger().info("Hummel009: Registered {} {}", entry.getValue(), entry.getKey());
		}
		proxy.postInit();
	}

	@Mod.EventHandler
	@SuppressWarnings({"MethodMayBeStatic", "CommentedOutCode", "EmptyMethod"})
	public void onMissingMappings(FMLMissingMappingsEvent event) {
		/*for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
			Item item;
			Block block;
			String newName;
			if (mapping.type == GameRegistry.Type.BLOCK) {
				if (mapping.name.contains("Carnotite")) {
					newName = mapping.name.replace("Carnotite", "Labradorite");
				} else if (mapping.name.contains("carnotite")) {
					newName = mapping.name.replace("carnotite", "labradorite");
				} else if (mapping.name.contains("chest_essos")) {
					newName = mapping.name.replace("chest_essos", "chest_sandstone");
				} else {
					newName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapping.name);
				}
				block = (Block) Block.blockRegistry.getObject(newName);
				if (block != null) {
					mapping.remap(block);
				}
			}
			if (mapping.type == GameRegistry.Type.ITEM) {
				if (mapping.name.contains("Carnotite")) {
					newName = mapping.name.replace("Carnotite", "Labradorite");
				} else if (mapping.name.contains("ignot")) {
					newName = mapping.name.replace("ignot", "ingot");
				} else if (mapping.name.contains("carnotite")) {
					newName = mapping.name.replace("carnotite", "labradorite");
				} else {
					newName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapping.name);
				}
				item = (Item) Item.itemRegistry.getObject(newName);
				if (item != null) {
					mapping.remap(item);
				}
			}
		}*/
	}

	@Mod.EventHandler
	@SuppressWarnings("MethodMayBeStatic")
	public void onServerStarting(FMLServerStartingEvent event) {
		WorldServer world = DimensionManager.getWorld(0);

		TESReflection.removeCommand(CommandTime.class);
		TESReflection.removeCommand(CommandMessage.class);

		Collection<CommandBase> command = new ArrayList<CommandBase>();
		command.add(new TESCommandTimeVanilla());
		command.add(new TESCommandMessageFixed());
		command.add(new TESCommandTime());
		command.add(new TESCommandAlignment());
		command.add(new TESCommandSummon());
		command.add(new TESCommandFastTravelClock());
		command.add(new TESCommandWaypointCooldown());
		command.add(new TESCommandDate());
		command.add(new TESCommandWaypoints());
		command.add(new TESCommandAlignmentSee());
		command.add(new TESCommandFellowship());
		command.add(new TESCommandFellowshipMessage());
		command.add(new TESCommandEnableAlignmentZones());
		command.add(new TESCommandEnchant());
		command.add(new TESCommandSpawnDamping());
		command.add(new TESCommandFactionRelations());
		command.add(new TESCommandPledgeCooldown());
		command.add(new TESCommandConquest());
		command.add(new TESCommandDragon());
		command.add(new TESCommandInvasion());
		command.add(new TESCommandAchievement());
		command.add(new TESCommandWikiGen());

		if (event.getServer().isDedicatedServer()) {
			command.add(new TESCommandBanStructures());
			command.add(new TESCommandAllowStructures());
			command.add(new TESCommandAdminHideMap());
		}
		for (CommandBase element : command) {
			event.registerServerCommand(element);
		}
		proxy.testReflection(world);
	}

	private static class EntitySelectorImpl1 implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return entity instanceof EntityLivingBase && entity.isEntityAlive() && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode);
		}
	}

	private static class EntitySelectorImpl2 implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return entity instanceof EntityPlayer && entity.isEntityAlive() && !((EntityPlayer) entity).capabilities.isCreativeMode;
		}
	}
}