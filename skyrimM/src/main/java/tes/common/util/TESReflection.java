package tes.common.util;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockStem;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class TESReflection {
	private TESReflection() {
	}

	public static boolean canPistonPushBlock(Block block, World world, int i, int j, int k, boolean flag) {
		try {
			Method method = getPrivateMethod(BlockPistonBase.class, null, new Class[]{Block.class, World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE}, "canPushBlock", "func_150080_a");
			return (Boolean) method.invoke(null, block, world, i, j, k, flag);
		} catch (Exception e) {
			logFailure(e);
			return false;
		}
	}

	public static Item getCropItem(BlockCrops block) {
		try {
			Method method = getPrivateMethod(BlockCrops.class, block, new Class[0], "func_149865_P");
			return (Item) method.invoke(block);
		} catch (Exception e) {
			logFailure(e);
			return null;
		}
	}

	public static float getDamageAmount(Item item) {
		try {
			Field privateField = getPotentiallyObfuscatedPrivateValue(ItemSword.class, "field_150934_a");
			if (privateField != null) {
				privateField.setAccessible(true);
				return (float) privateField.get(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0f;
	}

	public static int getFishHookBobTime(EntityFishHook fishHook) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityFishHook.class, fishHook, "field_146045_ax");
		} catch (Exception e) {
			logFailure(e);
			return 0;
		}
	}

	public static String[] getHorseArmorTextures() {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityHorse.class, null, "horseArmorTextures", "field_110270_bw");
		} catch (Exception e) {
			logFailure(e);
			return new String[0];
		}
	}

	public static AnimalChest getHorseInv(EntityHorse horse) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityHorse.class, horse, "horseChest", "field_110296_bG");
		} catch (Exception e) {
			logFailure(e);
			return null;
		}
	}

	public static IAttribute getHorseJumpStrength() {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityHorse.class, null, "horseJumpStrength", "field_110271_bv");
		} catch (Exception e) {
			logFailure(e);
			return null;
		}
	}

	public static Map<String, HoverEvent.Action> getHoverEventMappings() {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(HoverEvent.Action.class, null, "nameMapping", "field_150690_d");
		} catch (Exception e) {
			logFailure(e);
			return Collections.emptyMap();
		}
	}

	public static Block getOreGenBlock(WorldGenMinable ore) {
		try {
			Field privateField = getPotentiallyObfuscatedPrivateValue(WorldGenMinable.class, "field_150519_a");
			if (privateField != null) {
				privateField.setAccessible(true);
				return (Block) privateField.get(ore);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getOreGenMeta(WorldGenMinable ore) {
		try {
			Field privateField = getPotentiallyObfuscatedPrivateValue(WorldGenMinable.class, "mineableBlockMeta");
			if (privateField != null) {
				privateField.setAccessible(true);
				return (int) privateField.get(ore);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static <T, E> T getPotentiallyObfuscatedPrivateValue(Class<? super E> classToAccess, String fieldName) {
		try {
			return ReflectionHelper.getPrivateValue(classToAccess, null, ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldName));
		} catch (ReflectionHelper.UnableToFindFieldException | ReflectionHelper.UnableToAccessFieldException |
		         NullPointerException e1) {
			try {
				return (T) classToAccess.getDeclaredField(fieldName);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	public static <E> Method getPrivateMethod(Class<? super E> classToAccess, E instance, Class<?>[] methodClasses, String... methodNames) {
		try {
			return ReflectionHelper.findMethod(classToAccess, instance, remapMethodNames(classToAccess.getName(), methodNames), methodClasses);
		} catch (ReflectionHelper.UnableToFindFieldException e) {
			TESLog.getLogger().log(Level.ERROR, "Unable to locate any method %s on type %s", Arrays.toString(methodNames), classToAccess.getName());
			throw e;
		} catch (ReflectionHelper.UnableToAccessFieldException e) {
			TESLog.getLogger().log(Level.ERROR, "Unable to access any method %s on type %s", Arrays.toString(methodNames), classToAccess.getName());
			throw e;
		}
	}

	public static Block getStemFruitBlock(BlockStem block) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(BlockStem.class, block, "field_149877_a");
		} catch (Exception e) {
			logFailure(e);
			return null;
		}
	}

	public static Item.ToolMaterial getToolMaterial(Item item) {
		try {
			Field privateField = getPotentiallyObfuscatedPrivateValue(ItemSword.class, "field_150933_b");
			if (privateField != null) {
				privateField.setAccessible(true);
				return (Item.ToolMaterial) privateField.get(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isBadEffect(Potion potion) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(Potion.class, potion, "isBadEffect", "field_76418_K");
		} catch (Exception e) {
			logFailure(e);
			return false;
		}
	}

	public static boolean isFishHookInGround(EntityFishHook fishHook) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityFishHook.class, fishHook, "field_146051_au");
		} catch (Exception e) {
			logFailure(e);
			return false;
		}
	}

	public static void logFailure(Exception e) {
		TESLog.getLogger().log(Level.ERROR, "TESReflection failed");
		throw new RuntimeException(e);
	}

	public static Entity newEntity(Class<? extends Entity> entityClass, World world) {
		try {
			Class<World>[] param = new Class[]{World.class};
			return entityClass.getDeclaredConstructor(param).newInstance(world);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static WorldGenerator newStructure(Class<? extends WorldGenerator> structureClass, boolean bool) {
		try {
			Class<Boolean>[] param = new Class[]{boolean.class};
			return structureClass.getDeclaredConstructor(param).newInstance(bool);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String[] remapMethodNames(String className, String... methodNames) {
		String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
		String[] mappedNames = new String[methodNames.length];
		int i = 0;
		for (String mName : methodNames) {
			mappedNames[i] = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalClassName, mName, null);
			i++;
		}
		return mappedNames;
	}

	public static void removeCommand(Class<? extends ICommand> commandClass) {
		try {
			CommandHandler handler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
			Map<String, ICommand> commandMap = handler.getCommands();
			Set<ICommand> commandSet = ObfuscationReflectionHelper.getPrivateValue(CommandHandler.class, handler, "commandSet", "field_71561_b");
			Collection<ICommand> mapremoves = new ArrayList<>();
			for (ICommand command : commandMap.values()) {
				if (command.getClass() == commandClass) {
					mapremoves.add(command);
				}
			}
			commandMap.values().removeAll(mapremoves);
			Collection<ICommand> setremoves = new ArrayList<>();
			for (ICommand obj : commandSet) {
				if (obj.getClass() == commandClass) {
					setremoves.add(obj);
				}
			}
			for (ICommand setremove : setremoves) {
				commandSet.remove(setremove);
			}
		} catch (Exception e) {
			logFailure(e);
		}
	}

	public static <T, E> void setFinalField(T instance, E value, Field f) throws Exception {
		try {
			unlockFinalField(f);
			f.set(instance, value);
		} catch (Exception e) {
			TESLog.getLogger().log(Level.ERROR, "Unable to access static field");
			throw e;
		}
	}

	public static void setupHorseInv(EntityHorse horse) {
		try {
			Method method = getPrivateMethod(EntityHorse.class, horse, new Class[0], "func_110226_cD");
			method.invoke(horse);
		} catch (Exception e) {
			logFailure(e);
		}
	}

	public static void setWorldInfo(World world, WorldInfo newWorldInfo) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(World.class, world, newWorldInfo, "worldInfo", "field_72986_A");
		} catch (Exception e) {
			logFailure(e);
		}
	}

	public static void testAll(World world) {
		getHorseJumpStrength();
		getHorseArmorTextures();
		getHorseInv(new EntityHorse(world));
		setupHorseInv(new EntityHorse(world));
		getStemFruitBlock((BlockStem) Blocks.melon_stem);
		getCropItem((BlockCrops) Blocks.potatoes);
		isBadEffect(Potion.poison);
		getHoverEventMappings();
		isFishHookInGround(new EntityFishHook(world));
		getFishHookBobTime(new EntityFishHook(world));
		canPistonPushBlock(Blocks.glass, world, 0, 0, 0, false);
	}

	public static void unlockFinalField(Field f) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & 0xFFFFFFEF);
	}
}