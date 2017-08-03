package com.fireboss.heartlevels.commands;

import java.util.ArrayList;
import java.util.List;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.PlayerStats;
import com.fireboss.heartlevels.handlers.PlayerHandler;
import com.fireboss.heartlevels.handlers.PlayerHandlerHelper;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class HeartLevelCommands implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return "heartlevels";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/hl <help/reset>";
	}

	@Override
	public List<String> getCommandAliases() {
		List<String> commandList = new ArrayList<String>();
		commandList.add("hl");
		return commandList;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
			EntityPlayer taggedPlayer = null;
			boolean isConsole = (player == null);
			if (args[0].equalsIgnoreCase("help")) {
				sender.addChatMessage(new ChatComponentText("§2--- Showing help page 1 of 1 (/hl help <page>) ---"));
				sender.addChatMessage(new ChatComponentText(
						"/hl reset : Resets the user's levels (EXP AND HEARTS) completely."));
			} else if (args[0].equalsIgnoreCase("reset")) {

				// Check if RPG mode is off
				if (!Config.rpgMode.getBoolean()) {
					sender.addChatMessage(new ChatComponentText(
							"§cRPG mode is currently off in the config, so you cannot use this command"));
					return;
				}

				// Permission Checks & Console Checks
				boolean hasPermission = false;
				if (isConsole) {
					sender.addChatMessage(new ChatComponentText(
							"§cYou cannot run this command in console."));
					return;
				}
				if (args.length > 1) {
					taggedPlayer = sender.getEntityWorld().getPlayerEntityByName(args[1]);
				} else {
					taggedPlayer = player;
				}
				if (taggedPlayer == null) {
					sender.addChatMessage(new ChatComponentText("§cThat player cannot be found"));
					return;
				}
				boolean isThePlayer = false;
				if (player.getUUID(player.getGameProfile()) == taggedPlayer.getUUID(player.getGameProfile())) {
					hasPermission = true;
					isThePlayer = true;
				}
				if (!hasPermission) {
					sender.addChatMessage(new ChatComponentText("§cYou do not have permission to use this command"));
					return;
				}

				// Set the players health
				if (taggedPlayer.getHealth() != taggedPlayer.getMaxHealth()) {
					if (isThePlayer) {
						sender.addChatMessage(
								new ChatComponentText("§cYou cannot use this command unless you are fully healed"));
					} else {
						sender.addChatMessage(new ChatComponentText(
								"§cYou cannot use this command unless the user you are resetting is fully healed"));
					}
					return;
				}
				PlayerStats stats = PlayerStats
						.getPlayerStats(taggedPlayer.getUUID(taggedPlayer.getGameProfile()).toString());
				taggedPlayer.removeExperienceLevel(Integer.MAX_VALUE);
				double newMax = PlayerHandlerHelper.calculateTotalHeartLevelsContribNoHeartContainers(taggedPlayer,
						stats);
				double updatedModifier = newMax - 20;
				PlayerHandler.addHealthModifier(taggedPlayer, updatedModifier);
				stats.healthmod = taggedPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth)
						.getModifier(PlayerHandler.HeartLevelsID).getAmount();
				stats.count = 0;
				stats.heartContainers = 0;
				taggedPlayer.setHealth(taggedPlayer.getMaxHealth());
				taggedPlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 40));
				if (isThePlayer) {
					sender.addChatMessage(
							new ChatComponentText("§aAny hearts gained from enchantments will not be reset."));
					sender.addChatMessage(
							new ChatComponentText("You feel slightly uneasy while everything gets stripped down..."));
				} else {
					sender.addChatMessage(new ChatComponentText("Alright, I've reset that user's health."));
					taggedPlayer.addChatMessage(
							new ChatComponentText("§aAny hearts gained from enchantments will not be reset."));
					taggedPlayer.addChatMessage(
							new ChatComponentText("You feel slightly uneasy while everything gets stripped down..."));
				}
			} else {
				sender.addChatMessage(new ChatComponentText("§cUsage: " + this.getCommandUsage(sender)));
			}
		} else {
			sender.addChatMessage(new ChatComponentText("§cUsage: " + this.getCommandUsage(sender)));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
