package com.deth.controller;

import static com.deth.util.FinalUtilProperties.BOT_CHAN_ID;
import static com.deth.util.FinalUtilProperties.LEAD_CHAN_ID;
import static com.deth.util.FinalUtilProperties.O_CHAN_ID;

import java.io.IOException;
import java.util.List;

import com.deth.service.AttendanceService;

import net.dv8tion.jda.core.entities.Member;
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
			message = attendanceService.showFullAttendance(guild);
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe.fillInStackTrace()).queue();
		}
		
	}
	
	public void raidBoard(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			message = attendanceService.raidBoard(member, message, guild);
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to get raidboard " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to get raidboard " + ioe.fillInStackTrace()).queue();
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
			message = attendanceService.commitAttendance(guild);
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to commit attendance " + ioe.fillInStackTrace()).queue();
		}
	}
	
	public void getKickList(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		String message2 = "";
		try {
			message = attendanceService.kickList(guild);
			if(message.length() > 1999) {
				message2 = message.substring(1999);
				message = message.substring(0, 1999);
			}
			channel.sendMessage(message).queue();
			if (!message2.equals("")) {
				channel.sendMessage(message2).queue();
			}
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to gen kick list " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to gen kick list " + ioe.fillInStackTrace()).queue();
		}
		
	}
	
	public void raidCount(MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		try {
			if(channel.equals(guild.getTextChannelById(O_CHAN_ID))){
				String[] split = msg.split(" ");
				String name = split[1];
				
				for (int i = 2; i < split.length; i++){
					name += " " + split[i];
				}
				System.out.println("searching for user by name: ." + name + ".");
				try {
					
					List <Member> member = guild.getMembersByEffectiveName(name, true);
					if (member.size() == 0) {
						member = guild.getMembersByNickname(name, true);
					}
					message = attendanceService.raidCount(member.get(0), message, 1, guild);
					
				} catch (IndexOutOfBoundsException IOOBE) {
					guild.getTextChannelById(LEAD_CHAN_ID).sendMessage("Failed to find someone with that display name.").queue();
					return;
				}
			} else {
				message = attendanceService.raidCount(member, message, 0, guild);
			}
			channel.sendMessage(message).queue();
		} catch (IOException ioe) {
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to get members raidcount " + ioe).queue();
			guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to get members raidcount " + ioe.fillInStackTrace()).queue();
		} 
	}
	
	public void exemptList (MessageReceivedEvent event) {
		getBasicInfo(event);
		String message = "";
		
		//try {
			message = attendanceService.exemptList(guild);
			
			channel.sendMessage(message).queue();
		//}  /*catch (IOException ioe) {
			//guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to gen exempt list " + ioe).queue();
			//guild.getTextChannelById(BOT_CHAN_ID).sendMessage("failed to gen exempt list " + ioe.fillInStackTrace()).queue();
		//}*/
		
	}
}
