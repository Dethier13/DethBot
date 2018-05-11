package com.deth.service;

import java.io.IOException;

import com.deth.repository.MessageFileRepository;


public class MessageService {

	private static final MessageService instance = new MessageService();
	private MessageFileRepository msgRepository = MessageFileRepository.getInstance();
	private MessageService() {}
	
	public static MessageService getInstance() {
		return instance;
	}
	
	public String gRaidHelp() throws IOException{
		return msgRepository.readGenHelp();
	}
	
	public String oRaidHelp() throws IOException{
		return msgRepository.readOHelp();
	}
	
	public String rules() throws IOException{
		return msgRepository.readRules();
	}
	
	public String rules2() throws IOException{
		return msgRepository.readRules2();
	}
	
	public String schedule() throws IOException {
		return msgRepository.readRaidSchedule();
	}
}
