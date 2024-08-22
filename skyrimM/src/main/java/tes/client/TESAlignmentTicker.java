package tes.client;

import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumMap;
import java.util.Map;

public class TESAlignmentTicker {
	private static final Map<TESFaction, TESAlignmentTicker> ALL_FACTION_TICKERS = new EnumMap<>(TESFaction.class);

	private final TESFaction theFac;

	private float oldAlign;
	private float newAlign;

	private int moveTick;
	private int prevMoveTick;
	private int flashTick;
	private int numericalTick;

	private TESAlignmentTicker(TESFaction f) {
		theFac = f;
	}

	public static TESAlignmentTicker forFaction(TESFaction fac) {
		if (!ALL_FACTION_TICKERS.containsKey(fac)) {
			ALL_FACTION_TICKERS.put(fac, new TESAlignmentTicker(fac));
		}
		return ALL_FACTION_TICKERS.get(fac);
	}

	public static void updateAll(EntityPlayer entityplayer, boolean forceInstant) {
		for (TESDimension dim : TESDimension.values()) {
			for (TESFaction fac : dim.getFactionList()) {
				forFaction(fac).update(entityplayer, forceInstant);
			}
		}
	}

	public float getInterpolatedAlignment(float f) {
		if (moveTick == 0) {
			return oldAlign;
		}
		float tickF = prevMoveTick + (moveTick - prevMoveTick) * f;
		tickF /= 20.0f;
		tickF = 1.0f - tickF;
		return oldAlign + (newAlign - oldAlign) * tickF;
	}

	private void update(EntityPlayer entityplayer, boolean forceInstant) {
		float curAlign = TESLevelData.getData(entityplayer).getAlignment(theFac);
		if (forceInstant) {
			oldAlign = newAlign = curAlign;
			moveTick = 0;
			prevMoveTick = 0;
			flashTick = 0;
			numericalTick = 0;
		} else {
			if (newAlign != curAlign) {
				oldAlign = newAlign;
				newAlign = curAlign;
				moveTick = 20;
				flashTick = 30;
				numericalTick = 200;
			}
			prevMoveTick = moveTick;
			if (moveTick > 0) {
				--moveTick;
				if (moveTick <= 0) {
					oldAlign = newAlign;
				}
			}
			if (flashTick > 0) {
				--flashTick;
			}
			if (numericalTick > 0) {
				--numericalTick;
			}
		}
	}

	public int getFlashTick() {
		return flashTick;
	}

	public int getNumericalTick() {
		return numericalTick;
	}
}