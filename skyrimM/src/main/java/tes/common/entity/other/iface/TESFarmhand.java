package tes.common.entity.other.iface;

import net.minecraft.item.Item;
import net.minecraftforge.common.IPlantable;

public interface TESFarmhand {
	IPlantable getSeedsItem();

	void setSeedsItem(Item seed);
}