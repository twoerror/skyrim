package tes.common.quest;

import tes.client.TESKeyHandler;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESItems;
import tes.common.database.TESSpeech;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TESMiniQuestWelcome extends TESMiniQuest {

	protected TESMiniQuestWelcome(TESPlayerData pd) {
		super(pd);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected float getAlignmentBonus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getCoinBonus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getCompletionFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getObjectiveInSpeech() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgressedObjectiveInSpeech() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getQuestIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestObjective() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestProgressShorthand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleEvent(TESMiniQuestEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInteract(EntityPlayer entityplayer, TESEntityNPC npc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKill(EntityPlayer entityplayer, EntityLivingBase entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKilledByPlayer(EntityPlayer entityplayer, EntityPlayer killer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerTick() {
		// TODO Auto-generated method stub
		
	}	}