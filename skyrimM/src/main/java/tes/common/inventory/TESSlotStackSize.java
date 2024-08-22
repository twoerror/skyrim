package tes.common.inventory;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class TESSlotStackSize implements Comparable<Object> {
	private final int stackSize;
	private final int slot;

	public TESSlotStackSize(int i, int j) {
		slot = i;
		stackSize = j;
	}

	@Override
	public int compareTo(Object obj) {
		if (obj instanceof TESSlotStackSize) {
			TESSlotStackSize obj1 = (TESSlotStackSize) obj;
			if (obj1.stackSize < stackSize) {
				return 1;
			}
			if (obj1.stackSize > stackSize) {
				return -1;
			}
			if (obj1.slot < slot) {
				return 1;
			}
			if (obj1.slot > slot) {
				return -1;
			}
		}
		return 0;
	}

	public int getSlot() {
		return slot;
	}
}