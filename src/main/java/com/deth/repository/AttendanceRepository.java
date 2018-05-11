package com.deth.repository;

import static com.deth.util.FinalUtilProperties.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.deth.model.Attendance;
import com.deth.model.Raider;

public class AttendanceRepository {
	private static final AttendanceRepository instance = new AttendanceRepository();
	
	private AttendanceRepository(){}
	
	public  AttendanceRepository getInstance() {
		return instance;
	}
	
	public Attendance readAttendance() throws IOException{
		String fileName = RAID_ACTIVITY;
		List<Raider> raiders = new ArrayList<>();
		Raider tempRaider = null;
		Attendance attendance = null;
		{
			String readBuffer = "";
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			String[] raidListParse;
			while((readBuffer = bufferedReader.readLine())!= null) {
				raidListParse = readBuffer.split(" ");
				if(raidListParse.length == 2) {
					tempRaider = new Raider(raidListParse[0], raidListParse[1]);
				} else {
					bufferedReader.close();
					return null;
				}
				raiders.add(tempRaider);
			}
			bufferedReader.close();
		}
		attendance = new Attendance(raiders);
		return attendance;
	}
	
	public boolean writeAttendance(Attendance attendance) throws IOException {
		String fileName = RAID_ACTIVITY;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
		bufferedWriter.write(attendance.toFile());
		bufferedWriter.flush();
		bufferedWriter.close();
		return true;
	}
}
