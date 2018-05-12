package com.deth.controller;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.deth.util.FinalUtilProperties.*;

import java.io.IOException;

import com.deth.service.MessageService;

public class InfoController extends ControllerCommandsInfo{
	private MessageService msgService = MessageService.getInstance();
	
	public void raidHelp(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			if(channel.getId().equals(O_CHAN_ID)) {
				officerHelp();
			} else {
				signupHelp();
			}
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe.getStackTrace()).queue();
			channel.sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	//TODO: probably make into text doc for easier readability when updating?
	private void signupHelp() throws IOException{
		String help = msgService.gRaidHelp();
	
		channel.sendMessage(help).queue();
	}
	
	private void officerHelp() throws IOException{
		String help = msgService.oRaidHelp();
		channel.sendMessage(help).queue();
	}
	
	public void attendanceHelp(MessageReceivedEvent event) {
		getBasicInfo(event);
		try {
			String help = msgService.attendanceHelp();
			channel.sendMessage(help).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe.getStackTrace()).queue();
			channel.sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	
	public void rules(MessageReceivedEvent event) {
		getBasicInfo(event);
		String returnMessage = "";
		try {
			returnMessage = msgService.rules();
			channel.sendMessage(returnMessage).queue();
			returnMessage = msgService.rules2();
			channel.sendMessage(returnMessage).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe.getStackTrace()).queue();
			channel.sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	
	public void raidSchedule(MessageReceivedEvent event) {
		getBasicInfo(event);
		String returnMessage = "Current Guild raid schedule:\n";
		try { 
			returnMessage = msgService.schedule();
			channel.sendMessage(returnMessage).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("signup failed " + ioe.getStackTrace()).queue();
			channel.sendMessage("Error occurred. Please try again later." ).queue();
		}
	}
	
	public void welcomeMsg(GuildMemberJoinEvent event) {
		User user = event.getUser();
		final String rules1;
		final String rules2;
		try {
			rules1 = msgService.rules();
			user.openPrivateChannel().queue((channel) ->
			{
				channel.sendMessage(rules1).queue();
			});
			rules2 = msgService.rules2();
			user.openPrivateChannel().queue((channel) ->
			{
				channel.sendMessage(rules2).queue();
			});
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("join message failed " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("join message failed " + ioe.getStackTrace()).queue();
		}
	}
	
	public void leaveMsg(GuildMemberLeaveEvent event) {
		User user = event.getUser();
		Guild guild = event.getGuild();
		guild.getTextChannelById(O_CHAN_ID).sendMessage("The following person just left the server: " + user.getName() + ". BYE FELICIA").queue();
	}
	
}
