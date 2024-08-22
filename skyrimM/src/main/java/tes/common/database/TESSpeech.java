package tes.common.database;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.TES;
import tes.common.TESConfig;
import tes.common.TESDrunkenSpeech;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketNPCSpeech;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TESSpeech {
	private static final Map<String, SpeechBank> ALL_SPEECH_BANKS = new HashMap<>();
	private static final Random RANDOM = new Random();

	private TESSpeech() {
	}

	public static String formatSpeech(String speech, ICommandSender entityplayer, CharSequence location, CharSequence objective) {
		String s = speech;
		if (entityplayer != null) {
			s = s.replace("#", entityplayer.getCommandSenderName());
		}
		if (location != null) {
			s = s.replace("@", location);
		}
		if (objective != null) {
			return s.replace("$", objective);
		}
		return s;
	}

	public static String getRandomSpeech(String bankName) {
		return getSpeechBank(bankName).getRandomSpeech();
	}

	public static String getRandomSpeechForPlayer(TESEntityNPC entity, String speechBankName, ICommandSender entityplayer) {
		return getRandomSpeechForPlayer(entity, speechBankName, entityplayer, null, null);
	}

	public static String getRandomSpeechForPlayer(TESEntityNPC entity, String speechBankName, ICommandSender entityplayer, CharSequence location, CharSequence objective) {
		String s = getRandomSpeech(speechBankName);
		s = formatSpeech(s, entityplayer, location, objective);
		if (entity.isDrunkard()) {
			float f = entity.getDrunkenSpeechFactor();
			s = TESDrunkenSpeech.getDrunkenSpeech(s, f);
		}
		return s;
	}

	public static String getSpeechAtLine(String bankName, int i) {
		return getSpeechBank(bankName).getSpeechAtLine(i);
	}

	private static SpeechBank getSpeechBank(String name) {
		SpeechBank bank = ALL_SPEECH_BANKS.get(name);
		if (bank != null) {
			return bank;
		}
		return new SpeechBank("dummy_" + name, true, Collections.singletonList("Speech bank " + name + " could not be found!"));
	}

	public static void onInit() {
		Map<String, BufferedReader> speechBankNamesAndReaders = new HashMap<>();
		ZipFile zip = null;
		try {
			ModContainer mc = TES.getModContainer();
			if (mc.getSource().isFile()) {
				zip = new ZipFile(mc.getSource());
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					String s = entry.getName();
					String path = "assets/tes/texts/" + TESConfig.languageCode + "/speech/";
					if (!s.startsWith("assets/tes/texts/" + TESConfig.languageCode + "/speech/") || !s.endsWith(".txt")) {
						continue;
					}
					s = s.substring(path.length());
					int i = s.indexOf(".txt");
					try {
						s = s.substring(0, i);
						BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(zip.getInputStream(entry)), StandardCharsets.UTF_8));
						speechBankNamesAndReaders.put(s, reader);
					} catch (Exception e) {
						FMLLog.severe("Failed to onInit TES speech bank " + s + "from zip file");
						e.printStackTrace();
					}
				}
			} else {
				File speechBankDir = new File(TES.class.getResource("/assets/tes/texts/" + TESConfig.languageCode + "/speech/").toURI());
				Collection<File> subfiles = FileUtils.listFiles(speechBankDir, null, true);
				for (File subfile : subfiles) {
					String s = subfile.getPath();
					s = s.substring(speechBankDir.getPath().length() + 1);
					int i = (s = s.replace(File.separator, "/")).indexOf(".txt");
					if (i < 0) {
						FMLLog.severe("Failed to onInit TES speech bank " + s + " from MCP folder; speech bank files must be in .txt format");
						continue;
					}
					try {
						s = s.substring(0, i);
						BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(Files.newInputStream(subfile.toPath())), StandardCharsets.UTF_8));
						speechBankNamesAndReaders.put(s, reader);
					} catch (Exception e) {
						FMLLog.severe("Failed to onInit TES speech bank " + s + " from MCP folder");
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			FMLLog.severe("Failed to onInit TES speech banks");
			e.printStackTrace();
		}
		for (Map.Entry<String, BufferedReader> speechBankName : speechBankNamesAndReaders.entrySet()) {
			BufferedReader reader = speechBankName.getValue();
			try {
				String line;
				ArrayList<String> speeches = new ArrayList<>();
				ArrayList<String> allLines = new ArrayList<>();
				boolean random = true;
				while ((line = reader.readLine()) != null) {
					if ("!RANDOM".equals(line)) {
						random = false;
					} else {
						speeches.add(line);
					}
					allLines.add(line);
				}
				reader.close();
				if (speeches.isEmpty()) {
					FMLLog.severe("TES speech bank " + speechBankName.getKey() + " is empty!");
					continue;
				}
				SpeechBank bank;
				if (random) {
					bank = new SpeechBank(speechBankName.getKey(), true, speeches);
				} else {
					bank = new SpeechBank(speechBankName.getKey(), false, allLines);
				}
				ALL_SPEECH_BANKS.put(speechBankName.getKey(), bank);
			} catch (Exception e) {
				FMLLog.severe("Failed to onInit TES speech bank " + speechBankName.getKey());
				e.printStackTrace();
			}
		}
		if (zip != null) {
			try {
				zip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendSpeech(EntityPlayer entityplayer, TESEntityNPC entity, String speech) {
		sendSpeech(entityplayer, entity, speech, false);
	}

	private static void sendSpeech(EntityPlayer entityplayer, TESEntityNPC entity, String speech, boolean forceChatMsg) {
		IMessage packet = new TESPacketNPCSpeech(entity.getEntityId(), speech, forceChatMsg);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
	}

	public static void sendSpeechAndChatMessage(EntityPlayer entityplayer, TESEntityNPC entity, String speechBankName) {
		String name = entity.getCommandSenderName();
		String speech = getRandomSpeechForPlayer(entity, speechBankName, entityplayer, null, null);
		String message = EnumChatFormatting.YELLOW + "<" + name + '>' + EnumChatFormatting.WHITE + ' ' + speech;
		IChatComponent component = new ChatComponentText(message);
		entityplayer.addChatMessage(component);
		sendSpeech(entityplayer, entity, speech);
	}

	private static class SpeechBank {
		private final List<String> speeches;
		private final String name;
		private final boolean isRandom;

		private SpeechBank(String s, boolean r, List<String> spc) {
			name = s;
			isRandom = r;
			speeches = spc;
		}

		private static String internalFormatSpeech(String s) {
			return s;
		}

		private String getRandomSpeech() {
			if (!isRandom) {
				return "ERROR: Tried to retrieve random speech from non-random speech bank " + name;
			}
			String s = speeches.get(RANDOM.nextInt(speeches.size()));
			return internalFormatSpeech(s);
		}

		private String getSpeechAtLine(int line) {
			if (isRandom) {
				return "ERROR: Tried to retrieve indexed speech from random speech bank " + name;
			}
			int index = line - 1;
			if (index >= 0 && index < speeches.size()) {
				String s = speeches.get(index);
				return internalFormatSpeech(s);
			}
			return "ERROR: Speech line " + line + " is out of range!";
		}
	}
}