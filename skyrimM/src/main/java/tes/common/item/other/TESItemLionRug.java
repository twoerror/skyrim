package tes.common.item.other;

import tes.common.entity.other.inanimate.TESEntityLionRug;
import tes.common.entity.other.inanimate.TESEntityRugBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Locale;

public class TESItemLionRug extends TESItemRugBase {
	public TESItemLionRug() {
		super(LionRugType.lionRugNames());
	}

	@Override
	public TESEntityRugBase createRug(World world, ItemStack itemstack) {
		TESEntityLionRug rug = new TESEntityLionRug(world);
		rug.setRugType(LionRugType.forID(itemstack.getItemDamage()));
		return rug;
	}

	public enum LionRugType {
		LION(0), LIONESS(1);

		private final int lionID;

		LionRugType(int i) {
			lionID = i;
		}

		public static LionRugType forID(int ID) {
			for (LionRugType t : values()) {
				if (t.lionID != ID) {
					continue;
				}
				return t;
			}
			return LION;
		}

		private static String[] lionRugNames() {
			String[] names = new String[values().length];
			for (int i = 0; i < names.length; ++i) {
				names[i] = values()[i].textureName();
			}
			return names;
		}

		private String textureName() {
			return name().toLowerCase(Locale.ROOT);
		}

		public int getLionID() {
			return lionID;
		}
	}
}