package com.deth.controller;

import static com.deth.util.FinalUtilProperties.*;

import java.io.IOException;

import com.deth.model.Raider;
import com.deth.service.RaidService;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RaidRosterController extends ControllerCommandsInfo{
	private RaidService raidService = RaidService.getInstance();
	public void signup(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String message = raidService.signup(member, guild, msg);
			if(message == null) {
				channel.sendMessage("Something went wrong. Try again later please.").queue();
			}
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(SIGNUP_CHAN_ID).sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	
	public void withdraw(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String message = raidService.withdraw(member, msg);
			if(message == null) {
				channel.sendMessage("Something went wrong. Try again later please.").queue();
			}
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("withdraw failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("withdraw failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(SIGNUP_CHAN_ID).sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	
	public void remove(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String message = raidService.remove(guild, msg);
			if(message == null) {
				channel.sendMessage("Something went wrong. Try again later please.").queue();
			}
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("withdraw failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("withdraw failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(LEAD_CHAN_ID).sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	
	public void status(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String message = raidService.status(msg, guild);
			if(message == null) {
				channel.sendMessage("Something went wrong. Try again later please.").queue();
			}
			channel.sendMessage(message).queue();
			
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("Status failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("Status failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(SIGNUP_CHAN_ID).sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	

	public void setDefault(MessageReceivedEvent event) {
		getBasicInfo(event);
		channel.sendMessage("Warning: this feature is a work in progress...").queue();
		try {
			String message = raidService.setDefault(member, msg);
			if(message == null) {
				channel.sendMessage("Something went wrong. Try again later please.").queue();
			}
			channel.sendMessage(message).queue();
		}  catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("withdraw failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("withdraw failed " + ioe.getStackTrace()).queue();
			guild.getTextChannelById(SIGNUP_CHAN_ID).sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
}
