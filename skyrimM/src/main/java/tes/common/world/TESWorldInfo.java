package tes.common.world;

import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;

public class TESWorldInfo extends DerivedWorldInfo {
	private long tesTotalTime;
	private long tesWorldTime;

	public TESWorldInfo(WorldInfo worldinfo) {
		super(worldinfo);
	}

	@Override
	public long getWorldTime() {
		return tesWorldTime;
	}

	@Override
	public long getWorldTotalTime() {
		return tesTotalTime;
	}

	public void tes_setTotalTime(long time) {
		tesTotalTime = time;
	}

	public void tes_setWorldTime(long time) {
		tesWorldTime = time;
	}
}