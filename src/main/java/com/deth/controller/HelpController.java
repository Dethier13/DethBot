package com.deth.controller;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.deth.util.FinalUtilChannels.*;

public class HelpController extends ControllerCommandsInfo{
	public void raidHelp(MessageReceivedEvent event) {
		getBasicInfo(event);
		if(channel.getId().equals(O_CHAN_ID)) {
			officerHelp();
		} else {
			signupHelp();
		}
	}
	//TODO: probably make into text doc for easier readability when updating?
	private void signupHelp() {
		String help = "Available Commands are: \n";
		//SU
		help += "!su [Name of raid you want to join] [Role you want]\n-Use this to sign up for a current open raid (you can check this via '!raidlist' \n-If you dont specify a role, you will be defaulted to dps.\n";
		help+="-Unless you have already set your default role.\n";
		help+="-if youve already signed up for a raid, you can use this to change your role as well.\n";
		//default
		help += "!default [role] will save your default role to the bot, so you dont need to specify it every time";
		//Withdraw
		help+="!withdraw [Name of raid to withdraw from]\n-Use this to withdraw from a raid you previousely signed up for.\n-It is appreciated if you include a reason ;)\n";
		//status
		help+="!status [Name of raid you want to check]\n-Lists the current roster for the specified raid.";
		//raidlist
		help+="!raidlist\n-Lists any available raids.";
	
		channel.sendMessage(help).queue();
	}
	
	private void officerHelp() {
		String help = "Available Officer commads are: \n";
		//open
		help+="!open [name of raid] [#tanks] [#heals] [#dps] msg: [custom message to display].\n-Opens new raid and makes anouncement in signup channel.\n-if you do not follow the order correctly the anouncement message will have terrible grammer.\n";
		help+="-the custom msg is detected by the 'msg: ' tag, if that isnt there there will be no custom message.\n";
		help+="-the #'s for each role are required, as they are used to determine overflow";
		//close
		help+="!close [name of raid]\n-used to close raid and delete file used.\n";
		//rc
		help+="!rc [name of raid] [custom Ai String] \n-calls all people that signed up for the raid.\n-If you include a custom message, it'll display the custom AI in the string, if not it defaults to 'blood'.\n";
		channel.sendMessage(help).queue();
	}
}
