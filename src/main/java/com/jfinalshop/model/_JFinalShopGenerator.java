package com.jfinalshop.model;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _JFinalShopGenerator {
	
	public static DataSource getDataSource() {
		String jdbcUrl = "jdbc:mysql://192.168.9.43:3306/jfinalshop-b2b2c?useUnicode=true&characterEncoding=UTF-8";
		String user = "root";
		String password = "278421";
		DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, user, password);
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.jfinalshop.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = "/Users/lihongyuan/GitHub/jfinalshop-b2b2c/src/main/java/com/jfinalshop/model/base";
		
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.jfinalshop.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 添加不需要生成的表名
		gernerator.addExcludedTable("order_coupon");
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		//gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gernerator.generate();
	}
}




