package com.ge.predix.solsvc.dataingestion.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.asset.TagDatasource;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetFactory;

/**
 * 
 * @author predix -
 */
@Component
public class AssetDataHandler extends BaseFactory {
	private static Logger log = Logger.getLogger(AssetDataHandler.class);
	@Autowired
	private AssetFactory assetFactory;

	/**
	 * @return -
	 */
	public List<Asset> getAllAssets() {
		List<Header> headers = this.restClient.getOauthHttpHeaders();
		List<Asset> assets = this.assetFactory.getAllAssets(headers);
		/*
		 * if (assets != null) { for (Asset asset:assets) {
		 * log.info("Asset Name 				: "+asset.getAssetId());
		 * log.info("Asset Uri 				: "+asset.getUri());
		 * log.info("Asset Specification 	: "+asset.getSpecification()+"\n");
		 * } }
		 */
		return assets;
	}

	/**
	 * @param uuid
	 *            -
	 * @param filter
	 *            -
	 * @param value
	 *            -
	 * @param authorization
	 *            -
	 * @return -
	 */
	public Asset retrieveAsset(String uuid, String filter, String value, String authorization) {
		List<Header> headers = null;
		if (StringUtils.isEmpty(authorization)) {
			headers = this.restClient.getSecureTokenForClientId();
		} else {
			headers = new ArrayList<Header>();
			this.restClient.addSecureTokenToHeaders(headers, authorization);
		}
		this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

		List<Asset> assets = this.assetFactory.getAssetsByFilter(uuid, filter, value, headers);
		if (assets != null && assets.size() > 0) {
			Asset asset = assets.get(0);

			/*
			 * log.info("Asset Name 				: "+asset.getAssetId());
			 * log.info("Asset Uri 				: "+asset.getUri());
			 * log.info("Asset Specification 	: "+asset.getSpecification()+
			 * "\n");
			 */
			return asset;
		}
		return null;
	}

	/**
	 * Lookup the Asset based on a Filter = Value and in the Asset find the
	 * matching Predix Machine TagDatasource Node name.
	 * 
	 * @param filter
	 *            -
	 * @param value
	 *            -
	 * @param nodeName
	 *            -
	 * @param authorization
	 *            -
	 * @return -
	 */
	@SuppressWarnings("nls")
	public Map<String, AssetTag> getTimeSeriesTag(String filter, String value, String nodeName, String authorization) {
		Map<String, AssetTag> ret = new HashMap<String, AssetTag>(1);
		Asset asset = retrieveAsset(null, filter, value, authorization);
		if (asset != null) {
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, AssetTag> tags = asset.getAssetTag();
			if (tags != null) {
				for (Entry<String, AssetTag> entry : tags.entrySet()) {
					AssetTag assetTag = entry.getValue();
					TagDatasource dataSource = assetTag.getTagDatasource();
					if (dataSource != null && !dataSource.getNodeName().isEmpty()
							&& dataSource.getNodeName().equals(nodeName)) {
						ret.put(entry.getKey(), assetTag);
						return ret;
					}
				}
				log.warn("2. asset has no assetTags with matching nodeName, unable to find filter=" + filter + " = "
						+ value + " nodeName=" + nodeName + " authorization=" + authorization);
			} else
				log.warn("3. asset has no tags, unable to find filter=" + filter + " = " + value + " nodeName="
						+ nodeName + " authorization=" + authorization);

		}
		return ret;
	}
}
