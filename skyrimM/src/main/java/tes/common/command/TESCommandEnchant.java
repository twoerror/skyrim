package tes.common.command;

import tes.common.enchant.TESEnchantment;
import tes.common.enchant.TESEnchantmentHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TESCommandEnchant extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		switch (args.length) {
			case 1:
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
			case 2:
				return getListOfStringsMatchingLastWord(args, "add", "remove", "clear");
			case 3:
				ItemStack itemstack;
				if ("add".equals(args[1])) {
					EntityPlayerMP entityplayer2 = getPlayer(sender, args[0]);
					ItemStack itemstack2 = entityplayer2.getHeldItem();
					if (itemstack2 != null) {
						ArrayList<String> enchNames = new ArrayList<>();
						for (TESEnchantment ench : TESEnchantment.CONTENT) {
							if (TESEnchantmentHelper.hasEnchant(itemstack2, ench) || !ench.canApply(itemstack2, false) || !TESEnchantmentHelper.checkEnchantCompatible(itemstack2, ench)) {
								continue;
							}
							enchNames.add(ench.getEnchantName());
						}
						return getListOfStringsMatchingLastWord(args, enchNames.toArray(new String[0]));
					}
				} else if ("remove".equals(args[1]) && (itemstack = getPlayer(sender, args[0]).getHeldItem()) != null) {
					ArrayList<String> enchNames = new ArrayList<>();
					for (TESEnchantment ench : TESEnchantment.CONTENT) {
						if (!TESEnchantmentHelper.hasEnchant(itemstack, ench)) {
							continue;
						}
						enchNames.add(ench.getEnchantName());
					}
					return getListOfStringsMatchingLastWord(args, enchNames.toArray(new String[0]));
				}
				break;
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "tes_enchant";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.tes_enchant.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 1;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 2) {
			EntityPlayerMP entityplayer = getPlayer(sender, args[0]);
			ItemStack itemstack = entityplayer.getHeldItem();
			if (itemstack == null) {
				throw new WrongUsageException("tes.command.tes_enchant.noItem");
			}
			String option = args[1];
			if ("add".equals(option) && args.length >= 3) {
				String enchName = args[2];
				TESEnchantment ench = TESEnchantment.getEnchantmentByName(enchName);
				if (ench != null) {
					if (!TESEnchantmentHelper.hasEnchant(itemstack, ench) && ench.canApply(itemstack, false) && TESEnchantmentHelper.checkEnchantCompatible(itemstack, ench)) {
						TESEnchantmentHelper.setHasEnchant(itemstack, ench);
						func_152373_a(sender, this, "tes.command.tes_enchant.add", enchName, entityplayer.getCommandSenderName(), itemstack.getDisplayName());
						return;
					}
					throw new WrongUsageException("tes.command.tes_enchant.cannotAdd", enchName, itemstack.getDisplayName());
				}
				throw new WrongUsageException("tes.command.tes_enchant.unknown", enchName);
			}
			if ("remove".equals(option) && args.length >= 3) {
				String enchName = args[2];
				TESEnchantment ench = TESEnchantment.getEnchantmentByName(enchName);
				if (ench != null) {
					if (TESEnchantmentHelper.hasEnchant(itemstack, ench)) {
						TESEnchantmentHelper.removeEnchant(itemstack, ench);
						func_152373_a(sender, this, "tes.command.tes_enchant.remove", enchName, entityplayer.getCommandSenderName(), itemstack.getDisplayName());
						return;
					}
					throw new WrongUsageException("tes.command.tes_enchant.cannotRemove", enchName, itemstack.getDisplayName());
				}
				throw new WrongUsageException("tes.command.tes_enchant.unknown", enchName);
			}
			if ("clear".equals(option)) {
				TESEnchantmentHelper.clearEnchantsAndProgress(itemstack);
				func_152373_a(sender, this, "tes.command.tes_enchant.clear", entityplayer.getCommandSenderName(), itemstack.getDisplayName());
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}