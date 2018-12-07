package com.jfinalshop.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseAdPosition;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.template.TemplateException;

/**
 * Model - 广告位
 * 
 */
public class AdPosition extends BaseAdPosition<AdPosition> {
	private static final long serialVersionUID = 6838748335797699769L;
	public static final AdPosition dao = new AdPosition().dao();
	
	/**
	 * 广告
	 */
	private List<Ad> ads = new ArrayList<Ad>();

	/**
	 * 获取广告
	 * 
	 * @return 广告
	 */
	public List<Ad> getAds() {
		if (CollectionUtils.isEmpty(ads)) {
			String sql = "SELECT * FROM ad WHERE `ad_position_id` = ? AND begin_date <= NOW() AND end_date >= NOW() ORDER BY orders ASC";
			ads = Ad.dao.find(sql, getId());
		}
		return ads;
	}

	/**
	 * 设置广告
	 * 
	 * @param ads
	 *            广告
	 */
	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}

	/**
	 * 解析模板
	 * 
	 * @return 内容
	 */
	public String resolveTemplate() {
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("adPosition", this);
			return FreeMarkerUtils.process(getTemplate(), model);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}
	
	
}
