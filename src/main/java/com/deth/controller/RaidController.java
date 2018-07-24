package com.deth.controller;

import java.io.IOException;

import com.deth.service.RaidService;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.deth.util.FinalUtilProperties.*;

public class RaidController extends ControllerCommandsInfo{
	private RaidService raidService = RaidService.getInstance();
	
	public void openRaid(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String message = raidService.openRaid(msg);
			if (message == null) {
				channel.sendMessage("Double check that command dumbass, somethings wrong with it.").queue();
			}
			guild.getTextChannelById(SIGNUP_CHAN_ID).sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("file failed to create " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("file failed to create " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(O_CHAN_ID).sendMessage("Error making raid. " ).queue();
		}
	}
	
	public void updateRaid(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			message = raidService.updateRaid(msg);
			if (message == null) {
				channel.sendMessage("Double check that command dumbass, somethings wrong with it.").queue();
			}
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("file failed to update " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("file failed to update " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(O_CHAN_ID).sendMessage("Error updating raid. " ).queue();
		}
		channel.sendMessage(message).queue();
	}
	
	public void closeRaid(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			message = raidService.closeRaid(msg);
			if (message == null) {
				channel.sendMessage("Double check that command dumbass, somethings wrong with it.").queue();
			}
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("file failed to close " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("file failed to close " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(O_CHAN_ID).sendMessage("Error closing raid. " ).queue();
		}
		channel.sendMessage(message).queue();
	}
	
	public void rollcall(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String message = raidService.rollCall(msg, guild);
			if(message == null) {
				channel.sendMessage("Double check that command dumbass, somethings wrong with it.").queue();
			}
			guild.getTextChannelById(SIGNUP_CHAN_ID).sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("rollcall failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("rollcall failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(O_CHAN_ID).sendMessage("Error occurred. " ).queue();
		}
	}
	
	public void raidList(MessageReceivedEvent event) {
		getBasicInfo(event);

		String message = "";
		try {
			message = raidService.raidList();
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("rollcall failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("rollcall failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(O_CHAN_ID).sendMessage("Error occurred. " ).queue();
		}
	}
}
