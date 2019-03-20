package com.deth.repository;

import static com.deth.util.FinalUtilProperties.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.deth.model.Attendance;
import com.deth.model.Raider;

public class AttendanceRepository {
	private static final AttendanceRepository instance = new AttendanceRepository();
	
	private AttendanceRepository(){}
	
	public  static AttendanceRepository getInstance() {
		return instance;
	}
	
	public boolean readAttendance() throws IOException{
		String fileName = RAID_ACTIVITY;
		List<Raider> raiders = new ArrayList<>();
		Raider tempRaider = null;
		Attendance attendance = Attendance.getInstance();
		{
			String readBuffer = "";
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			String[] raidListParse;
			while((readBuffer = bufferedReader.readLine())!= null) {
				raidListParse = readBuffer.split(" ");
				if(raidListParse.length==2) {
					tempRaider = new Raider(raidListParse[0], LocalDate.parse(raidListParse[1]),0);
				} else if(raidListParse.length == 3) {
					tempRaider = new Raider(raidListParse[0], LocalDate.parse(raidListParse[1]), Integer.parseInt(raidListParse[2]));
				} else {
					bufferedReader.close();
					return false;
				}
				raiders.add(tempRaider);
			}
			bufferedReader.close();
		}
		attendance.setFullAttendance(raiders);
		return true;
	}
	
	public boolean writeAttendance() throws IOException {
		String fileName = RAID_ACTIVITY;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
		bufferedWriter.write(Attendance.getInstance().toFile());
		bufferedWriter.flush();
		bufferedWriter.close();
		return true;
	}
}
