package com.deth.service;

import static com.deth.util.FinalUtilProperties.ACTIVITY_REQ;
//import static com.deth.util.FinalUtilProperties.CORE;
//import static com.deth.util.FinalUtilProperties.GM;
//import static com.deth.util.FinalUtilProperties.INITIAL_ACTIVITY_REQ;
//import static com.deth.util.FinalUtilProperties.OFFICER;
//import static com.deth.util.FinalUtilProperties.SKEEVER;
//import static com.deth.util.FinalUtilProperties.ZOMBIE;
import static com.deth.util.FinalUtilProperties.LOYALTY;
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
	
	public String raidBoard(Member member, String msg, Guild guild) throws IOException{
		String message = "Legions top 10 active raiders:\n";
		String personal = "-------------------------------\n";
		attendanceRepository.readAttendance();
		List<Raider> fullAttendance = attendance.getFullAttendance();
		int ranking = 0;
		for(Raider r: fullAttendance) {
			if(guild.getMemberById(r.getId())!= null ){
				ranking++;
				if (ranking < 11) {
					message += ranking + ": " + guild.getMemberById(r.getId()).getEffectiveName() + "\n";
				}
				if(r.getId().equals(member.getUser().getId())) {
					personal += guild.getMemberById(r.getId()).getEffectiveName() +" with " + r.getRaids() + " raids you rank " + ranking;
				}
				
			}
		}
		personal += " out of the " + (ranking--) + " raiders in guild";
		return message + personal;
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

		attendance.update();
		System.out.println("attendance updated.");
		//message+=promoteLoyalty(guild);
		System.out.println("loyalty should have been updated.");
		attendanceRepository.writeAttendance();
		return message;
	}
/*+
	private String promoteLoyalty(Guild guild) {
		System.out.println("loyalty successfully called.");
		String message = "promoted the following members for attending their 10th raid:\n";
		List<Raider> active = attendance.getFullAttendance();
		boolean promotion = false;
		Member member;
		for(Raider r: active) {
			System.out.println("in loyalty raider loop.");
			member = guild.getMemberById(r.getId());
			if(r.getRaids() == 10) {
				System.out.println("10 raids detected");
				if(!member.getRoles().contains(guild.getRoleById(LOYALTY))) {
					guild.getController().addSingleRoleToMember(member, guild.getRoleById(LOYALTY)).queue(); 
					message+=""+member.getEffectiveName() + "\n";
					promotion = true;
				}
			} else {
				System.out.println("not enough raids detected, only found, only found: " + r.getRaids());
			}
		}
		if(!promotion) {
			
			return "";
		}
		return message;
	} */
	
	public String kickList(Guild guild) throws IOException {
		String message = "The following people are slotted to be kicked:\n";
		attendanceRepository.readAttendance();
		List<Raider> attend = attendance.getFullAttendance();
		LocalDate date = LocalDate.now();
		LocalDate tempDate = null;
		long days = 0;
		List<Member> members = guild.getMembers();

		Role[] exemptions = new Role[EXEMPT.length];
		for (int i = 0; i < EXEMPT.length; i++) {
			exemptions[i] = guild.getRoleById(EXEMPT[i]);
		}
		boolean exempt = false;
		Member member;
		
		for (int i = 0; i < members.size(); i++) {
			member = members.get(i);
					for(Raider r: attend) {
						if(member.getUser().getId().equals(r.getId())){
							days = DAYS.between(r.getRaidDate(),date);
							if(days > Integer.parseInt(ACTIVITY_REQ)) {
								for( Role role: member.getRoles()) {
									for(int j = 0; j <exemptions.length; j++)
										if(role.getId().equals(exemptions[j].getId())) {
											exempt = true;
										}
								}
								
								if (!exempt) {
									message+=""+member.getEffectiveName() + " for not raiding with us after " + days + " days.\n";
									exempt = false;
								}
							}
							
						}
					} 
				
			//}
		}
		return message;
	}
	
	public String raidCount(Member member, String msg, int type, Guild guild) throws IOException {
		attendanceRepository.readAttendance();
		String message = "";
		List<Raider> attend = attendance.getFullAttendance();
		Raider raider = new Raider(member.getUser().getId(),"");
		boolean found = false;
		//if (type == 0) {
			message = "You have ";
			for (Raider r: attend) {
				if(r.getId().equals(raider.getId())) {
					message+= r.getRaids() + " raids recorded with legion, the last one was on " + r.getRaidDate().getMonth().toString().toLowerCase() + " " + r.getRaidDate().getDayOfMonth();
					found = true;
				}
			}
		if(found) {
			return message;
		}
		return "I was unable to find you in my attendance records";
	}
	
	public String exemptList(Guild guild) {
		String message = "Anybody with the following roles are exempt from attendance calculations:\n";
		Role[] exemptions = new Role[EXEMPT.length];
		for (int i = 0; i < EXEMPT.length; i++) {
			exemptions[i] = guild.getRoleById(EXEMPT[i]);
			message += exemptions[i].getName() + "\n";
		}
			
		return message;
	}
}
