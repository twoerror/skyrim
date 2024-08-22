package tes.common.util;

import tes.client.model.TESModelDragonAnimaton;

import java.util.Arrays;

public class TESCircularBuffer {
	private final double[] buffer;
	private int index;

	public TESCircularBuffer(int size) {
		buffer = new double[size];
	}

	public void fill(double value) {
		Arrays.fill(buffer, value);
	}

	private double get(float x, int offset) {
		int i = index - offset;
		int len = buffer.length - 1;
		return TESModelDragonAnimaton.lerp(buffer[i - 1 & len], buffer[i & len], x);
	}

	public double get(float x, int offset1, int offset2) {
		return get(x, offset2) - get(x, offset1);
	}

	public void update(double value) {
		index++;
		index %= buffer.length;
		buffer[index] = value;
	}
}