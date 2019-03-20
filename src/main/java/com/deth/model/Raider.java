package com.deth.model;

import java.time.LocalDate;
import java.util.Date;

public class Raider {
	private String id;
	private String role;
	private String position;
	private String info;
	private LocalDate raidDate;
	private int raids;
	
	public Raider() {}

	public Raider(String id, String role) {
		super();
		this.id = id;
		this.role = role;
		this.position = null;
	}

	public Raider(String id, String role, String info) {
		super();
		this.id = id;
		this.role = role;
		this.info = info;
		this.position = null;
	}
	
	public Raider(String id, LocalDate raidDate, int raids) {
		super();
		this.id = id;
		this.raidDate = raidDate;
		this.raids = raids;
		this.position = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public LocalDate getRaidDate() {
		return raidDate;
	}
	
	public void setRaidDate(LocalDate raidDate) {
		this.raidDate = raidDate;
		this.raids++;
		System.out.println("numRaids: " + this.raids);
	}
	
	

	public int getRaids() {
		return raids;
	}

	public void setRaids(int raids) {
		this.raids = raids;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Raider other = (Raider) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Raider [id=" + id + ", role=" + role + ", info=" + info + "]";
	}
	
	public String toFile() {
		return ""+this.id+" " + this.role + " " + this.info  + "\n";
	}
	
	public String toAttendance() {
		return ""+this.id+" " + this.raidDate+ " " + this.raids + "\n";
	}
	
	public String toFileWithInfo() {
		return ""+this.id+" " + this.role + " " + this.info + "\n";
	}
	
	public String toRollCall() {
		return "<@"+ this.id+"> " + this.role + ((this.info== null || this.info.equals("null") )?  "" : ("info: " + this.info)) + "\n";
	}
}
