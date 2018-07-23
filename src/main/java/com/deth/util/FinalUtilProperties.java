package com.deth.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class FinalUtilProperties {
	
	/**
	 * Used to access specific discord server
	 */
	public static final String TOKEN;

	/**
	 * Determines which commands can be used in which channel
	 */
	public static final String SIGNUP_CHAN_ID;
	public static final String O_CHAN_ID;
	public static final String BOT_CHAN_ID;
	
	/**
	 * Discord Roles for attendance features
	 * used for promoting from raids and exempting from attendance.
	 */
	public static final String SKEEVER;
	public static final String ZOMBIE;
	public static final String CORE;
	public static final String OFFICER;
	public static final String GM;
	public static final String[] EXEMPT;
	
	/**
	 * activity req 'settings'
	 */
	public static final String INITIAL_ACTIVITY_REQ;
	public static final String ACTIVITY_REQ;
	
	/**
	 * Uses for displaying Raid roles
	 */
	public static final String ROLE_TANK;
	public static final String ROLE_MELEE;
	public static final String ROLE_DPS;
	public static final String ROLE_HEALS;
	
	/**
	 * paths to local files.
	 */
	public static final String RAID_FILE_PATH = "resources/raids/";
	public static final String DEFAULTS_FILE_PATH = "resources/defaults.txt";
	public static final String RAID_ACTIVITY = "resources/attendance/raidActivity.txt";
	public static final String LEAVE_OF_ABSCENCE = "resources/attendance/leaveOfAbscence.txt";
	
	public static final String GEN_HELP_FILE = "resources/messages/generalHelp.txt";
	public static final String O_HELP_FILE = "resources/messages/officerHelp.txt";
	public static final String ATTENDANCE_HELP = "resources/messages/attendanceHelp.txt";
	
	public static final String RULES_MESSAGE = "resources/messages/rules.txt";
	public static final String RULES_MESSAGE2 = "resources/messages/rules2.txt";
	public static final String RAID_SCHEDULE = "resources/messages/raidSchedule.txt";
	
	

	
	static {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("resources/config.properties");
			prop.load(input);
		} catch (IOException ioe) {
			System.out.println("error loading config properties.");
		}
		TOKEN = prop.getProperty("TOKEN");
		SIGNUP_CHAN_ID = prop.getProperty("SIGNUP_CHAN_ID");
		O_CHAN_ID = prop.getProperty("O_CHAN_ID");
		BOT_CHAN_ID = prop.getProperty("BOT_CHAN_ID");
		SKEEVER = prop.getProperty("SKEEVER");
		ZOMBIE = prop.getProperty("ZOMBIE");
		CORE = prop.getProperty("CORE");
		OFFICER = prop.getProperty("OFFICER");
		GM = prop.getProperty("GM");
		EXEMPT = prop.getProperty("exempt").split(",");
		INITIAL_ACTIVITY_REQ = prop.getProperty("INITIAL_ACTIVITY_REQ");
		ACTIVITY_REQ = prop.getProperty("ACTIVITY_REQ");
		ROLE_TANK = prop.getProperty("ROLE_TANK");
		ROLE_MELEE = prop.getProperty("ROLE_MELEE");
		ROLE_DPS = prop.getProperty("ROLE_DPS");
		ROLE_HEALS = prop.getProperty("ROLE_HEALS");
	}
}

