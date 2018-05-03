package com.deth.repository;

import static com.deth.util.FinalUtilChannels.RAID_FILE_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
			raid.setMaxTanks(Integer.parseInt(rosterInfoParse[0]));
			raid.setMaxHeals(Integer.parseInt(rosterInfoParse[1]));
			raid.setMaxDps(Integer.parseInt(rosterInfoParse[2]));
			String[] raidListParse;
			while((readBuffer = bufferedReader.readLine())!= null) {
				raidListParse = readBuffer.split(" ");
				//TODO: check logic info for sets. maybe check length/size of raidListParse?
				if(raidListParse.length == 2) {
					tempRaider = new Raider(raidListParse[0], raidListParse[1]);
				} else if (raidListParse.length == 3){
					tempRaider = new Raider(raidListParse[0], raidListParse[1], raidListParse[2]);
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
}
