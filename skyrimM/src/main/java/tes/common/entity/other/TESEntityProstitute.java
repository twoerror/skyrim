package tes.common.entity.other;

import tes.common.database.TESAchievement;
import tes.common.database.TESFoods;
import tes.common.database.TESNames;
import tes.common.entity.ai.TESEntityAIDrink;
import tes.common.entity.ai.TESEntityAIEat;
import tes.common.entity.ai.TESEntityAIFollowHiringPlayer;
import tes.common.entity.ai.TESEntityAIHiredRemainStill;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Locale;

public class TESEntityProstitute extends TESEntityHumanBase {
	private ProstituteType prostituteType;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityProstitute(World world) {
		super(world);
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		addTargetTasks(false);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new TESEntityAIHiredRemainStill(this));
		tasks.addTask(2, new EntityAIPanic(this, 1.4));
		tasks.addTask(3, new TESEntityAIFollowHiringPlayer(this));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(4, new TESEntityAIEat(this, TESFoods.WESTEROS, 8000));
		tasks.addTask(5, new TESEntityAIDrink(this, TESFoods.WESTEROS_DRINK, 8000));
		tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 8.0f, 0.02f));
		tasks.addTask(7, new EntityAIWatchClosest2(this, TESEntityNPC.class, 5.0f, 0.02f));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(18, (byte) 0);
		setProstituteType(prostituteType);
	}

	@Override
	public TESAchievement getKillAchievement() {
		return TESAchievement.killProstitute;
	}

	@Override
	public String getNPCName() {
		return familyInfo.getName();
	}

	public ProstituteType getProstituteType() {
		byte i = dataWatcher.getWatchableObjectByte(18);
		return ProstituteType.forID(i);
	}

	private void setProstituteType(ProstituteType t) {
		dataWatcher.updateObject(18, (byte) t.getProstituteID());
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			return "standard/special/prostitute_friendly";
		}
		return "standard/special/prostitute_hostile";
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("ProstituteType")) {
			setProstituteType(ProstituteType.forID(nbt.getByte("ProstituteType")));
		}
	}

	@Override
	public void setupNPCGender() {
		familyInfo.setMale(false);
	}

	@Override
	public void setupNPCName() {
		String name = null;
		switch (rand.nextInt(10)) {
			case 0:
				name = TESNames.getWildName(rand, familyInfo.isMale());
				prostituteType = ProstituteType.WILD;
				break;
			case 1:
				name = TESNames.getGhiscarName(rand, familyInfo.isMale());
				prostituteType = ProstituteType.DARK;
				break;
			case 2:
				name = TESNames.getSothoryosName(rand, familyInfo.isMale());
				prostituteType = ProstituteType.BLACK;
				break;
			case 3:
				switch (rand.nextInt(2)) {
					case 0:
						name = TESNames.getDothrakiName(rand, familyInfo.isMale());
						break;
					case 1:
						name = TESNames.getLhazarName(rand, familyInfo.isMale());
						break;
				}
				prostituteType = ProstituteType.NOMAD;
				break;
			case 4:
				name = TESNames.getYiTiName(rand, familyInfo.isMale());
				prostituteType = ProstituteType.YITI;
				break;
			case 5:
				name = TESNames.getJogosName(rand, familyInfo.isMale());
				prostituteType = ProstituteType.JOGOS;
				break;
			default:
				switch (rand.nextInt(3)) {
					case 0:
						name = TESNames.getWesterosName(rand, familyInfo.isMale());
						break;
					case 1:
						name = TESNames.getEssosName(rand, familyInfo.isMale());
						break;
					case 2:
						name = TESNames.getQarthName(rand, familyInfo.isMale());
						break;
				}
				switch (rand.nextInt(5)) {
					case 0:
						prostituteType = ProstituteType.LIGHT_1;
						break;
					case 1:
						prostituteType = ProstituteType.LIGHT_2;
						break;
					case 2:
						prostituteType = ProstituteType.LIGHT_3;
						break;
					case 3:
						prostituteType = ProstituteType.LIGHT_4;
						break;
					case 4:
						prostituteType = ProstituteType.LIGHT_5;
						break;
				}
				break;
		}
		familyInfo.setName(name);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("ProstituteType", (byte) getProstituteType().getProstituteID());
	}

	public enum ProstituteType {
		LIGHT_1(0), LIGHT_2(1), LIGHT_3(2), LIGHT_4(3), LIGHT_5(4), DARK(5), BLACK(6), NOMAD(7), YITI(8), JOGOS(9), WILD(10);

		private final int prostituteID;

		ProstituteType(int i) {
			prostituteID = i;
		}

		private static ProstituteType forID(int ID) {
			for (ProstituteType t : values()) {
				if (t.prostituteID == ID) {
					return t;
				}
			}
			return LIGHT_1;
		}

		public String textureName() {
			return name().toLowerCase(Locale.ROOT);
		}

		private int getProstituteID() {
			return prostituteID;
		}
	}
}