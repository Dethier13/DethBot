package com.deth.service;

import static com.deth.util.FinalUtilChannels.RAID_FILE_PATH;
import static com.deth.util.FinalUtilChannels.ROLE_DPS;
import static com.deth.util.FinalUtilChannels.ROLE_HEALS;
import static com.deth.util.FinalUtilChannels.ROLE_TANK;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.deth.model.Raid;
import com.deth.model.Raider;
import com.deth.repository.RaidFileRepository;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class RaidService {
	private static final RaidService instance = new RaidService();
	private RaidFileRepository raidRepository = RaidFileRepository.getInstance();
	private RaidService() {}
	
	public static RaidService getInstance() {
		return instance;
	}
	
	//=====================================================================================================================
	// OFFICER COMMANDS
	//=====================================================================================================================
	public String openRaid(String msg) throws IOException{
		String anouncement = "@everyone Signups are now open for ";
		//get raid info
		String[] raidInfo = msg.split(" ");
		Raid newRaid = new Raid(raidInfo[1].toLowerCase(), 
				Integer.parseInt(raidInfo[2]), 
				Integer.parseInt(raidInfo[3]), 
				Integer.parseInt(raidInfo[4])
				);
		anouncement += newRaid.getRaidName() + ". ";
		if(raidRepository.raidCheck(newRaid)) {
			return "That raid is already open.";
		}
		
		if(!raidRepository.createRaid(newRaid)) {
			return null;
		}
		
		String[] raidMessage = msg.split("msg:");
		if(raidMessage.length > 1) {
			anouncement += raidMessage[1];
		}
		
		
		return anouncement;
	}
	
	public String closeRaid(String msg) throws IOException{
		String raidName = msg.split(" ")[1];
		Raid closedRaid = new Raid(raidName);
		
		if(!raidRepository.raidCheck(closedRaid)) {
			return "Raid not found.";
		}
		
		if(!raidRepository.deleteRaid(closedRaid)) {
			return null;
		}
		return "The raid " + closedRaid.getRaidName() + " has been closed.";
	}
	
	public String rollCall(String msg, Guild guild) throws IOException{
		String[] paramsCheck = msg.split(" ");
		String ai = "blood";
		if(paramsCheck.length < 2) {
			return null;
		}
		if(paramsCheck.length == 3) {
			ai = paramsCheck[2];
		}
		String raidName = paramsCheck[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Raid not found.";
		}
		raid = raidRepository.readRaid(raid);
		String roster = "Forming up for " + raid.getRaidName() + "the following people please log in and enter '" + ai +"' in chat for invite\n";
		
		for(Raider r: raid.getStarters()) {
			roster+=r.toRollCall();
		}
		if(raid.getOverflow().size() > 0) {
			roster+= "--------- \nOverflow: \n--------- \nThe following people please be ready to sub in if required:\n";
			for(Raider r: raid.getOverflow()) {
				roster+=guild.getMemberById(r.getId());
			}
		}
		return roster;
	}
	
	//=====================================================================================================================
	// GUILDIE COMMANDS
	//=====================================================================================================================

	public String signup(Member member, String msg) throws IOException{
		String[] paramsCheck = msg.split(" ");
		boolean isUpdate = false;
		boolean isOverflow = false;
		if(paramsCheck.length < 2) {
			return "You forgot to specify the raid you want to sign up for.";
		}
		String raidName = paramsCheck[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
	
		Raider raider = new Raider(member.getUser().getId(),"");
		
		if (paramsCheck.length < 3) {
			if(getDefault(raider) != null) {
				raider.setRole(getDefault(raider));
			} else {
				raider.setRole(ROLE_DPS);
			}
		} else {
			if(paramsCheck[2].equalsIgnoreCase("heal") || paramsCheck[2].equalsIgnoreCase("heals") ||paramsCheck[2].equalsIgnoreCase("healer") || paramsCheck[2].equals(ROLE_HEALS)) {
				raider.setRole(ROLE_HEALS);
			} else if(paramsCheck[2].equalsIgnoreCase("tank") || paramsCheck[2].equalsIgnoreCase("meatshield") || paramsCheck[2].equals(ROLE_TANK)) {
				raider.setRole(ROLE_TANK);
			} else {
				raider.setRole(ROLE_DPS);
			}
		}
	
		List<Raider> raiders = raid.getRaiders();

		for(Raider r: raiders) {
			if(r.getId().equals(raider.getId())) {
				isUpdate = true;
				raiders.remove(r);
				raid.setRaiders(raiders);
				break;
			}
		}
		raid.addRaider(raider);
	
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			
			return "Something happened when adding you to the raid, please try again later.";
		}

		
		//check if overflow.
		if(raid.getOverflow().size() > 0) {
			List<Raider> overflow = raid.getOverflow();
			for(Raider r: overflow) {
				if(r.equals(raider)) {
					isOverflow = true;
				}
			}
		}
	
		String message = "" + member.getEffectiveName() + " successfully signed up for " + raidName + " as " + raider.getRole();
		if(isUpdate) {
			message =  member.getEffectiveName() + " successfully updated role for " + raidName + " to " + raider.getRole();
		}
		if(isOverflow) {
			message += " but the raid is full for that role, you've been added to overflow.";
		}
		return message;
	}
	
	public String withdraw(Member member, String msg) throws IOException {
		String[] paramsCheck = msg.split(" ");
		boolean withdrawn = false;
		if(paramsCheck.length < 2) {
			return "You forgot to specify the raid you want to withdraw from.";
		}
		String raidName = paramsCheck[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
		Raider raider = new Raider(member.getUser().getId(),"");
		List<Raider> raiders = raid.getRaiders();
		for(Raider r: raiders) {
			if(r.getId().equals(raider.getId())) {
				raiders.remove(r);
				raid.removeRaider(r);
				withdrawn = true;
				break;
			}
		}
		if(!withdrawn) {
			return "Please make sure you are already signed up for a raid before trying to withdraw from it.";
		}
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			return "Something happened trying to refresh the raid roster, hopefully no data was lost....";
		}
		return "You have successfully withdrawn from " + raidName+".";
		
	}
	
	public String getDefault(Raider raider) throws IOException {
		List<Raider> defaults = raidRepository.readDefaults();
		String role = null;
		if(defaults != null) {
			for(Raider tempRaider: defaults) {
				if(tempRaider.getId().equals(raider.getId())) {
					role = tempRaider.getRole();
					break;
				}
			}
		}
		return role;
	}
	
	public String setDefault(Member member, String msg ) throws IOException {
		String message = "";
		String[] paramsCheck = msg.split(" ");
		if(paramsCheck.length < 2) {
			return "You forgot to specify the role you want to set as default.";
		}
		Raider raider = new Raider(member.getUser().getId(),"");
		if (paramsCheck.length >= 2)  {
			if(paramsCheck[1].equalsIgnoreCase("heal") || paramsCheck[1].equalsIgnoreCase("heals") ||paramsCheck[1].equalsIgnoreCase("healer") || paramsCheck[1].equals(ROLE_HEALS)) {
				raider.setRole(ROLE_HEALS);
			} else if(paramsCheck[1].equalsIgnoreCase("tank") || paramsCheck[1].equalsIgnoreCase("meatshield") || paramsCheck[1].equals(ROLE_TANK)) {
				raider.setRole(ROLE_TANK);
			} else {
				raider.setRole(ROLE_DPS);
			}
		}
		List<Raider> defaults = raidRepository.readDefaults();
		if(defaults != null) {
			for(Raider tempRaider: defaults) {
				if(tempRaider.getId().equals(raider.getId())) {
					defaults.remove(tempRaider);
					break;
				}
			}
		}
		defaults.add(raider);
		boolean isWritten = raidRepository.writeDefaults(defaults);
		if(!isWritten) {
			return "Error occored recording default role.";
		}
		message = "Successfully updated the default role of " + member.getEffectiveName() + " to " + raider.getRole()+".";
		return message;
	}
	
	public String status(String msg, Guild guild) throws IOException{
		
		String[] paramsCheck = msg.split(" ");
		if(paramsCheck.length < 2) {
			return "You forgot to specify the raid you want to check.";
		}
		String raidName = paramsCheck[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
		String roster = "";
		if(raid.getStarters().size() > 0) {
			for(Raider r: raid.getStarters()) {
				roster+=guild.getMemberById(r.getId()).getEffectiveName() + " " + r.getRole() + "\n";
			}
			if(raid.getOverflow().size() > 0) {
				roster+= "--------- \nOverflow: \n--------- \n";
				for(Raider r: raid.getOverflow()) {
					roster+=guild.getMemberById(r.getId()).getEffectiveName() + " " + r.getRole() + "\n";
				}
			}
			roster += "+------------+--------------+\n";
			roster += "| Tanks: " + raid.getNumTank() + "/"+raid.getMaxTanks() + " | Heals: " + raid.getNumHeal() + "/" + raid.getMaxHeals() + "   |\n";
			roster += "+------------+--------------+\n";
			roster += "| Dps: "+ raid.getNumDps() + "/" + raid.getMaxDps() + "     | Total: " + raid.getStarters().size() + "/12   |\n";
			roster += "+------------+--------------+\n";
			return roster;
		} else {
			return "Nobody has signed up for this raid yet.";
		}
	}
	
	public String raidList() {
		String pathName=RAID_FILE_PATH;
		File filePath = new File(RAID_FILE_PATH);
		ArrayList<String> raids = new ArrayList<String>(Arrays.asList(filePath.list()));
		String message = "The current available raids are: \n";
		boolean isRaids = raids.size()>0;
		if(!isRaids) {
			message += "There are no raids currently open. \n Please check the raid schedule, raids are generally opened ~24 hours before a scheduled raid.";
		}
		for(int i = 0; i < raids.size(); i++) {
			message += raids.get(i).substring(0, raids.get(i).indexOf('.'));
			message += "\n";
		}
		return message;
	}
}
