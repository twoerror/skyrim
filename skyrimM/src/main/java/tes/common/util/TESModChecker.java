package tes.common.util;

public class TESModChecker {
	private static int hasNEI = -1;
	private static int isCauldron = -1;
	private static int hasShaders = -1;
	private static int hasLOTR = -1;
	private static int hasWeather2 = -1;

	private TESModChecker() {
	}

	public static boolean hasLOTR() {
		if (hasLOTR == -1) {
			try {
				Class.forName("lotr.common.LOTRMod");
				TESLog.getLogger().info("Hummel009: Found LOTR installed");
				hasLOTR = 1;
			} catch (ClassNotFoundException e) {
				hasLOTR = 0;
			}
		}
		return hasLOTR == 1;
	}

	public static boolean hasNEI() {
		if (hasNEI == -1) {
			try {
				Class.forName("codechicken.nei.api.API");
				TESLog.getLogger().info("Hummel009: Found NEI installed");
				hasNEI = 1;
			} catch (ClassNotFoundException e) {
				hasNEI = 0;
			}
		}
		return hasNEI == 1;
	}

	public static boolean hasShaders() {
		if (hasShaders == -1) {
			try {
				Class.forName("shadersmodcore.client.Shaders");
				TESLog.getLogger().info("Hummel009: Found shaders installed");
				hasShaders = 1;
			} catch (ClassNotFoundException e) {
				hasShaders = 0;
			}
		}
		return hasShaders == 1;
	}

	public static boolean hasWeather2() {
		if (hasWeather2 == -1) {
			try {
				Class.forName("weather2.Weather");
				TESLog.getLogger().info("Hummel009: Found Weather2 installed");
				hasWeather2 = 1;
			} catch (ClassNotFoundException e) {
				hasWeather2 = 0;
			}
		}
		return hasWeather2 == 1;
	}

	public static boolean isCauldronServer() {
		if (isCauldron == -1) {
			try {
				Class.forName("kcauldron.KCauldronClassTransformer");
				System.out.println("Hummel009: Found Cauldron installed");
				if (TESLog.getLogger() != null) {
					TESLog.getLogger().info("Hummel009: Found Cauldron installed");
				}
				isCauldron = 1;
				return true;
			} catch (ClassNotFoundException ignored) {
			}
			try {
				Class.forName("thermos.ThermosClassTransformer");
				System.out.println("Hummel009: Found Thermos installed");
				if (TESLog.getLogger() != null) {
					TESLog.getLogger().info("Hummel009: Found Thermos installed");
				}
				isCauldron = 1;
				return true;
			} catch (ClassNotFoundException ignored) {
			}
			isCauldron = 0;
			return false;
		}
		return isCauldron == 1;
	}
}