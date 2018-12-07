package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.PluginConfigDao;
import com.jfinalshop.model.PluginConfig;

/**
 * Service - 插件配置
 * 
 */
@Singleton
public class PluginConfigService extends BaseService<PluginConfig> {

	/**
	 * 构造方法
	 */
	public PluginConfigService() {
		super(PluginConfig.class);
	}
	
	@Inject
	private PluginConfigDao pluginConfigDao;
	
	/**
	 * 判断插件ID是否存在
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件ID是否存在
	 */
	public boolean pluginIdExists(String pluginId) {
		return pluginConfigDao.exists("plugin_id", pluginId);
	}

	/**
	 * 根据插件ID查找插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件配置，若不存在则返回null
	 */
	public PluginConfig findByPluginId(String pluginId) {
		return pluginConfigDao.find("plugin_id", pluginId);
	}

	/**
	 * 根据插件ID删除插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 */
	public void deleteByPluginId(String pluginId) {
		PluginConfig pluginConfig = findByPluginId(pluginId);
		pluginConfigDao.remove(pluginConfig);
	}

	@Override
	public PluginConfig save(PluginConfig pluginConfig) {
		return super.save(pluginConfig);
	}
	
	@Override
	public PluginConfig update(PluginConfig pluginConfig) {
		return super.update(pluginConfig);
	}
	
	@Override
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Override
	public void delete(Long... ids) {
		super.delete(ids);
	}
	
	@Override
	public void delete(PluginConfig pluginConfig) {
		super.delete(pluginConfig);
	}
	
}