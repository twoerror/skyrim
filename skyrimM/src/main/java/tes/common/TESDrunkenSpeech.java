package tes.common;

import java.util.Random;

public class TESDrunkenSpeech {
	private static final Random RAND = new Random();

	private TESDrunkenSpeech() {
	}

	public static String getDrunkenSpeech(String speech, float chance) {
		StringBuilder newSpeech = new StringBuilder();
		for (int i = 0; i < speech.length(); ++i) {
			String s = speech.substring(i, i + 1);
			if (RAND.nextFloat() < chance) {
				s = "";
			} else if (RAND.nextFloat() < chance * 0.4f) {
				s = " *hic* ";
			}
			newSpeech.append(s);
		}
		return newSpeech.toString();
	}
}