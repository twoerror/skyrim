package tes.common.util;

import tes.client.model.TESModelDragonAnimaton;

public class TESTickFloat {
	private boolean clamp;
	private float min;
	private float max;
	private float current;
	private float previous;

	public TESTickFloat() {
		current = previous = 0;
	}

	public TESTickFloat(float value) {
		current = previous = value;
	}

	public void add(float value) {
		sync();
		current += value;
		if (clamp) {
			current = TESModelDragonAnimaton.clamp(current, min, max);
		}
	}

	public float get() {
		return current;
	}

	public float get(float x) {
		return TESModelDragonAnimaton.lerp(previous, current, x);
	}

	public void set(float value) {
		sync();
		if (clamp) {
			current = TESModelDragonAnimaton.clamp(value, min, max);
		} else {
			current = value;
		}
	}

	public TESTickFloat setLimit(float min, float max) {
		clamp = true;
		this.min = min;
		this.max = max;
		set(current);
		return this;
	}

	public void sync() {
		previous = current;
	}
}