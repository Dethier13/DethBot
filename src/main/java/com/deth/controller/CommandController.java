package com.deth.controller;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static com.deth.util.FinalUtilChannels.*;

public class CommandController extends ListenerAdapter{

	
	RaidController raidController = new RaidController();
	RaidRosterController rosterController = new RaidRosterController();
	HelpController helpController = new HelpController();
	
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		long responseNumber = event.getResponseNumber();
		
		//get event info. probably not needed in this method
		User user = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		String msg = message.getContentDisplay();
		try {
			if (msg.startsWith("!open")) {
				if(channel.getId().equals(O_CHAN_ID)) {
					raidController.openRaid(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			} else if (msg.startsWith("!close")) {
				if(channel.getId().equals(O_CHAN_ID)) {
					raidController.closeRaid(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			} else if (msg.startsWith("!su")) {
				if(channel.getId().equals(SIGNUP_CHAN_ID)) {
					channel.sendMessage("Processing...").queue(); 
					rosterController.signup(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			} else if (msg.startsWith("!withdraw")) {
				if(channel.getId().equals(SIGNUP_CHAN_ID)) {
					channel.sendMessage("Processing...").queue(); 
					rosterController.withdraw(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			
			} else if (msg.startsWith("!default")) {
				if(channel.getId().equals(SIGNUP_CHAN_ID)) {
					channel.sendMessage("Working on it, try again later...").queue(); 
					rosterController.setDefault(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			
			} else if (msg.startsWith("!rc")) {
				if(channel.getId().equals(O_CHAN_ID)) {
					raidController.rollcall(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			}  else if (msg.startsWith("!status")) {
				if(channel.getId().equals(SIGNUP_CHAN_ID)) {
					channel.sendMessage("Processing...").queue(); 
					rosterController.status(event);
				} else {
					channel.sendMessage("" + channel.getName() + " is not the right channel for this command.").queue();
				}
			} else if (msg.startsWith("!raidlist")) {
				raidController.raidList(event);
			} else if (msg.startsWith("!raidhelp")) {
				helpController.raidHelp(event);
			}
		} catch (Exception e) {
			event.getGuild().getTextChannelById(BOT_CHAN_ID).sendMessage("exception occurred: " + e).queue();
			event.getGuild().getTextChannelById(BOT_CHAN_ID).sendMessage("trace: " + e.getStackTrace()).queue();
		}
		
		
	}
}
