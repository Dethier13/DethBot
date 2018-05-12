package com.deth.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static java.time.temporal.ChronoUnit.DAYS;

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
	
	public String showFUllAttendance(Guild guild) throws IOException{
		String message = "Entire attendance record on file:\n";
		attendanceRepository.readAttendance();
		List<Raider> fullAttendance = attendance.getFullAttendance();
		for(Raider r: fullAttendance) {
			message += "" + guild.getMemberById(r.getId()).getEffectiveName() +" "+ r.getRaidDate() + "\n";
		}
		return message;
	}
	
	public String removeRaider(String msg, Guild guild) {
		System.out.println("removeRaider.");
		if(!msg.contains(" ")) {
			return "You forgot to specify who to remove dumbass.";
		}
		System.out.println("passed idiot check.");
		msg = msg.substring(msg.indexOf(" ")+1, msg.length());
		System.out.println("remove raider-msg: "+ msg);
		
		String[] paramsCheck = msg.split(",");
		System.out.println("removal list: \n"+paramsCheck[0]);
		Raider raider = null;
		boolean isRemoved = true;
		String message = "You just removed: \n";
		for(String s: paramsCheck) {
			//s = s.substring(1);
			raider = new Raider(s,"");
			System.out.println("raider: " + raider.toAttendance());
			isRemoved = attendance.removeRaider(s);
			if(!isRemoved) {
				return "Something went wrong trying to remove id# " + s + " attendance removal has stopped.";
			}
			message += "" + guild.getMemberById(raider.getId()).getEffectiveName()+"\n";
		}
		System.out.println("Message: " + message);
		return message;
	}
	public String commitAttendance() throws IOException{
		System.out.println("In commit attendance.");
		String message = "Current raid attendance updated, raid roster cleared, you can no longer change the raid's attendance.";
		attendanceRepository.readAttendance();
		attendance.update();
		System.out.println("attendance updated.");
		attendanceRepository.writeAttendance();
		System.out.println("attendance writted.");
		return message;
	}
	
	public String kickList(Guild guild) throws IOException {
		String message = "The following people are slotted to be kicked due to not raiding yet:\n";
		System.out.println("attendance-service.");
		List<Role> roles = guild.getRoles();
		List<Role> exemptRoles = new ArrayList<>();
		exemptRoles.add(roles.get(0));
		exemptRoles.add(roles.get(1));
		exemptRoles.add(roles.get(2));
		System.out.println("-----\n exempt roles: " + exemptRoles +"\n------");
		System.out.println("found roles: " + roles);
		List<Member> members = guild.getMembersWithRoles(roles.get(roles.size()-2)); //roles are top-down
		System.out.println("lvl 1 role membners: " + members);
		attendanceRepository.readAttendance();
		List<Raider> attend = attendance.getFullAttendance();
		LocalDate date = LocalDate.now();
		LocalDate tempDate = null;
		boolean isBum = false;
		TreeMap<String,Integer> kickList = new TreeMap<String,Integer>();
		long days = 0;
		Period dateDifference;
		/**
		 * Sort through discord and see who is at the lowest role that has been in discord for over a month.
		 */
		for(Member m: members) {
			tempDate = LocalDate.of(m.getJoinDate().getYear(), m.getJoinDate().getMonth().getValue(), m.getJoinDate().getDayOfMonth());
			days = DAYS.between(tempDate,date);
			//dateDifference = Period.between(tempDate, date);
			System.out.println("member: " + m + " difference: " + days);
			if(days > 21 && m.getRoles().size() < 2) {
				kickList.put(m.getUser().getId(),(int) days);
				System.out.println("kicklist put skeevers.");
			}
		}
		/**
		 * different message for people that havent done anything
		 */
		for(Map.Entry<String,Integer> entry : kickList.entrySet()) {
		//for(int i = 0; i < kickList.size(); i++) {
			message+= ""+ guild.getMemberById(entry.getKey()).getEffectiveName() + " it has been: " + entry.getValue() + " days since they joined discord and they are still skeevers.\n";
			isBum = true;
		}
		kickList.clear();
		if (isBum) {
			message += "============================\n";
			message += "The following people have not been active in over 31 days:\n";
		}
		//int numRolestoCheck = roles.size()-3; //GM | officer | core ? need to check logic, might need 4th for owner
		/**
		 * add remainder of users in discord
		 */
		members.clear();
		System.out.println("next level members: " + members);
		for(int i = roles.size()-3; i > 2; i--) {
			members.addAll(guild.getMembersWithRoles(roles.get(i)));
		}
		/**
		 * cycle through remaining members and check if theyve raided with us in the last ~31 days. 
		 */
		System.out.println("full level members: " + members);
		
		for(Member m: members) {
			System.out.println("member: " + m);
			for(Raider r: attend) {
				System.out.println("raider: " + r.toAttendance());
				if(m.getUser().getId().equals(r.getId())){
					System.out.println("date: " + date + " r-date " + r.getRaidDate());
					//dateDifference = Period.between(r.getRaidDate(), date);
					days = DAYS.between(r.getRaidDate(),date);
					System.out.println("days difference: " + days + " for: " + m);
					if(days > 31 && !(m.getRoles().contains(exemptRoles.get(0)) || m.getRoles().contains(exemptRoles.get(1)) || m.getRoles().contains(exemptRoles.get(2)))) {
						kickList.put(r.getId(),(int) days);
					}
				}
			}
		}
		System.out.println("after double loop.");
		/**
		 * add everyone on the kick list with their name and how many days ago they raided with us.
		 */
		for(Map.Entry<String,Integer> entry : kickList.entrySet()) {
			message+= ""+ guild.getMemberById(entry.getKey()).getEffectiveName() + " last recorded raid was: " + entry.getValue() + " days ago.\n";
		}
		return message;
	}
}
