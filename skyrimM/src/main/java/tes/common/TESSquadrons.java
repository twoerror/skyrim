package tes.common;

import tes.common.entity.other.TESEntityNPC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;

public class TESSquadrons {
	public static final int SQUADRON_LENGTH_MAX = 200;

	private TESSquadrons() {
	}

	public static boolean areSquadronsCompatible(TESEntityNPC npc, ItemStack itemstack) {
		String npcSquadron = npc.getHireableInfo().getHiredSquadron();
		String itemSquadron = getSquadron(itemstack);
		if (StringUtils.isNullOrEmpty(npcSquadron)) {
			return StringUtils.isNullOrEmpty(itemSquadron);
		}
		return npcSquadron.equalsIgnoreCase(itemSquadron);
	}

	public static String checkAcceptableLength(String squadron) {
		if (squadron != null && squadron.length() > SQUADRON_LENGTH_MAX) {
			return squadron.substring(0, SQUADRON_LENGTH_MAX);
		}
		return squadron;
	}

	public static String getSquadron(ItemStack itemstack) {
		if (itemstack.getItem() instanceof SquadronItem && itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("TESSquadron")) {
			return itemstack.getTagCompound().getString("TESSquadron");
		}
		return null;
	}

	public static void setSquadron(ItemStack itemstack, String squadron) {
		if (itemstack.getItem() instanceof SquadronItem) {
			if (itemstack.getTagCompound() == null) {
				itemstack.setTagCompound(new NBTTagCompound());
			}
			itemstack.getTagCompound().setString("TESSquadron", squadron);
		}
	}

	public interface SquadronItem {
	}
}