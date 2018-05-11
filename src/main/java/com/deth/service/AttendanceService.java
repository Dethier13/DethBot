package com.deth.service;

import java.io.IOException;
import java.util.List;

import com.deth.model.Attendance;
import com.deth.model.Raider;
import com.deth.repository.AttendanceRepository;

import net.dv8tion.jda.core.entities.Guild;

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
	
	public String showFUllAttendance(Guild guild) {
		String message = "Entire attendance record on file:\n";
		List<Raider> fullAttendance = attendance.getFullAttendance();
		for(Raider r: fullAttendance) {
			message += "" + guild.getMemberById(r.getId()).getEffectiveName() +" "+ r.getRaidDate() + "\n";
		}
		return message;
	}
	
	public String removeRaider(String msg, Guild guild) {
		String[] paramsCheck = msg.split(" ");
		if(paramsCheck.length < 2) {
			return "You forgot to specify who to remove dumbass.";
		}
		Raider raider = new Raider(paramsCheck[1],"");
		String message = "You just removed " + guild.getMemberById(raider.getId()).getEffectiveName();
		boolean isRemoved = attendance.removeRaider(raider);
		if(!isRemoved) {
			return "Some error occured removing them, check your command and the discord ID.";
		}
		return message;
	}
	public String commitAttendance() throws IOException{
		String message = "Current raid attendance updated, raid roster cleared, you can no longer change the raid's attendance.";
		attendance.update();
		attendanceRepository.writeAttendance();
		return message;
	}
}
