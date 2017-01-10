package com.ge.predix.solsvc.dataingestion.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;


/**
 * 
 * @author 212421693
 *
 */
public abstract class BaseFactory {
	@SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(BaseFactory.class);
	
	
	/**
	 * 
	 */
	@Autowired
	protected RestClient restClient;

	/**
	 * 
	 */
	@Autowired
	protected AssetConfig assetConfig;

	

}
