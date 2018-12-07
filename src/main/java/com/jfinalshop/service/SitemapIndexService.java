package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.entity.SitemapIndex;
import com.jfinalshop.entity.SitemapUrl;
import com.jfinalshop.util.Assert;

/**
 * Service - Sitemap索引
 * 
 */
@Singleton
public class SitemapIndexService {

	@Inject
	private SitemapUrlService sitemapUrlService;
	
	/**
	 * 生成Sitemap索引
	 * 
	 * @param type
	 *            类型
	 * @param maxSitemapUrlSize
	 *            最大Sitemap URL数量
	 * @return Sitemap索引
	 */
	public List<SitemapIndex> generate(SitemapUrl.Type type, int maxSitemapUrlSize) {
		Assert.notNull(type);
		Assert.state(maxSitemapUrlSize >= 0);

		List<SitemapIndex> sitemapIndexs = new ArrayList<>();
		Long sitemapUrlSize = sitemapUrlService.count(type);
		for (int i = 0; i < Math.ceil((double) sitemapUrlSize / (double) maxSitemapUrlSize); i++) {
			SitemapIndex sitemapIndex = new SitemapIndex();
			sitemapIndex.setType(type);
			sitemapIndex.setIndex(i);
			sitemapIndexs.add(sitemapIndex);
		}
		return sitemapIndexs;
	}

}