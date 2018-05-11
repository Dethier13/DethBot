package com.deth.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.deth.model.Attendance;
import com.deth.model.Raider;
import com.deth.repository.AttendanceRepository;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class AttendanceService {
	//TODO: add help
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
	
	public String showFUllAttendance(Guild guild) {
		String message = "Entire attendance record on file:\n";
		List<Raider> fullAttendance = attendance.getFullAttendance();
		for(Raider r: fullAttendance) {
			message += "" + guild.getMemberById(r.getId()).getEffectiveName() +" "+ r.getRaidDate() + "\n";
		}
		return message;
	}
	
	public String removeRaider(String msg, Guild guild) {
		msg = msg.substring(msg.indexOf(" "), msg.length());
		String[] paramsCheck = msg.split(", ");
		if(paramsCheck.length < 1) {
			return "You forgot to specify who to remove dumbass.";
		}
		Raider raider = null;
		boolean isRemoved = true;
		String message = "You just removed: \n";
		for(String s: paramsCheck) {
			raider = new Raider(s,"");
			isRemoved = attendance.removeRaider(s);
			if(!isRemoved) {
				return "Something went wrong trying to remove id# " + s + " attendance removal has stopped.";
			}
			message = "" + guild.getMemberById(raider.getId()).getEffectiveName()+"\n";
		}
		return message;
	}
	public String commitAttendance() throws IOException{
		String message = "Current raid attendance updated, raid roster cleared, you can no longer change the raid's attendance.";
		attendance.update();
		attendanceRepository.writeAttendance();
		return message;
	}
	
	public String kickList(Guild guild) {
		String message = "The following people are slotted to be kicked:\n";
		List<Role> roles = guild.getRoles();
		List<Member> members = guild.getMembersWithRoles(roles.get(1)); //0 is the everyone role?

		List<Raider> attend = attendance.getFullAttendance();
		LocalDate date = LocalDate.now();
		LocalDate tempDate = null;

		TreeMap<String,Integer> kickList = new TreeMap<String,Integer>();
		Period dateDifference;
		/**
		 * Sort through discord and see who is at the lowest role that has been in discord for over a month.
		 */
		for(Member m: members) {
			tempDate = LocalDate.of(m.getJoinDate().getYear(), m.getJoinDate().getMonth().getValue(), m.getJoinDate().getDayOfMonth());
			dateDifference = Period.between(tempDate, date);
			if(dateDifference.getDays() > 31) {
				kickList.put(m.getUser().getId(),dateDifference.getDays());
			}
		}
		int numRolestoCheck = roles.size()-3; //GM | officer | core ? need to check logic, might need 4th for owner
		/**
		 * add remainder of users in discord
		 */
		members = guild.getMembersWithRoles(roles.get(2));
		for(int i = 3; i < numRolestoCheck; i++) {
			members.addAll(guild.getMembersWithRoles(roles.get(i)));
		}
		/**
		 * cycle through remaining members and check if theyve raided with us in the last ~31 days. 
		 */
		for(Member m: members) {
			for(Raider r: attend) {
				if(m.getUser().getId().equals(r.getId())){
					dateDifference = Period.between(r.getRaidDate(), date);
					if(dateDifference.getDays() > 31) {
						kickList.put(r.getId(),dateDifference.getDays());
					}
				}
			}
		}
		/**
		 * add everyone on the kick list with their name and how many days ago they raided with us.
		 */
		for(Map.Entry<String,Integer> entry : kickList.entrySet()) {
			message+= ""+ guild.getMemberById(entry.getKey()).getEffectiveName() + " last recorded raid was: " + entry.getValue() + " days ago.";
		}
		return message;
	}
}
