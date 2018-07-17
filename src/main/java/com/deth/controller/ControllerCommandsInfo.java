package com.deth.controller;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ControllerCommandsInfo {
	JDA jda;
	long responseNumber;
	User user;
	Message message;
	MessageChannel channel;
	String msg;
	Guild guild;
	TextChannel textChannel;
	Member member;
	String name;
	
	public ControllerCommandsInfo() {
		
	}
	
	public void getBasicInfo (MessageReceivedEvent event) {
		jda = event.getJDA();
		responseNumber = event.getResponseNumber();
		
		//get event info. probably not needed in this method
		user = event.getAuthor();
		
		message = event.getMessage();
		channel = event.getChannel();
		msg = message.getContentDisplay().toLowerCase();
		
		guild = event.getGuild();
		textChannel = event.getTextChannel();
		member = event.getMember();
		
		name = member.getEffectiveName();
	}
}
