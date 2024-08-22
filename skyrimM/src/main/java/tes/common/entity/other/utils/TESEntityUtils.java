package tes.common.entity.other.utils;

import tes.common.database.TESItems;
import tes.common.entity.other.TESEntityHumanBase;
import tes.common.entity.other.TESEntityNPC;
import tes.common.item.other.TESItemRobes;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class TESEntityUtils {
	private static final int[] LEATHER_DYES = {10855845, 8026746, 5526612, 3684408, 8350297, 10388590, 4799795, 5330539, 4211801, 2632504};
	private static final int[] TURBAN_COLORS = {1643539, 6309443, 7014914, 7809314, 5978155};

	private static final Collection<Class<? extends TESEntityHumanBase>> BASIC_NPC = new HashSet<>();
	private static final Collection<Class<? extends TESEntityHumanBase>> CAN_BE_MARRIED = new HashSet<>();
	private static final Collection<Class<? extends TESEntityHumanBase>> CAN_BE_ROBBED = new HashSet<>();
	private static final Collection<Class<? extends TESEntityHumanBase>> CAN_SMOKE_DRINK = new HashSet<>();

	static {
		
		CAN_BE_MARRIED.addAll(BASIC_NPC);
		CAN_BE_ROBBED.addAll(BASIC_NPC);

			}

	private TESEntityUtils() {
	}

	public static boolean canBeMarried(TESEntityNPC npc) {
		return npc.getFamilyInfo().getAge() >= 0 && CAN_BE_MARRIED.contains(npc.getClass());
	}

	public static boolean canBeRobbed(TESEntityNPC npc) {
		return npc.getFamilyInfo().getAge() >= 0 && CAN_BE_ROBBED.contains(npc.getClass());
	}

	public static boolean canSmokeDrink(TESEntityNPC npc) {
		return npc.getFamilyInfo().isMale() && npc.getFamilyInfo().getAge() >= 0 && CAN_SMOKE_DRINK.contains(npc.getClass());
	}

	private static ItemStack dyeLeather(ItemStack itemstack, Random rand) {
		int i = rand.nextInt(LEATHER_DYES.length);
		int color = LEATHER_DYES[i];
		((ItemArmor) itemstack.getItem()).func_82813_b(itemstack, color);
		return itemstack;
	}

	public static EntityAITasks.EntityAITaskEntry removeAITask(EntityCreature entity, Class<? extends EntityAIBase> taskClass) {
		int i;
		EntityAITasks.EntityAITaskEntry taskEntry;
		for (i = 0; i < entity.tasks.taskEntries.size(); ++i) {
			taskEntry = (EntityAITasks.EntityAITaskEntry) entity.tasks.taskEntries.get(i);
			if (!taskClass.isAssignableFrom(taskEntry.action.getClass())) {
				continue;
			}
			entity.tasks.removeTask(taskEntry.action);
			return taskEntry;
		}
		for (i = 0; i < entity.targetTasks.taskEntries.size(); ++i) {
			taskEntry = (EntityAITasks.EntityAITaskEntry) entity.targetTasks.taskEntries.get(i);
			if (!taskClass.isAssignableFrom(taskEntry.action.getClass())) {
				continue;
			}
			entity.targetTasks.removeTask(taskEntry.action);
			return taskEntry;
		}
		return null;
	}

	public static void setLevymanArmor(TESEntityNPC npc, Random rand) {
		setLevymanArmor(npc, rand, false);
	}

	public static void setLevymanArmor(TESEntityNPC npc, Random rand, boolean forceTurban) {
		boolean alreadyHasChain = false;
		npc.setCurrentItemOrArmor(1, dyeLeather(new ItemStack(Items.leather_boots), rand));

		int legs = rand.nextInt(10);
		if (legs == 0 || legs == 1) {
			npc.setCurrentItemOrArmor(2, new ItemStack(TESItems.bronzeChainmailLeggings));
			alreadyHasChain = true;
		} else if (legs == 2 || legs == 3 || legs == 4) {
			npc.setCurrentItemOrArmor(2, new ItemStack(Items.chainmail_leggings));
			alreadyHasChain = true;
		} else {
			npc.setCurrentItemOrArmor(2, dyeLeather(new ItemStack(Items.leather_leggings), rand));
		}
		int body = rand.nextInt(10);
		if ((body == 0 || body == 1) && !alreadyHasChain) {
			npc.setCurrentItemOrArmor(3, new ItemStack(TESItems.bronzeChainmailChestplate));
		} else if ((body == 2 || body == 3 || body == 4) && !alreadyHasChain) {
			npc.setCurrentItemOrArmor(3, new ItemStack(Items.chainmail_chestplate));
		} else {
			npc.setCurrentItemOrArmor(3, dyeLeather(new ItemStack(Items.leather_chestplate), rand));
		}
		if (forceTurban) {
			ItemStack turban = new ItemStack(TESItems.bronzeHelmet);
			int robeColor = TURBAN_COLORS[rand.nextInt(TURBAN_COLORS.length)];
			TESItemRobes.setRobesColor(turban, robeColor);
			npc.setCurrentItemOrArmor(4, turban);
		} else if (rand.nextInt(5) != 0) {
			npc.setCurrentItemOrArmor(4, dyeLeather(new ItemStack(Items.leather_helmet), rand));
		}
	}
}