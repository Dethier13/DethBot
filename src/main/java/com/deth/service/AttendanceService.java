package com.deth.service;

import static com.deth.util.FinalUtilProperties.ACTIVITY_REQ;
import static com.deth.util.FinalUtilProperties.CORE;
import static com.deth.util.FinalUtilProperties.GM;
import static com.deth.util.FinalUtilProperties.INITIAL_ACTIVITY_REQ;
import static com.deth.util.FinalUtilProperties.OFFICER;
import static com.deth.util.FinalUtilProperties.SKEEVER;
import static com.deth.util.FinalUtilProperties.ZOMBIE;
import static com.deth.util.FinalUtilProperties.EXEMPT;
import static java.time.temporal.ChronoUnit.DAYS;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.deth.model.Attendance;
import com.deth.model.Raider;
import com.deth.repository.AttendanceRepository;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class AttendanceService {

	private static final AttendanceService instance = new AttendanceService();
	private AttendanceRepository attendanceRepository = AttendanceRepository.getInstance();
	private Attendance attendance = Attendance.getInstance();
	
	private AttendanceService(){}
	
	public static AttendanceService getInstance() {
		return instance;
	}
	
	public String showRaidAttendance(String msg, Guild guild) {
		String message = "Current list of people set to get credit for this raid:\n";
		List<Raider> roster = attendance.getRaiders();
		if(roster.size() == 0) {
			message = "Nobody called rollcall yet, so nobody is set to get credit.";
			return message;
		}
		for(Raider r: roster) {
			message+= ""+guild.getMemberById(r.getId()).getEffectiveName() + " id: " + r.getId() + "\n";
		}
		return message;
	}
	
	public String showFullAttendance(Guild guild) throws IOException{
		String message = "Entire attendance record on file:\n";
		attendanceRepository.readAttendance();
		List<Raider> fullAttendance = attendance.getFullAttendance();
		for(Raider r: fullAttendance) {
			if(guild.getMemberById(r.getId())!= null) {
				message += "" + guild.getMemberById(r.getId()).getEffectiveName() +" "+ r.getRaidDate() + "\n";
			}
		}
		return message;
	}
	
	public String removeRaider(String msg, Guild guild) {
		if(!msg.contains(" ")) {
			return "You forgot to specify who to remove dumbass.";
		}
		msg = msg.substring(msg.indexOf(" ")+1, msg.length());
		
		String[] paramsCheck = msg.split(",");
		Raider raider = null;
		boolean isRemoved = true;
		String message = "You just removed: \n";
		for(String s: paramsCheck) {
			//s = s.substring(1);
			raider = new Raider(s,"");
			isRemoved = attendance.removeRaider(s);
			if(!isRemoved) {
				return "Something went wrong trying to remove id# " + s + " attendance removal has stopped.";
			}
			message += "" + guild.getMemberById(raider.getId()).getEffectiveName()+"\n";
		}
		return message;
	}
	public String commitAttendance(Guild guild) throws IOException{
		String message = "Raid attendance commited.\n";
		attendanceRepository.readAttendance();
		message+=promoteSkeevers(guild);
		attendance.update();
		attendanceRepository.writeAttendance();
		return message;
	}
	
	private String promoteSkeevers(Guild guild) {
		String message = "Promoted the folowing members:\n";
		System.out.println(attendance.getRaiders());
		List<Raider> active = attendance.getRaiders();
		boolean promotion = false;
		Member member;
		for(Raider r : active ) {
			member = guild.getMemberById(r.getId());
			if(member.getRoles().contains(guild.getRoleById(SKEEVER))) {
				guild.getController().removeSingleRoleFromMember(member, guild.getRoleById(SKEEVER)).queue();
				if(!member.getRoles().contains(guild.getRoleById(ZOMBIE))) {
					guild.getController().addSingleRoleToMember(member, guild.getRoleById(ZOMBIE)).queue(); 
					message+=""+ member.getEffectiveName() + "\n";
					promotion = true;
				}
			}
		}
		if(!promotion) {
			return "";
		}
		return message;
	}
	
	public String kickList(Guild guild) throws IOException {
		String message = "The following people are slotted to be kicked due to not raiding yet:\n";
		attendanceRepository.readAttendance();
		List<Raider> attend = attendance.getFullAttendance();
		LocalDate date = LocalDate.now();
		LocalDate tempDate = null;
		long days = 0;
		List<Member> members = guild.getMembers();
		Role skeever = guild.getRoleById(SKEEVER);
		//Role core = guild.getRoleById(CORE);
		//Role officer = guild.getRoleById(OFFICER);
		//Role gm = guild.getRoleById(GM);
		Role[] exemptions = new Role[EXEMPT.length];
		for (int i = 0; i < EXEMPT.length; i++) {
			exemptions[i] = guild.getRoleById(EXEMPT[i]);
		}
		boolean exempt = false;
		Member member;
		
		for (int i = 0; i < members.size(); i++) {
			member = members.get(i);
			if(member.getRoles().contains(skeever)) {
				if(member.getRoles().size() <= 1) { //only skeever?
					tempDate = LocalDate.of(member.getJoinDate().getYear(), member.getJoinDate().getMonth().getValue(), member.getJoinDate().getDayOfMonth());
					days = DAYS.between(tempDate,date);
					if(days > Integer.parseInt(INITIAL_ACTIVITY_REQ)) {
						message+=""+ member.getEffectiveName() + " for not raiding with us after joining " + days + " days ago.\n";
					}
				} else { //other role(s) besides skeever?
					for(Raider r: attend) {
						if(member.getUser().getId().equals(r.getId())){
							days = DAYS.between(r.getRaidDate(),date);
							if(days > Integer.parseInt(ACTIVITY_REQ)) {
								for(int j = 0; j <exemptions.length; j++)
									if(member.getRoles().contains(exemptions[i])) {
										exempt = true;
									}
							}
							if (!exempt) {
								message+=""+member.getEffectiveName() + " for not raiding with us after " + days + " days.\n";
							}
						}
					} 
				}
			}
		}
		return message;
	}
}
