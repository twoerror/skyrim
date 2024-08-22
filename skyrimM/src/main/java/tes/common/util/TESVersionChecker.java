package tes.common.util;

import tes.TES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TESVersionChecker {
	private static final String VERSION_URL = "https://raw.githubusercontent.com/Hummel009/Game-of-Thrones/master/version.txt";

	private static boolean checkedUpdate;

	private TESVersionChecker() {
	}

	public static void checkForUpdates() {
		if (!checkedUpdate) {
			Runnable checkRunnable = () -> {
				try {
					String line;
					URL url = new URL(VERSION_URL);
					BufferedReader updateReader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
					StringBuilder updateVersion = new StringBuilder();
					while ((line = updateReader.readLine()) != null) {
						updateVersion.append(line);
					}
					updateReader.close();
					updateVersion = new StringBuilder(updateVersion.toString().trim());
					String currentVersion = TES.VERSION;
					if (!updateVersion.toString().equals(currentVersion)) {
						ChatComponentText component = new ChatComponentText("Game of Thrones Mod:");
						component.getChatStyle().setColor(EnumChatFormatting.YELLOW);
						EntityClientPlayerMP entityplayer = Minecraft.getMinecraft().thePlayer;
						if (entityplayer != null) {
							entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.update", component, updateVersion.toString()));
						}
					}
				} catch (Exception e) {
					TESLog.getLogger().warn("Hummel009: Version check failed");
					e.printStackTrace();
				}
			};

			Thread checkThread = new Thread(checkRunnable, "TES Update Checker");

			checkedUpdate = true;
			checkThread.setDaemon(true);
			checkThread.start();
		}
	}

	public static void setCheckedUpdate(boolean checkedUpdate) {
		TESVersionChecker.checkedUpdate = checkedUpdate;
	}
}