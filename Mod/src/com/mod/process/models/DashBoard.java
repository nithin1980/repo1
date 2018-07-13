package com.mod.process.models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.objects.GroupPosition;
import com.mod.web.KiteProcess;


public class DashBoard {

	public double overall;
	
	public static Map<String, GroupPosition> positionMap = new HashMap<String, GroupPosition>();
	
	public static KiteGeneralWebSocketClient kiteWebSocketClient;
	
	public static final KiteProcess kiteProcess = new KiteProcess();
	
	public static int allowedSeconds;
	
	public static String checkedTime; 
	
	public static long lastRecordTime=0;
	
	
	public DashBoard() {
		System.out.println("Enable status check..");
		ScheduledExecutorService threadService = Executors.newSingleThreadScheduledExecutor();
		Runnable command = new Runnable() {
			@Override
			public void run() {
				 
				if(lastRecordTime>0 && ((System.currentTimeMillis()-lastRecordTime)>29000)){
					System.out.println("Connection check failed. Re-connecting...");
					if(kiteWebSocketClient!=null){
						kiteWebSocketClient.connect();
					}
				}
			}
		};
		threadService.scheduleAtFixedRate(command, 5, 15, TimeUnit.SECONDS);
		
	}

	public Map<String, GroupPosition> getPositionMap() {
		return positionMap;
	}
	
	public static void setKiteGenerlWebSocketClient(KiteGeneralWebSocketClient client){
		kiteWebSocketClient = client;
	}
	
}
