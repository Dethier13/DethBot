package com.deth.repository;

import static com.deth.util.FinalUtilProperties.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.deth.model.Raid;
import com.deth.model.Raider;

public class RaidFileRepository {
	private static final RaidFileRepository instance = new RaidFileRepository();
	
	private RaidFileRepository() {}
	
	public static RaidFileRepository getInstance() {
		return instance;
	}

	public boolean raidCheck(Raid raid) {
		String fileName=RAID_FILE_PATH + raid.getRaidName() + ".txt";
		File fileCheck = new File(fileName);
		if(fileCheck.exists()) {
			return true;
		}
		return false;
	}
	
	public boolean createRaid(Raid raid) throws IOException{
		String fileName=RAID_FILE_PATH + raid.getRaidName() + ".txt";
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
			bufferedWriter.write(raid.toFileInfo());
			bufferedWriter.close();
			return true;
		} catch(IOException ioe) {
			throw new IOException("Failed to create raid");
		} 
	}
	
	public boolean deleteRaid(Raid raid) throws IOException{
		String fileName=RAID_FILE_PATH + raid.getRaidName() + ".txt";
		File raidFile = new File(fileName);
		if(raidFile.delete()) {
			return true;
		}
		return false;
	}
	
	/**
	 * read all info from file to update raid object.
	 * @param raid
	 * @return
	 * @throws IOException
	 */
	public Raid readRaid(Raid raid) throws IOException{
		String fileName=RAID_FILE_PATH + raid.getRaidName() + ".txt";
		List<Raider> raidList = new ArrayList<>();
		Raider tempRaider = null;
		{
			String readBuffer = "";
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			String rosterInfo = bufferedReader.readLine();
			String[] rosterInfoParse = rosterInfo.split(" ");
			raid.setTier(Integer.parseInt(rosterInfoParse[0]));
			raid.setMaxTanks(Integer.parseInt(rosterInfoParse[1]));
			raid.setMaxHeals(Integer.parseInt(rosterInfoParse[2]));
			raid.setMaxMeleeDps(Integer.parseInt(rosterInfoParse[3]));
			raid.setMaxDps(Integer.parseInt(rosterInfoParse[4]));
			raid.setRaidMsg(bufferedReader.readLine());
			String[] raidListParse;
			while((readBuffer = bufferedReader.readLine())!= null) {
				raidListParse = readBuffer.split(" ");

				if(raidListParse.length == 2) {
					tempRaider = new Raider(raidListParse[0], raidListParse[1]);
				} else {
					bufferedReader.close();
					return null;
				}
				raidList.add(tempRaider);
			}
			bufferedReader.close();
		}
		raid.setRaiders(raidList);
		return raid;
	}
	
	public boolean writeRaid(Raid raid) throws IOException{
		String fileName=RAID_FILE_PATH + raid.getRaidName() + ".txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
		bufferedWriter.write(raid.toFileInfo());
		bufferedWriter.newLine();
		bufferedWriter.write(raid.toFileRoster());
		bufferedWriter.flush();
		bufferedWriter.close();
		return true;
	}
	
	public List<Raider> readDefaults() throws IOException{
		String fileName = DEFAULTS_FILE_PATH;
		List<Raider> raiderList = new ArrayList<>();
		Raider tempRaider = null;
		{
			String readBuffer = "";
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			
			String[] raidersParse;
			while((readBuffer = bufferedReader.readLine())!= null) {
				raidersParse = readBuffer.split(" ");
				if(raidersParse.length == 2) {
					tempRaider = new Raider(raidersParse[0], raidersParse[1]);
				} else {
					bufferedReader.close();
					return null;
				}
				raiderList.add(tempRaider);
			}
			bufferedReader.close();
			
		}
		
		return raiderList;
	}
	
	public boolean writeDefaults(List<Raider> defaults) throws IOException {
		String fileName = DEFAULTS_FILE_PATH;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
		for(Raider raider: defaults) {
			bufferedWriter.write(raider.toFile());
		}
		bufferedWriter.flush();
		bufferedWriter.close();
		return true;
	}
}
