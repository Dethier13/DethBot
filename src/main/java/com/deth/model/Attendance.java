package com.deth.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Attendance {
	private List<Raider> raidRoster;
	private List<Raider> fullAttendance;
	private static Attendance instance = new Attendance();
	

	private Attendance() {
		this.raidRoster= new ArrayList<>();
		this.fullAttendance= new ArrayList<>();
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
	
	public synchronized boolean removeRaider(String iD) {
		boolean isRemoved = false;
		System.out.println("in removeRaider. checking for id:"+ iD + ". currentRoster: " + raidRoster.toString());
		for(int i = 0; i < raidRoster.size(); i++) {
			System.out.println("In loop, current raider check: " + raidRoster.get(i).getId() + " against: " + iD + ".");
			if(raidRoster.get(i).getId().equals(iD)){
				System.out.println("raider found: " + raidRoster.get(i).toAttendance());
				raidRoster.remove(i);
				i--;
				isRemoved = true;
			}
		}
		System.out.println("loop finished.");
		return isRemoved;
	}
	
	public boolean update() {
		System.out.println("Attendance update().");
		for(int i = 0; i < raidRoster.size(); i++) {
			System.out.println("update loop, raider:  " + raidRoster.get(i).toAttendance());
			for (int j = 0; j < fullAttendance.size(); j++)	
				if(raidRoster.get(i).getId().equals(fullAttendance.get(j).getId())){
					System.out.println("raider found, updating.");
					fullAttendance.remove(fullAttendance.get(j));
					j--;
					System.out.println("J decrament");
					raidRoster.get(i).setRaidDate(LocalDate.now());
					fullAttendance.add(raidRoster.get(i));
					raidRoster.remove(i);
					i--;
					System.out.println("I decrament");
					break;
				}
			}
		
		System.out.println("update loop finished.");
		for(Raider r: raidRoster) {
			System.out.println("second loop, raider:" + r.toAttendance());
			r.setRaidDate(LocalDate.now());
			fullAttendance.add(r);
		}
		System.out.println("second loop finished.");
		raidRoster.removeAll(raidRoster);
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
