package com.deth;

import static com.deth.util.FinalUtilProperties.*;

import javax.security.auth.login.LoginException;

import com.deth.controller.CommandController;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class DethDriver {

public static JDA jda;
	
	public static void main(String[] args) {
		try {
			JDA jda = new JDABuilder(AccountType.BOT)
					.setToken(TOKEN)
					.addEventListener(new CommandController())
					.buildBlocking();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
