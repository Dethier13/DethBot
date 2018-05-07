package com.deth.model;

import java.util.ArrayList;
import java.util.List;

import static com.deth.util.FinalUtilChannels.*;

public class Raid {
	private String raidName;
	private int maxTanks;
	private int numTank;
	private int maxHeals;
	private int numHeal;
	private int maxDps;
	private int numDps;
	private List<Raider> raiders;
	private List<Raider> starters;
	private List<Raider> overflow;
	
	public Raid() {}
	
	/**
	 * specifically for filecheck
	 * @param raidName
	 */
	public Raid(String raidName) {
		this.raidName = raidName;
		this.maxTanks = 0;
		this.maxHeals = 0;
		this.maxDps = 0;
		this.numTank = 0;
		this.numHeal = 0;
		this.numDps = 0;
		this.starters= new ArrayList<>();
		this.overflow= new ArrayList<>();
	}

	/**
	 * specifically for opening/closing raids
	 * @param raidName
	 * @param maxTanks
	 * @param maxHeals
	 * @param maxDps
	 */
	public Raid(String raidName, int maxTanks, int maxHeals, int maxDps) {
		super();
		this.raidName = raidName;
		this.maxTanks = maxTanks;
		this.maxHeals = maxHeals;
		this.maxDps = maxDps;
		this.numTank = 0;
		this.numHeal = 0;
		this.numDps = 0;
		this.starters= new ArrayList<>();
		this.overflow= new ArrayList<>();
	}
	
	/**
	 * for signup/withdraw/status?
	 * @param raidName
	 * @param maxTanks
	 * @param maxHeals
	 * @param maxDps
	 * @param raiders
	 */
	public Raid(String raidName, int maxTanks, int maxHeals, int maxDps, List<Raider> raiders) {
		super();
		this.raidName = raidName;
		this.maxTanks = maxTanks;
		this.maxHeals = maxHeals;
		this.maxDps = maxDps;
		this.raiders = raiders;
		this.starters= new ArrayList<>();
		this.overflow= new ArrayList<>();
		this.numTank = 0;
		this.numHeal = 0;
		this.numDps = 0;
		updateRosters();
	}

	public String getRaidName() {
		return raidName;
	}

	public void setRaidName(String raidName) {
		this.raidName = raidName;
	}

	public int getNumTank() {
		return numTank;
	}

	public void setNumTank(int numTank) {
		this.numTank = numTank;
	}

	public int getNumHeal() {
		return numHeal;
	}

	public void setNumHeal(int numHeal) {
		this.numHeal = numHeal;
	}

	public int getNumDps() {
		return numDps;
	}

	public void setNumDps(int numDps) {
		this.numDps = numDps;
	}

	public List<Raider> getRaiders() {
		return raiders;
	}

	public void setRaiders(List<Raider> raiders) {
		this.raiders = raiders;
		updateRosters();
	}
	

	public List<Raider> getStarters() {
		return starters;
	}

	public List<Raider> getOverflow() {
		return overflow;
	}

	public int getMaxTanks() {
		return maxTanks;
	}

	public void setMaxTanks(int maxTanks) {
		this.maxTanks = maxTanks;
	}

	public int getMaxHeals() {
		return maxHeals;
	}

	public void setMaxHeals(int maxHeals) {
		this.maxHeals = maxHeals;
	}

	public int getMaxDps() {
		return maxDps;
	}

	public void setMaxDps(int maxDps) {
		this.maxDps = maxDps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numDps;
		result = prime * result + numHeal;
		result = prime * result + numTank;
		result = prime * result + ((raidName == null) ? 0 : raidName.hashCode());
		result = prime * result + ((raiders == null) ? 0 : raiders.hashCode());
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
		Raid other = (Raid) obj;
		if (numDps != other.numDps)
			return false;
		if (numHeal != other.numHeal)
			return false;
		if (numTank != other.numTank)
			return false;
		if (raidName == null) {
			if (other.raidName != null)
				return false;
		} else if (!raidName.equals(other.raidName))
			return false;
		if (raiders == null) {
			if (other.raiders != null)
				return false;
		} else if (!raiders.equals(other.raiders))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Raid [raidName=" + raidName + ", maxTanks=" + maxTanks + ", numTank=" + numTank + ", maxHeals="
				+ maxHeals + ", numHeal=" + numHeal + ", maxDps=" + maxDps + ", numDps=" + numDps + ", raiders="
				+ raiders + ", starters=" + starters + ", overflow=" + overflow + "]";
	}

	public List<Raider> addRaider(Raider raider){
		if(raider != null) {
			raiders.add(raider);
			updateRosters();
		}
		return raiders;
	}
	
	public boolean removeRaider(Raider raider) {
		if (raider != null) {
			raiders.remove(raider);
			if(starters.contains(raider)) {
				starters.remove(raider);
			} else if (overflow.contains(raider)) {
				overflow.remove(raider);
			} else {
				return false;
			}
			updateRosters();
			return true;
		}
		return false;
	}
	
	private void updateRosters() {
		starters.removeAll(starters);
		overflow.removeAll(overflow);
		numTank = 0;
		numHeal = 0;
		numDps = 0;
		for(int i = 0; i < raiders.size(); i++) {
			if(raiders.get(i).getRole().equals(ROLE_TANK)) {
				this.numTank++;
				if(numTank <= maxTanks) {
					starters.add(raiders.get(i));
				} else {
					overflow.add(raiders.get(i));
				}
			} 
		}
		for(int i = 0; i < raiders.size(); i++) {
			if(raiders.get(i).getRole().equals(ROLE_HEALS)) {
				this.numHeal++;
				if(numHeal <= maxHeals) {
					starters.add(raiders.get(i));
				} else {
					overflow.add(raiders.get(i));
				}
			}
		}
		for(int i = 0; i < raiders.size(); i++) {
			if (raiders.get(i).getRole().equals(ROLE_DPS)) {
				this.numDps++;
				if(numDps <= maxDps) {
					starters.add(raiders.get(i));
				} else {
					overflow.add(raiders.get(i));
				}
			}
		}
	}
	
	public String toFileInfo() {
		return ""+this.maxTanks + " " + this.maxHeals + " " + this.maxDps;
	}
	
	public String toFileRoster() {
		String roster ="";
		for(Raider r: starters) {
			roster+=r.toFile();
		}
		if(overflow.size() > 0) {
			for(Raider r: overflow) {
				roster+=r.toFile();
			}
		}
		return roster;
	}
}
