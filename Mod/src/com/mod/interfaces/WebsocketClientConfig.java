package com.mod.interfaces;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.websocket.ClientEndpointConfig.Configurator;
import jakarta.websocket.HandshakeResponse;

import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;

public class WebsocketClientConfig extends Configurator {
	
	static volatile boolean called = false;

	private ConfigData configData = ApplicationHelper.Application_Config_Cache.get("app");
	
    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        called = true;
        headers.put("Accept-Encoding", Arrays.asList("gzip, deflate, br"));
        headers.put("Accept-Language", Arrays.asList("en-GB,en-US;q=0.9,en;q=0.8"));
        headers.put("Cache-Control", Arrays.asList("no-cache"));
        headers.put("Connection", Arrays.asList("Upgrade"));
        headers.put("Host", Arrays.asList("websocket.kite.trade"));
        headers.put("Origin", Arrays.asList("https://kite.zerodha.com"));
        headers.put("Pragma", Arrays.asList("no-cache"));
        headers.put("Sec-WebSocket-Extensions", Arrays.asList("permessage-deflate; client_max_window_bits"));
        
        //headers.put("Sec-WebSocket-Key", Arrays.asList("6fzWv4vmBRfkKoA4OgzCIA=="));
        System.out.println("Sec-WebSocket-Key:"+configData.getKeyValueConfigs().get("Sec-WebSocket-Key"));
        headers.put("Sec-WebSocket-Key", Arrays.asList(configData.getKeyValueConfigs().get("Sec-WebSocket-Key")));
        
        headers.put("Sec-WebSocket-Version", Arrays.asList("13"));
        headers.put("Upgrade", Arrays.asList("websocket"));
        System.out.println("User Agent:"+configData.getKeyValueConfigs().get("User-Agent"));
        headers.put("User-Agent", Arrays.asList(configData.getKeyValueConfigs().get("User-Agent")));
        
    }

    @Override
    public void afterResponse(HandshakeResponse handshakeResponse) {
        final Map<String, List<String>> headers = handshakeResponse.getHeaders();
        System.out.println(headers);
        
//        assertEquals(HEADER_VALUE[0], headers.get(HEADER_NAME).get(0));
//        assertEquals(HEADER_VALUE[1], headers.get(HEADER_NAME).get(1));
//        assertEquals(HEADER_VALUE[2], headers.get(HEADER_NAME).get(2));
//        assertEquals("myOrigin", headers.get("origin").get(0));
    }
    
    

}
