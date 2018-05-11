package com.deth.model;

import java.util.Collections;
import java.util.List;

public class Attendance {
	private List<Raider> raidRoster;
	private List<Raider> fullAttendance;
	private static Attendance instance = new Attendance();
	

	private Attendance() {
		this.raidRoster=Collections.emptyList();
		this.fullAttendance=Collections.emptyList();
	}
	public static Attendance getInstance() {
		return instance;
	}
	public void setFullAttendance(List<Raider> attendance) {
		this.fullAttendance = attendance;
	}
	public List<Raider> getFullAttendance() {
		return fullAttendance;
	}
	
	public void setRaiders(List<Raider> raiders) {
		this.raidRoster=raiders;
	}
	public List<Raider> getRaiders() {
		return this.raidRoster;
	}
	
	public boolean removeRaider(Raider raider) {
		this.raidRoster.remove(raider);
		return true;
	}
	
	public boolean removeRaider(String iD) {
		boolean isRemoved = false;
		for(Raider r: raidRoster) {
			if(r.getId().equals(iD)){
				raidRoster.remove(r);
				isRemoved = true;
			}
		}
		return isRemoved;
	}
	
	public boolean update() {
		for(Raider r: raidRoster) {
			for(Raider attend: fullAttendance) {
				if(r.getId().equals(attend.getId())){
					fullAttendance.remove(r);
					fullAttendance.add(attend);
					raidRoster.remove(r);
				}
			}
		}
		for(Raider r: raidRoster) {
			fullAttendance.add(r);
		}
		fullAttendance = Collections.emptyList();
		return true;
	}
	
	public String toFile() {
		String attendance = "";
		for(Raider r: fullAttendance) {
			attendance += r.toAttendance();
		}
		return attendance;
	}
	
	
}
