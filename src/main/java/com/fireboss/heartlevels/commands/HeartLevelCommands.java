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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

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
				sender.addChatMessage(new ChatComponentTranslation("help.page1.line1").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
				sender.addChatMessage(new ChatComponentTranslation("help.page1.line2"));
			} else if (args[0].equalsIgnoreCase("reset")) {

				// Check if RPG mode is off
				if (!Config.rpgMode.getBoolean()) {
					sender.addChatMessage(new ChatComponentTranslation("text.rpgoff").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}

				// Permission Checks & Console Checks
				boolean hasPermission = false;
				if (isConsole) {
					sender.addChatMessage(new ChatComponentTranslation("text.noconsole").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}
				if (args.length > 1) {
					taggedPlayer = sender.getEntityWorld().getPlayerEntityByName(args[1]);
				} else {
					taggedPlayer = player;
				}
				if (taggedPlayer == null) {
					sender.addChatMessage(new ChatComponentTranslation("text.noplayer").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}
				boolean isThePlayer = false;
				if (player.getUUID(player.getGameProfile()) == taggedPlayer.getUUID(player.getGameProfile())) {
					hasPermission = true;
					isThePlayer = true;
				}
				if (!hasPermission) {
					sender.addChatMessage(new ChatComponentTranslation("text.noperms").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}

				// Set the players health
				if (taggedPlayer.getHealth() != taggedPlayer.getMaxHealth()) {
					if (isThePlayer) {
						sender.addChatMessage(new ChatComponentTranslation("text.notfull").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					} else {
						sender.addChatMessage(new ChatComponentTranslation("text.notfull2").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					}
					return;
				}
				PlayerStats stats = PlayerStats
						.getPlayerStats(taggedPlayer.getUUID(taggedPlayer.getGameProfile()).toString());
				taggedPlayer.removeExperienceLevel(Integer.MAX_VALUE);
				double newMax = PlayerHandlerHelper.calcDefaultHeartsNoHC(taggedPlayer,
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
					sender.addChatMessage(new ChatComponentTranslation("text.enchnotreset").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
					sender.addChatMessage(new ChatComponentTranslation("text.uneasy"));
				} else {
					sender.addChatMessage(new ChatComponentTranslation("text.resetuser"));
					taggedPlayer.addChatMessage(new ChatComponentTranslation("text.enchnotreset").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
					taggedPlayer.addChatMessage(new ChatComponentTranslation("text.uneasy"));
				}
			} else {
				sender.addChatMessage(new ChatComponentTranslation("text.usage", this.getCommandUsage(sender)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			}
		} else {
			sender.addChatMessage(new ChatComponentTranslation("text.usage", this.getCommandUsage(sender)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
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
