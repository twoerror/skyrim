package tes.common.item.other;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import tes.common.block.other.TESBlockConcrete;
import tes.common.block.other.TESBlockConcretePowder;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class TESItemBlockConcrete extends ItemBlock {
	public TESItemBlockConcrete(Block p_i45328_1_) {
		super(p_i45328_1_);
	}

	@Override
	public String getItemStackDisplayName(ItemStack p_77653_1_) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (field_150939_a instanceof TESBlockConcretePowder) {
				return I18n.format("tile.concrete_powder.name", I18n.format("color." + ((TESBlockConcretePowder) field_150939_a).getColor().getUnlocalizedName()));
			}
			if (field_150939_a instanceof TESBlockConcrete) {
				return I18n.format("tile.concrete.name", I18n.format("color." + ((TESBlockConcrete) field_150939_a).getColor().getUnlocalizedName()));
			}
			return I18n.format(field_150939_a.getUnlocalizedName());
		}
		return "";
	}
}