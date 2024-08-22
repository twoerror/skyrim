package tes.common.faction;

import java.util.*;

public class TESAlignmentBonusMap implements Map<TESFaction, Float> {
	private final Map<TESFaction, Float> bonusMap = new EnumMap<>(TESFaction.class);

	public Set<TESFaction> getChangedFactions() {
		EnumSet<TESFaction> changed = EnumSet.noneOf(TESFaction.class);
		for (Map.Entry<TESFaction, Float> fac : entrySet()) {
			float bonus = fac.getValue();
			if (bonus == 0.0f) {
				continue;
			}
			changed.add(fac.getKey());
		}
		return changed;
	}

	@Override
	public int size() {
		return bonusMap.size();
	}

	@Override
	public boolean isEmpty() {
		return bonusMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return bonusMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return bonusMap.containsValue(value);
	}

	@Override
	public Float get(Object key) {
		return bonusMap.get(key);
	}

	@Override
	public Float put(TESFaction key, Float value) {
		return bonusMap.put(key, value);
	}

	@Override
	public Float remove(Object key) {
		return bonusMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends TESFaction, ? extends Float> m) {
		bonusMap.putAll(m);
	}

	@Override
	public void clear() {
		bonusMap.clear();
	}

	@Override
	public Set<TESFaction> keySet() {
		return bonusMap.keySet();
	}

	@Override
	public Collection<Float> values() {
		return bonusMap.values();
	}

	@Override
	public Set<Entry<TESFaction, Float>> entrySet() {
		return bonusMap.entrySet();
	}
}