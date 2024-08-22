package tes.common;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.event.FMLInterModComms;
import tes.TES;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class TESInterModComms {
	private TESInterModComms() {
	}

	public static void update() {
		ImmutableList<FMLInterModComms.IMCMessage> messages = FMLInterModComms.fetchRuntimeMessages(TES.instance);
		if (!messages.isEmpty()) {
			for (FMLInterModComms.IMCMessage message : messages) {
				if ("SIEGE_ACTIVE".equals(message.key)) {
					String playerName = message.getStringValue();
					EntityPlayerMP entityplayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName);
					if (entityplayer != null) {
						int duration = 20;
						TESLevelData.getData(entityplayer).setSiegeActive(duration);
					}
				}
			}
		}
	}
}