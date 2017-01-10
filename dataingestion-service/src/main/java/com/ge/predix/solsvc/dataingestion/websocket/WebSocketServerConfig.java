/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.dataingestion.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.websocket.config.DefaultWebSocketConfigForTimeseries;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

/**
 * 
 * @author predix -
 */
@Component("webSocketServerConfig")
public class WebSocketServerConfig extends DefaultWebSocketConfigForTimeseries implements IWebSocketConfig
{
    @Override
    /**
     * @param wsUri the wsUri to set
     */
    @Value("${predix.websocket.server.uri}")
    public void setWsUri(String wsUri)
    {
        super.setWsUri(wsUri);
    }
    


}