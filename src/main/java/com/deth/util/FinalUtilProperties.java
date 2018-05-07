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
	 * Uses for displaying roles
	 */
	public static final String ROLE_TANK;
	public static final String ROLE_DPS;
	public static final String ROLE_HEALS;
	
	/**
	 * paths to local files.
	 */
	public static final String RAID_FILE_PATH = "resources/raids/";
	public static final String DEFAULTS_FILE_PATH = "resources/defaults.txt";
	
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
		ROLE_TANK = prop.getProperty("ROLE_TANK");
		ROLE_DPS = prop.getProperty("ROLE_DPS");
		ROLE_HEALS = prop.getProperty("ROLE_HEALS");
	}
}

