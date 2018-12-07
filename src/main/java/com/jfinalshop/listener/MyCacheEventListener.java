package com.jfinalshop.listener;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.LogKit;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.util.HasorUtils;

public class MyCacheEventListener implements CacheEventListener  {

	private static final ArticleService articleService = HasorUtils.getBean(ArticleService.class);
	private static final ProductService productService = HasorUtils.getBean(ProductService.class);
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
	@Override
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
		String cacheName = cache.getName();
		LogKit.info(">>>>> 缓存【" + cacheName + "】移除 <<<<<");
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
		String cacheName = cache.getName();
		LogKit.info(">>>>> 缓存【" + cacheName + "】增加 <<<<<");
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
		String cacheName = cache.getName();
		LogKit.info(">>>>> 缓存【" + cacheName + "】更新 <<<<<");
	}

	@Override
	public void notifyElementExpired(Ehcache ehcache, Element element) {
		String cacheName = ehcache.getName();
		LogKit.info(">>>>> 缓存【" + cacheName + "】过期 <<<<<");
		if (StringUtils.equals(cacheName, Article.HITS_CACHE_NAME)) {
			Long id = (Long) element.getObjectKey();
			Long hits = (Long) element.getObjectValue();
			Article article = articleService.find(id);
			if (article != null && hits != null && hits > 0 && hits > article.getHits()) {
				article.setHits(hits);
				articleService.update(article);
			}
		} else if (StringUtils.equals(cacheName, Product.HITS_CACHE_NAME)) {
			Long id = (Long) element.getObjectKey();
			Long hits = (Long) element.getObjectValue();
			Product product = productService.find(id);
			if (product != null && hits != null && hits > 0) {
				long amount = hits - product.getHits();
				if (amount > 0) {
					productService.addHits(product, amount);
				}
			}
		}
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		String cacheName = cache.getName();
		LogKit.info(">>>>> 缓存【" + cacheName + "】驱逐 <<<<<");
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
		String cacheName = cache.getName();
		LogKit.info(">>>>> 缓存【" + cacheName + "】删除 <<<<<");
	}

	@Override
	public void dispose() {
		LogKit.info(">>>>> 缓存处置 <<<<<");
	}

}
