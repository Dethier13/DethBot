package com.deth.model;

import java.util.Collections;
import java.util.List;

public class Attendance {
	Raider[] raiders = new Raider[12];
	List<Raider> fullAttendance;

	public Attendance() {
		this.fullAttendance=Collections.emptyList();
	}
	public Attendance(List<Raider> raiders) {
		super();
		this.fullAttendance = raiders;
	}
	public List<Raider> getFullAttendance() {
		return fullAttendance;
	}
	
	public void setRaiders()
	
	public boolean update(Raider raider) {
		if(raider == null) {
			return false;
		}
		for(Raider r: fullAttendance) {
			if(r.getId().equals(raider.getId())){
				fullAttendance.remove(r);
				fullAttendance.add(raider);
			}
		}
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
