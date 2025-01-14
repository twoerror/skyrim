package tes.common.item.other;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.List;

public class TESItemOwnership {
	private TESItemOwnership() {
	}

	public static void addPreviousOwner(ItemStack itemstack, String name) {
		if (!itemstack.hasTagCompound()) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		List<String> previousOwners = getPreviousOwners(itemstack);
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt.hasKey("TESOwner", 8)) {
			nbt.removeTag("TESOwner");
		}
		List<String> lastPreviousOwners = previousOwners;
		previousOwners = new ArrayList<>();
		previousOwners.add(name);
		previousOwners.addAll(lastPreviousOwners);
		while (previousOwners.size() > 3) {
			previousOwners.remove(previousOwners.size() - 1);
		}
		NBTTagList tagList = new NBTTagList();
		for (String owner : previousOwners) {
			tagList.appendTag(new NBTTagString(owner));
		}
		nbt.setTag("TESPrevOwnerList", tagList);
	}

	public static String getCurrentOwner(ItemStack itemstack) {
		NBTTagCompound nbt;
		if (itemstack.getTagCompound() != null && (nbt = itemstack.getTagCompound()).hasKey("TESCurrentOwner", 8)) {
			return nbt.getString("TESCurrentOwner");
		}
		return null;
	}

	public static List<String> getPreviousOwners(ItemStack itemstack) {
		List<String> owners = new ArrayList<>();
		if (itemstack.getTagCompound() != null) {
			NBTTagCompound nbt = itemstack.getTagCompound();
			if (nbt.hasKey("TESOwner", 8)) {
				String outdatedOwner = nbt.getString("TESOwner");
				owners.add(outdatedOwner);
			}
			if (nbt.hasKey("TESPrevOwnerList", 9)) {
				NBTTagList tagList = nbt.getTagList("TESPrevOwnerList", 8);
				for (int i = 0; i < tagList.tagCount(); ++i) {
					String owner = tagList.getStringTagAt(i);
					owners.add(owner);
				}
			}
		}
		return owners;
	}

	public static void setCurrentOwner(ItemStack itemstack, String name) {
		if (!itemstack.hasTagCompound()) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		String previousCurrentOwner = getCurrentOwner(itemstack);
		if (previousCurrentOwner != null) {
			addPreviousOwner(itemstack, previousCurrentOwner);
		}
		NBTTagCompound nbt = itemstack.getTagCompound();
		nbt.setString("TESCurrentOwner", name);
	}
}