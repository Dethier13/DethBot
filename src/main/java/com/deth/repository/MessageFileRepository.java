package com.deth.repository;

import static com.deth.util.FinalUtilProperties.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MessageFileRepository {
private static final MessageFileRepository instance = new MessageFileRepository();
	
	private MessageFileRepository() {}
	
	public static MessageFileRepository getInstance() {
		return instance;
	}
	
	public String readGenHelp() throws IOException{
		String message = "";
		String fileName = GEN_HELP_FILE;
		String readBuffer = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		while((readBuffer = bufferedReader.readLine())!= null) {
			message += readBuffer+"\n";
		}
		return message;
	}
	
	public String readOHelp() throws IOException{
		String message = "";
		String fileName = O_HELP_FILE;
		String readBuffer = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		while((readBuffer = bufferedReader.readLine())!= null) {
			message += readBuffer+"\n";
		}
		return message;
	}
	
	public String readRules() throws IOException{
		String message = "";
		String fileName = RULES_MESSAGE;
		String readBuffer = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		while((readBuffer = bufferedReader.readLine())!= null) {
			message += readBuffer+"\n";
		}
		return message;
	}
	
	public String readRules2() throws IOException{
		String message = "";
		String fileName = RULES_MESSAGE2;
		String readBuffer = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		while((readBuffer = bufferedReader.readLine())!= null) {
			message += readBuffer+"\n";
		}
		return message;
	}
}
