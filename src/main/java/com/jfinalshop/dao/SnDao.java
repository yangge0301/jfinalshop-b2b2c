package com.jfinalshop.dao;

import java.io.IOException;

import net.hasor.core.Init;
import net.hasor.core.InjectSettings;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 序列号
 * 
 */
public class SnDao extends BaseDao<Sn> {
	
	/**
	 * 构造方法
	 */
	public SnDao() {
		super(Sn.class);
	}
	
	/**
	 * 商品编号生成器
	 */
	private HiloOptimizer productHiloOptimizer;

	/**
	 * 订单编号生成器
	 */
	private HiloOptimizer orderHiloOptimizer;

	/**
	 * 订单支付编号生成器
	 */
	private HiloOptimizer orderPaymentHiloOptimizer;

	/**
	 * 订单退款编号生成器
	 */
	private HiloOptimizer orderRefundsHiloOptimizer;

	/**
	 * 订单发货编号生成器
	 */
	private HiloOptimizer orderShippingHiloOptimizer;

	/**
	 * 订单退货编号生成器
	 */
	private HiloOptimizer orderReturnsHiloOptimizer;

	/**
	 * 支付事务编号生成器
	 */
	private HiloOptimizer paymentTransactionHiloOptimizer;

	/**
	 * 平台服务编号生成器
	 */
	private HiloOptimizer platformServiceHiloOptimizer;
	
	@InjectSettings("${sn.product.prefix}")
	private String productPrefix;
	
	@InjectSettings("${sn.product.maxLo}")
	private int productMaxLo;
	
	@InjectSettings("${sn.order.prefix}")
	private String orderPrefix;
	
	@InjectSettings("${sn.order.maxLo}")
	private int orderMaxLo;
	
	@InjectSettings("${sn.orderPayment.prefix}")
	private String orderPaymentPrefix;
	
	@InjectSettings("${sn.orderPayment.maxLo}")
	private int orderPaymentMaxLo;
	
	@InjectSettings("${sn.orderRefunds.prefix}")
	private String orderRefundsPrefix;
	
	@InjectSettings("${sn.orderRefunds.maxLo}")
	private int orderRefundsMaxLo;
	
	@InjectSettings("${sn.orderShipping.prefix}")
	private String orderShippingPrefix;
	
	@InjectSettings("${sn.orderShipping.maxLo}")
	private int orderShippingMaxLo;
	
	@InjectSettings("${sn.orderReturns.prefix}")
	private String orderReturnsPrefix;
	
	@InjectSettings("${sn.orderReturns.maxLo}")
	private int orderReturnsMaxLo;
	
	@InjectSettings("${sn.paymentTransaction.prefix}")
	private String paymentTransactionPrefix;
	
	@InjectSettings("${sn.paymentTransaction.maxLo}")
	private int paymentTransactionMaxLo;
	
	@InjectSettings("${sn.platformService.prefix}")
	private String platformServicePrefix;
	
	@InjectSettings("${sn.platformService.maxLo}")
	private int platformServiceMaxLo;
	
	/**
	 * 初始化
	 */
	@Init
	public void afterPropertiesSet() throws Exception {
		productHiloOptimizer = new HiloOptimizer(Sn.Type.product, productPrefix, productMaxLo);
		orderHiloOptimizer = new HiloOptimizer(Sn.Type.order, orderPrefix, orderMaxLo);
		orderPaymentHiloOptimizer = new HiloOptimizer(Sn.Type.orderPayment, orderPaymentPrefix, orderPaymentMaxLo);
		orderRefundsHiloOptimizer = new HiloOptimizer(Sn.Type.orderRefunds, orderRefundsPrefix, orderRefundsMaxLo);
		orderShippingHiloOptimizer = new HiloOptimizer(Sn.Type.orderShipping, orderShippingPrefix, orderShippingMaxLo);
		orderReturnsHiloOptimizer = new HiloOptimizer(Sn.Type.orderReturns, orderReturnsPrefix, orderReturnsMaxLo);
		paymentTransactionHiloOptimizer = new HiloOptimizer(Sn.Type.paymentTransaction, paymentTransactionPrefix, paymentTransactionMaxLo);
		platformServiceHiloOptimizer = new HiloOptimizer(Sn.Type.platformService, platformServicePrefix, platformServiceMaxLo);
	}
	
	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	public String generate(Sn.Type type) {
		Assert.notNull(type);

		switch (type) {
		case product:
			return productHiloOptimizer.generate();
		case order:
			return orderHiloOptimizer.generate();
		case orderPayment:
			return orderPaymentHiloOptimizer.generate();
		case orderRefunds:
			return orderRefundsHiloOptimizer.generate();
		case orderShipping:
			return orderShippingHiloOptimizer.generate();
		case orderReturns:
			return orderReturnsHiloOptimizer.generate();
		case paymentTransaction:
			return paymentTransactionHiloOptimizer.generate();
		case platformService:
			return platformServiceHiloOptimizer.generate();
		default:
			break;
		}
		return null;
	}
	
	/**
	 * 获取末值
	 * 
	 * @param type
	 *            类型
	 * @return 末值
	 */
	private long getLastValue(Sn.Type type) {
		String sql = "SELECT * FROM sn WHERE type = ?";
		Sn sn = modelManager.findFirst(sql, type.ordinal());
		long lastValue = sn.getLastValue();
		String updateSql = "UPDATE sn SET last_value = ? WHERE type = ? AND last_value = ?";
		int result = Db.update(updateSql, lastValue + 1, type.ordinal(), lastValue);
		return 0 < result ? lastValue : getLastValue(type);
	}

	/**
	 * 高低位算法生成器
	 */
	private class HiloOptimizer {

		/**
		 * 类型
		 */
		private Sn.Type type;

		/**
		 * 前缀
		 */
		private String prefix;

		/**
		 * 最大低位值
		 */
		private int maxLo;

		/**
		 * 低位值
		 */
		private int lo;

		/**
		 * 高位值
		 */
		private long hi;

		/**
		 * 末值
		 */
		private long lastValue;

		/**
		 * 构造方法
		 * 
		 * @param type
		 *            类型
		 * @param prefix
		 *            前缀
		 * @param maxLo
		 *            最大低位值
		 */
		HiloOptimizer(Sn.Type type, String prefix, int maxLo) {
			this.type = type;
			this.prefix = prefix != null ? prefix.replace("{", "${") : "";
			this.maxLo = maxLo;
			this.lo = maxLo + 1;
		}

		/**
		 * 生成序列号
		 * 
		 * @return 序列号
		 */
		public synchronized String generate() {
			if (lo > maxLo) {
				lastValue = getLastValue(type);
				lo = lastValue == 0 ? 1 : 0;
				hi = lastValue * (maxLo + 1);
			}
			try {
				return FreeMarkerUtils.process(prefix) + (hi + lo++);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (TemplateException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

}