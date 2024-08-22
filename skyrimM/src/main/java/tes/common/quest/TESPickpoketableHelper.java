package tes.common.quest;

import tes.common.util.TESLog;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class TESPickpoketableHelper {
	private TESPickpoketableHelper() {
	}

	public static String getOwner(ItemStack itemstack) {
		if (itemstack.hasTagCompound()) {
			return itemstack.getTagCompound().getCompoundTag("TESPickpocket").getString("Owner");
		}
		return null;
	}

	public static UUID getWanterID(ItemStack itemstack) {
		if (itemstack.hasTagCompound()) {
			String id = itemstack.getTagCompound().getCompoundTag("TESPickpocket").getString("WanterID");
			try {
				return UUID.fromString(id);
			} catch (IllegalArgumentException e) {
				TESLog.getLogger().warn("Item %s has invalid pickpocketed wanter ID %s", itemstack.getDisplayName(), id);
			}
		}
		return null;
	}

	public static boolean isPickpocketed(ItemStack itemstack) {
		return itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("TESPickpocket");
	}

	public static void setPickpocketData(ItemStack itemstack, String ownerName, String wanterName, UUID wantedID) {
		NBTTagCompound data = new NBTTagCompound();
		data.setString("Owner", ownerName);
		data.setString("Wanter", wanterName);
		data.setString("WanterID", wantedID.toString());
		itemstack.setTagInfo("TESPickpocket", data);
	}
}
