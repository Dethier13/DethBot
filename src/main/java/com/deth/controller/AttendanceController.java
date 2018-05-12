package com.deth.controller;

import static com.deth.util.FinalUtilProperties.BOT_CHAN_ID;

import java.io.IOException;

import com.deth.service.AttendanceService;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AttendanceController extends ControllerCommandsInfo{
	private AttendanceService attendanceService = AttendanceService.getInstance();
	
	public void raidAttendance(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = attendanceService.showRaidAttendance(msg, guild);
		channel.sendMessage(message).queue();
	}
	
	public void fullAttendance(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			message = attendanceService.showFUllAttendance(guild);
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe.fillInStackTrace()).queue();
		}
		
	}
	
	public void removeAttendance(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = attendanceService.removeRaider(msg, guild);
		channel.sendMessage(message).queue();
	}
	
	public void commitAttendance(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			message = attendanceService.commitAttendance();
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe.fillInStackTrace()).queue();
		}
	}
	
	public void getKickList(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			message = attendanceService.kickList(guild);
			System.out.println("controller msg: " + message);
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to gen kick list " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to gen kick list " + ioe.fillInStackTrace()).queue();
		}
		
	}
}
