package com.jfinalshop.cfg;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.collect.Lists;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.model._MappingKit;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.security.MyJdbcAuthzService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.shiro.core.ShiroInterceptor;
import com.jfinalshop.shiro.core.ShiroPlugin;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.freemarker.ShiroTags;
import com.jfinalshop.template.directive.*;
import com.jfinalshop.template.method.AbbreviateMethod;
import com.jfinalshop.template.method.CurrencyMethod;
import com.jfinalshop.template.method.MessageMethod;
import com.jfinalshop.util.EncriptionKit;
import com.jfinalshop.util.HasorUtils;
import com.jfinalshop.util.SystemUtils;
import com.ld.zxw.config.LucenePlusConfig;
import com.ld.zxw.core.LucenePlugin;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StandardCompress;
import net.hasor.plugins.jfinal.HasorInterceptor;
import net.hasor.plugins.jfinal.HasorPlugin;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JFWebConfig extends JFinalConfig {

	/**
	 * 供Shiro插件使用。
	 */
	Routes routes;
	
	@Override
	public void configConstant(Constants constants) {
		PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
		constants.setDevMode(PropKit.getBoolean("devMode", false));
		constants.setViewType(ViewType.FREE_MARKER);
		constants.setI18nDefaultBaseName("i18n");
		constants.setI18nDefaultLocale("zh_CN");
		constants.setViewExtension(".ftl");
		constants.setError401View("/common/error/not_found.ftl");
		constants.setError403View("/common/error/unauthorized.ftl");
		constants.setError404View("/common/error/not_found.ftl");
		constants.setError500View("/common/error/not_found.ftl");
	}

	@Override
	public void configRoute(Routes routes) {
		this.routes = routes;
		
		AutoBindRoutes abr = new AutoBindRoutes();
		// 忽略不自动扫描的Controller
		List<Class<? extends Controller>> clazzes = new ArrayList<Class<? extends Controller>>();
		clazzes.add(com.jfinalshop.controller.admin.BaseController.class);
		clazzes.add(com.jfinalshop.controller.business.BaseController.class);
		clazzes.add(com.jfinalshop.controller.member.BaseController.class);
		clazzes.add(com.jfinalshop.api.controller.BaseAPIController.class);
		abr.addExcludeClasses(clazzes);
		routes.add(abr);
	}

	@Override
	public void configEngine(Engine me) {
		
	}

	@Override
	public void configPlugin(Plugins plugins) {
		String publicKey = StringUtils.trim(PropKit.get("jdbc.publicKey")); 
		String password = StringUtils.trim(PropKit.get("jdbc.password"));
		
		//配置druid连接池
		DruidPlugin druidDefault = new DruidPlugin(
		StringUtils.trim(PropKit.get("jdbc.url")), 
		StringUtils.trim(PropKit.get("jdbc.username")),
		EncriptionKit.passwordDecrypt(publicKey, password),
		//getProperty("jdbc.password"),
		StringUtils.trim(PropKit.get("jdbc.driver")));
		// StatFilter提供JDBC层的统计信息
		druidDefault.addFilter(new StatFilter());

		// WallFilter的功能是防御SQL注入攻击
		WallFilter wallDefault = new WallFilter();

		wallDefault.setDbType(JdbcConstants.MYSQL);
		druidDefault.addFilter(wallDefault);
		
		druidDefault.setInitialSize(PropKit.getInt("connection_pools.initial_pool_size"));
		druidDefault.setMaxPoolPreparedStatementPerConnectionSize(PropKit.getInt("connection_pools.max_pool_size"));
		druidDefault.setTimeBetweenConnectErrorMillis(PropKit.getInt("connection_pools.checkout_timeout"));
		plugins.add(druidDefault);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidDefault);
		plugins.add(arp);
		
		// 配置属性名(字段名)大小写不敏感容器工厂
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		
		// 显示SQL
		arp.setShowSql(true);
		
		// 所有配置在 MappingKit
		_MappingKit.mapping(arp);
		
		//Ehcache缓存
		plugins.add(new EhCachePlugin());
		
		//shiro权限框架，添加到plugin
		plugins.add(new ShiroPlugin(routes, new MyJdbcAuthzService()));
		
		//（必选）Hasor 框架的启动和销毁
		plugins.add(new HasorPlugin(JFinal.me()));

		// Lucene插件
		LucenePlugin lucenePlugin = new LucenePlugin();
		LucenePlusConfig luceneConfig = new LucenePlusConfig();
		// 默认开发模式 false  生产模式 为 true
		luceneConfig.setDevMode(false);
		// 词库目录 启用词
		//luceneConfig.setExtWordPath("");
		// 开启高亮 如果开启 高亮  必须设置高亮字段
		//luceneConfig.setHighlight(true);
		luceneConfig.setHighlightFields(Lists.newArrayList("keyword"));
		// 分词字段 设置后模糊查询
		luceneConfig.setParticipleField(Lists.newArrayList("keyword","name","caption","brand","seoTitle","seoKeywords"));
		// 索引目录
		luceneConfig.setLucenePath(PathKit.getWebRootPath() + "/lucene/");
		//添加 store 源
		lucenePlugin.putDataSource(luceneConfig, "store");
		//添加 article 源
		lucenePlugin.putDataSource(luceneConfig, "article");
		//添加 product 源
		lucenePlugin.putDataSource(luceneConfig, "product");
		plugins.add(lucenePlugin);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// 依赖注入
        me.add(new HasorInterceptor(JFinal.me()));
        // CSRF拦截器
        me.add(new CsrfInterceptor());
		// 添加shiro
        me.add(new ShiroInterceptor());
	}

	@Override
	public void configHandler(Handlers handlers) {
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid", new IDruidStatViewAuth() {
			@Override
			public boolean isPermitted(HttpServletRequest request) {
				if (SubjectKit.hasRoleAdmin()) {
					return true;
				} else {
					return false;
				}
			}
		});
		handlers.add(new UrlSkipHandler("/apipotal",false));
		handlers.add(new UrlSkipHandler("/loginUser",false));
		handlers.add(new UrlSkipHandler("/registerUser",false));
		handlers.add(dvh);
	}

	@Override
	public void afterJFinalStart() {
		try {
			Configuration cfg = FreeMarkerRender.getConfiguration();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("base", JFinal.me().getContextPath());
			map.put("showPowered", false);
			map.put("setting", SystemUtils.getSetting());
			map.put("message", new MessageMethod());
			map.put("abbreviate", new AbbreviateMethod());
			map.put("currency", new CurrencyMethod());
			map.put("flash_message", new FlashMessageDirective());
			map.put("pagination", new PaginationDirective());
			map.put("seo", new SeoDirective());
			map.put("ad_position", new AdPositionDirective());
			map.put("member_attribute_list", new MemberAttributeListDirective());
			map.put("business_attribute_list", new BusinessAttributeListDirective());
			map.put("navigation_list", new NavigationListDirective());
			map.put("friend_link_list", new FriendLinkListDirective());
			map.put("brand_list", new BrandListDirective());
			map.put("attribute_list", new AttributeListDirective());
			map.put("article_list", new ArticleListDirective());
			map.put("article_category_root_list", new ArticleCategoryRootListDirective());
			map.put("article_category_parent_list", new ArticleCategoryParentListDirective());
			map.put("article_category_children_list", new ArticleCategoryChildrenListDirective());
			map.put("article_tag_list", new ArticleTagListDirective());
			map.put("product_list", new ProductListDirective());
			map.put("product_count", new ProductCountDirective());
			map.put("product_category_root_list", new ProductCategoryRootListDirective());
			map.put("product_category_parent_list", new ProductCategoryParentListDirective());
			map.put("product_category_children_list", new ProductCategoryChildrenListDirective());
			map.put("store_product_category_root_list", new StoreProductCategoryRootListDirective());
			map.put("store_product_category_parent_list", new StoreProductCategoryParentListDirective());
			map.put("store_product_category_children_list", new StoreProductCategoryChildrenListDirective());
			map.put("product_tag_list", new ProductTagListDirective());
			map.put("product_favorite", new ProductFavoriteDirective());
			map.put("store_favorite", new StoreFavoriteDirective());
			map.put("review_list", new ReviewListDirective());
			map.put("consultation_list", new ConsultationListDirective());
			map.put("promotion_list", new PromotionListDirective());
			map.put("promotion_plugin", new PromotionPluginDirective());
			map.put("order_count", new OrderCountDirective());
			map.put("compress", StandardCompress.INSTANCE);
			cfg.setSharedVaribles(map);
			
			cfg.setDefaultEncoding(StringUtils.trim(PropKit.get("template.encoding")));
			cfg.setURLEscapingCharset(StringUtils.trim(PropKit.get("url_escaping_charset")));
			cfg.setTemplateUpdateDelayMilliseconds(PropKit.getLong(("template.update_delay")));
			cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
			cfg.setClassicCompatible(true);
			cfg.setNumberFormat(StringUtils.trim(PropKit.get("template.number_format")));
			cfg.setBooleanFormat(StringUtils.trim(PropKit.get("template.boolean_format")));
			cfg.setDateTimeFormat(StringUtils.trim(PropKit.get("template.datetime_format")));
			cfg.setDateFormat(StringUtils.trim(PropKit.get("template.date_format")));
			cfg.setTimeFormat(StringUtils.trim(PropKit.get("template.time_format")));
			cfg.setObjectWrapper(new BeansWrapper(Configuration.VERSION_2_3_26));
			cfg.setWhitespaceStripping(true);
			cfg.setSharedVariable("shiro", new ShiroTags());
			cfg.setServletContextForTemplateLoading(JFinal.me().getServletContext(), StringUtils.trim(PropKit.get("template.loader_path")));
			ApiConfigKit.putApiConfig(getApiConfig());
			super.afterJFinalStart();
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
	}
	
	public ApiConfig getApiConfig() {
		ApiConfig apiConfig = new ApiConfig();
		// 配置微信 API 相关常量
		PluginService pluginService = HasorUtils.getBean(PluginService.class);
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("weixinPublicPaymentPlugin");
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		apiConfig.setToken(pluginConfig.getAttribute("mchId"));
		apiConfig.setAppId(pluginConfig.getAttribute("appId"));
		apiConfig.setAppSecret(pluginConfig.getAttribute("appSecret"));
		apiConfig.setEncryptMessage(false);
		return apiConfig;
	}
	

}
