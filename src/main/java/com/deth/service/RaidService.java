package com.deth.service;

import static com.deth.util.FinalUtilProperties.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.deth.model.Attendance;
import com.deth.model.Raid;
import com.deth.model.Raider;
import com.deth.repository.MessageFileRepository;
import com.deth.repository.RaidFileRepository;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class RaidService {
	private static final RaidService instance = new RaidService();
	private RaidFileRepository raidRepository = RaidFileRepository.getInstance();
	private MessageFileRepository msgRepository = MessageFileRepository.getInstance();
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
				Integer.parseInt(raidInfo[3]), 
				Integer.parseInt(raidInfo[4]), 
				Integer.parseInt(raidInfo[5]),
				Integer.parseInt(raidInfo[6])
				);
		newRaid.setTier(Integer.parseInt(raidInfo[2]));
		anouncement += newRaid.getRaidName() + ". ";
		String[] raidMessage = msg.split("msg:");
		if(raidMessage.length > 1) {
			anouncement += raidMessage[1];
			newRaid.setRaidMsg(raidMessage[1]);
			System.out.println("msg: " + raidMessage[1]);
			System.out.println("raid msg: " + newRaid.getRaidMsg());
		}
		
		if(raidRepository.raidCheck(newRaid)) {
			return "That raid is already open.";
		}
		
		if(!raidRepository.createRaid(newRaid)) {
			return null;
		}
		return anouncement;
	}
	
	public String updateRaid(String msg) throws IOException{
		String raidName = msg.split(" ")[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
		if(msg.split(" ")[2].equals("roles") || msg.split(" ")[2].equals("role")) {

			return updateRoles(raid,msg);
		} else if (msg.split(" ")[2].equals("message:") || msg.split(" ")[2].equals("mesage:") || msg.split(" ")[2].equals("msg:")) {

			return updateMsg(raid, msg);
		}  else if(msg.split(" ")[2].equalsIgnoreCase("tier")){
			return updateTier(raid,Integer.parseInt(msg.split(" ")[3]));
		}else {
		
			return "Please specify if you want to update either the raid roles, the raid message or the raid tier.";
		}
	}
		
	private String updateRoles(Raid raid, String msg) throws IOException{
		raid.setMaxTanks(Integer.parseInt(msg.split(" ")[3]));
		raid.setMaxHeals(Integer.parseInt(msg.split(" ")[4]));
		raid.setMaxMeleeDps(Integer.parseInt(msg.split(" ")[5]));
		raid.setMaxDps(Integer.parseInt(msg.split(" ")[6]));
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			
			return "Something happened when adding you to the raid, please try again later.";
		}
		
		
		return "Raid " + raid.getRaidName() + " has been update to " + raid.getMaxTanks() + " tanks, " + raid.getMaxHeals() + " healers, " + raid.getMaxMeleeDps() + " melee dps, and " + raid.getMaxDps() + " ranged dps";
		
	}
	
	private String updateMsg(Raid raid, String msg) throws IOException{
		//update raid message
//		String debug = msg.split(" ")[3];
//		System.out.println("debug: " + debug);
		String msgUpdate = msg.substring(msg.indexOf(':')+1);
		raid.setRaidMsg(msgUpdate);
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			
			return "Something happened when updating the raid, please try again later.";
		}
		//feedback
		return "Raid " + raid.getRaidName() + " now has the following message: " + msgUpdate;
	}
	
	private String updateTier(Raid raid, int tier) throws IOException{
		raid.setTier(tier);
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			
			return "Something happened when updating the raid, please try again later.";
		}
		return "Raid " + raid.getRaidName() + " now has the following tier: " + tier;
	}
	
	public String updateInfo(Member member,Guild guild,String msg) throws IOException{
		boolean isUpdate = false;
		String info = "";
		String[] paramsCheck = msg.split(" ");
		if(paramsCheck.length < 2) {
			return "You forgot to specify the raid you want to sign up for.";
		}
		if (!msg.contains("msg:")) {
			return "you forgot to include the info to update with.";
		}
		if(msg.contains("msg: ")) {
			System.out.println("----------------------\n info detected");
			info = msg.substring(msg.indexOf("msg:")+ 5);
			System.out.println(info);
			msg = msg.substring(0, msg.indexOf("msg:"));
			System.out.println(msg);
			System.out.println("info end\n------------------");
		}
		String raidName = paramsCheck[1].toLowerCase();
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
	
		Raider raider = new Raider(member.getUser().getId(),"");
		List<Raider> raiders = raid.getRaiders();

		for(Raider r: raiders) {
			if(r.getId().equals(raider.getId())) {
				isUpdate = true;
				r.setInfo(info);
				raid.setRaiders(raiders);
				break;
			}
		}
		
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			
			return "Something happened when adding you to the raid, please try again later.";
		}
		
		if (isUpdate) {
			return "You set the following as your info for that raid: " + info;
		}
		
		return "You havent even signed up for that raid.";
		
	}
	
	public String closeRaid(String msg) throws IOException{
		String raidName = msg.split(" ")[1];
		Raid closedRaid = new Raid(raidName);
		
		if(!raidRepository.raidCheck(closedRaid)) {
			return "Raid not found.";
		}
		closedRaid = raidRepository.readRaid(closedRaid);
		Attendance.getInstance().setRaiders(closedRaid.getStarters());
		System.out.println(Attendance.getInstance().getRaiders());
		if(!raidRepository.deleteRaid(closedRaid)) {
			return null;
		}
		return "The raid " + closedRaid.getRaidName() + " has been closed.";
	}
	
	public String cancelRaid(String msg) throws IOException{
		String raidName = msg.split(" ")[1];
		
		String anouncement = raidName + " has been cancelled, signups are now closed.  ";
		Raid closedRaid = new Raid(raidName);
		String[] raidMessage = msg.split("msg:");
		if (raidMessage.length > 1) {
			anouncement+= raidMessage[1];
		}
		anouncement+= "\n";
		if(!raidRepository.raidCheck(closedRaid)) {
			return "Raid not found.";
		}
		for(Raider r: closedRaid.getStarters()) {
			anouncement+=r.toRollCall();
		}
		closedRaid = raidRepository.readRaid(closedRaid);
		if(!raidRepository.deleteRaid(closedRaid)) {
			return null;
		}
		if (msg.split(" ")[2].equalsIgnoreCase("no")) {
			return "no anouncement";
		}
		return anouncement;
	}
	
	public String notifyRaid(String msg) throws IOException{
		String notify = "";
		if(msg.contains("msg: ")) {
			System.out.println("----------------------\n notify detected");
			notify = msg.substring(msg.indexOf("msg:")+ 5);
			System.out.println(notify);
			msg = msg.substring(0, msg.indexOf("msg:"));
			System.out.println(msg);
			System.out.println("notify end\n------------------");
		} else {
			return "you didnt include your message Binch!";
		}
		String[] paramsCheck = msg.split(" ");
		if(paramsCheck.length < 2) {
			return "You forgot to specify the raid you want to notify Binch!";
		}
		String raidName = paramsCheck[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Raid not found.";
		}
		raid = raidRepository.readRaid(raid);
		String notification = "Tagging everyone who signed up for " + raid.getRaidName() + ".  " + notify + "\n";
		for (Raider r: raid.getStarters()) {
			notification +=r.toRollCall();
		}
		if(raid.getOverflow().size() > 0) {
			notification+= "--------- \nOverflow: \n--------- \n";
			for(Raider r: raid.getOverflow()) {
				notification+=r.toRollCall();
			}
		}
		return notification;
		
		
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
				roster+=guild.getMemberById(r.getId()).getEffectiveName() + ", ";
			}
		}
		Attendance.getInstance().setRaidDay();
		return roster;
	}
	
	//=====================================================================================================================
	// GUILDIE COMMANDS
	//=====================================================================================================================

	public String signup(Member member,Guild guild, String msg) throws IOException{
		String[] paramsCheck = msg.split(" ");
		boolean isUpdate = false;
		boolean isOverflow = false;
		boolean isRanged = false;
		boolean meetsReq = false;
		String info = "";
		List<Role> roles = member.getRoles();
		if(msg.contains("msg: ")) {
			System.out.println("----------------------\n info detected");
			info = msg.substring(msg.indexOf("msg:")+ 5);
			System.out.println(info);
			msg = msg.substring(0, msg.indexOf("msg:"));
			System.out.println(msg);
			System.out.println("info end\n------------------");
		}
		if(paramsCheck.length < 2) {
			return "You forgot to specify the raid you want to sign up for.";
		}
		String raidName = paramsCheck[1].toLowerCase();
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
	
		Raider raider = new Raider(member.getUser().getId(),"");
		if (!info.isEmpty()) {
			raider.setInfo(info);
		}
		
		if (paramsCheck.length < 3) {
			if(getDefault(raider) != null) {
				raider.setRole(getDefault(raider));
				if(raid.getTier() ==1) {
					if(raider.getRole().equals(ROLE_HEALS)) {
						if(roles.contains(guild.getRoleById(HEAL_1)) || roles.contains(guild.getRoleById(HEAL_2))) {
							meetsReq = true;
						}
					}
					if(raider.getRole().equals(ROLE_TANK)) {
						if(roles.contains(guild.getRoleById(TANK_1)) || roles.contains(guild.getRoleById(TANK_2))) {
							meetsReq = true;
						}
					}
					if(raider.getRole().equals(ROLE_DPS)) {
						if(roles.contains(guild.getRoleById(DPS_1)) || roles.contains(guild.getRoleById(DPS_2))) {
							meetsReq = true;
						}
					}

					
				} else if (raid.getTier() == 2) {
					if(raider.getRole().equals(ROLE_HEALS)) {
						if(roles.contains(guild.getRoleById(HEAL_2))) {
							meetsReq = true;
						}
					}
					if(raider.getRole().equals(ROLE_TANK)) {
						if(roles.contains(guild.getRoleById(TANK_2))) {
							meetsReq = true;
						}
					}
					if(raider.getRole().equals(ROLE_DPS)) {
						if(roles.contains(guild.getRoleById(DPS_2))) {
							meetsReq = true;
						}
					}
				} else {
					meetsReq = true; //t0
				}
				
			} else {
				raider.setRole(ROLE_DPS);
				if(raid.getTier() ==1) {
					if(roles.contains(guild.getRoleById(DPS_1)) || roles.contains(guild.getRoleById(DPS_2))) {
						meetsReq = true;
					}
				} else if (raid.getTier() == 2 ) {
					if(roles.contains(guild.getRoleById(DPS_2))) {
						meetsReq = true;
					}
				} else {
					meetsReq = true; //t0?
				}
			}
		} else {
			if(msg.contains("heal") || msg.contains("Heal")|| msg.contains("heals") || msg.contains("Heals") || msg.contains("healer") || msg.contains(ROLE_HEALS) || msg.contains("babysitter")|| msg.contains("Babysitter")) {
				raider.setRole(ROLE_HEALS);
				if(raid.getTier() ==1) {
					if(roles.contains(guild.getRoleById(HEAL_1)) || roles.contains(guild.getRoleById(HEAL_2))) {
						meetsReq = true;
					}
				} else if (raid.getTier() == 2 ) {
					if(roles.contains(guild.getRoleById(HEAL_2))) {
						meetsReq = true;
					}
				} else {
					meetsReq = true; //t0?
				}
				
			} else if(msg.contains("tank") || msg.contains("Tank") || msg.contains("meatshield") || msg.contains("Meatshield")||  msg.contains("Tonk") || msg.contains("Tonk") || msg.contains(ROLE_TANK)) {
				raider.setRole(ROLE_TANK);
				
				if(raid.getTier() ==1) {
					if(roles.contains(guild.getRoleById(TANK_1)) || roles.contains(guild.getRoleById(TANK_2))) {
						meetsReq = true;
					}
				} else if (raid.getTier() == 2 ) {
					if(roles.contains(guild.getRoleById(TANK_2))) {
						meetsReq = true;
					}
				} else {
					meetsReq = true; //t0?
				}
			} else if (paramsCheck[2].equalsIgnoreCase("melee") || paramsCheck[2].equalsIgnoreCase("mdps") || paramsCheck[2].equalsIgnoreCase("mele")) {
				raider.setRole(ROLE_MELEE);
				
				if(raid.getTier() ==1) {
					if(roles.contains(guild.getRoleById(DPS_1)) || roles.contains(guild.getRoleById(DPS_2))) {
						meetsReq = true;
					}
				} else if (raid.getTier() == 2 ) {
					if(roles.contains(guild.getRoleById(DPS_2))) {
						meetsReq = true;
					}
				} else {
					meetsReq = true; //t0?
				}
			}else {
				raider.setRole(ROLE_DPS);
				isRanged = true;
				
				if(raid.getTier() ==1) {
					if(roles.contains(guild.getRoleById(DPS_1)) || roles.contains(guild.getRoleById(DPS_2))) {
						meetsReq = true;
					}
				} else if (raid.getTier() == 2 ) {
					if(roles.contains(guild.getRoleById(DPS_2))) {
						meetsReq = true;
					}
				} else {
					meetsReq = true; //t0?
				}
			}
		}
	
		if (!meetsReq) {
			return "Looks like you dont have the required role to signup for that raid, please contact an officer if you should have that role.";
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

		
		//check if overflow
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
		
		if( raider.getRole().equals(ROLE_MELEE) && isRanged) {
			message += " your role was changed to melee because ranged spots are full, this doesnt mean you need to bring a melee toon.";
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
	
	public String remove(Guild guild, String msg) throws IOException {
		String[] paramsCheck = msg.split(" ");
		boolean removed = false;
		List<String> names = new ArrayList();
		List<Member> membersToKick = new ArrayList();
		
		String name = paramsCheck[2];
		System.out.println("name to remove: " + name);
		/*for (int i = 2; i < paramsCheck.length; i++){
			name += " " + paramsCheck[i];
		}*/
		List<Member> members = guild.getMembers();
		String message = "Successfully removed the following people: ";
		if (paramsCheck.length < 3) {
			return "Specify both the raid and the person to be removed";
		} else {
			int found = 0;
			for(Member m: members) {
				System.out.println("member name: " + m.getEffectiveName().toLowerCase());
				System.out.println("name: " + name.toLowerCase());
				if(m.getEffectiveName().toLowerCase().contains(name.toLowerCase())) {
					names.add(m.getUser().getId());
					found++;
					if (found > 1) {
						return "Name wasnt specific enough, try again with more of their name.";
					}
				}
			}
		}
		System.out.println("found: " + names.toString());
		String raidName = paramsCheck[1];
		Raid raid = new Raid(raidName);
		if(!raidRepository.raidCheck(raid)) {
			return "Sorry, unable to find that raid";
		}
		raid = raidRepository.readRaid(raid);
		List<Raider> raiders = raid.getRaiders();
		for(int i = 0; i < names.size(); i++) {
			for(int j = 0; j < raiders.size(); j++) {
				if(raiders.get(j).getId().equals(names.get(i))) {
					raid.removeRaider(raiders.get(j));
					//raiders.remove(j);
					removed = true;
					message += guild.getMemberById(names.get(i)).getEffectiveName() + " ";
				}
			}
		}
		boolean isWritten = raidRepository.writeRaid(raid);
		if(!isWritten) {
			return "Something happened trying to refresh the raid roster, hopefully no data was lost....";
		}
		if (!removed) {
			message = "nobody with that name signed up for that raid dumbass.";
		}
		return message;
		
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
		String roster = raid.getRaidMsg() + "\nRoster:\n";
		if(raid.getRaiders().size() > 0) {
			for(Raider r: raid.getStarters()) {
				roster+=guild.getMemberById(r.getId()).getEffectiveName() + " " + r.getRole() + (r.getPosition() == null ? "": "(taking melee position)" )+ ((r.getInfo()== null || r.getInfo().equals("null") )?  "" : ("info: " + r.getInfo())) +"\n";
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
			roster += "| Melee: "+ + raid.getNumMeleeDps() + "/" + raid.getMaxMeleeDps() + " | Range: " + raid.getNumDps() + "/" + raid.getMaxDps() + "  |\n";
			roster += "+------------+--------------+\n";
			roster += "|     Total Raiders: "+ raid.getStarters().size() + "/" + (raid.getMaxDps() +raid.getMaxHeals()+raid.getMaxMeleeDps()+raid.getMaxTanks())+"      |\n" ;
			roster += "+------------+--------------+\n";
			return roster;
		} else {
			return "Nobody has signed up for this raid yet.";
		}
	}
	

	
	public String raidList() throws IOException{
		String pathName=RAID_FILE_PATH;
		File filePath = new File(RAID_FILE_PATH);
		ArrayList<String> raids = new ArrayList<String>(Arrays.asList(filePath.list()));
		String message = "The current available raids are: \n";
		boolean isRaids = raids.size()>0;
		if(!isRaids) {
			message += "There are no raids currently open. \n Please check the raid schedule, raids are generally opened ~24 hours before a scheduled raid.";
			message += msgRepository.readRaidSchedule();
		}
		for(int i = 0; i < raids.size(); i++) {
			message += raids.get(i).substring(0, raids.get(i).indexOf('.'));
			message += "\n";
		}
		return message;
	}
}
