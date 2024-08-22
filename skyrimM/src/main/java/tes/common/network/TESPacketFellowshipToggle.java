package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketFellowshipToggle extends TESPacketFellowshipDo {
	private ToggleFunction function;

	@SuppressWarnings("unused")
	public TESPacketFellowshipToggle() {
	}

	public TESPacketFellowshipToggle(TESFellowshipClient fs, ToggleFunction f) {
		super(fs);
		function = f;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		super.fromBytes(data);
		function = ToggleFunction.values()[data.readByte()];
	}

	@Override
	public void toBytes(ByteBuf data) {
		super.toBytes(data);
		data.writeByte(function.ordinal());
	}

	public enum ToggleFunction {
		PVP, HIRED_FF, MAP_SHOW

	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipToggle, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipToggle packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESFellowship fellowship = packet.getActiveFellowship();
			if (fellowship != null) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				if (packet.function == ToggleFunction.PVP) {
					boolean current = fellowship.getPreventPVP();
					playerData.setFellowshipPreventPVP(fellowship, !current);
				} else if (packet.function == ToggleFunction.HIRED_FF) {
					boolean current = fellowship.getPreventHiredFriendlyFire();
					playerData.setFellowshipPreventHiredFF(fellowship, !current);
				} else if (packet.function == ToggleFunction.MAP_SHOW) {
					boolean current = fellowship.getShowMapLocations();
					playerData.setFellowshipShowMapLocations(fellowship, !current);
				}
			}
			return null;
		}
	}
}