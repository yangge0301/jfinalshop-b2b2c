package com.jfinalshop.api.controller;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.Require;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.shiro.core.ShiroInterceptor;
import com.jfinalshop.util.EnumUtils;
import com.jfinalshop.util.SystemUtils;
/**
 * 
 * 移动API - Base
 *
 */
@Clear({CsrfInterceptor.class, ShiroInterceptor.class})
public class BaseAPIController extends Controller {

	/** 返回图片地址 */
	protected Setting setting = SystemUtils.getSetting();
	
	protected Res res = I18n.use();
	
    /**
     * 获取当前用户对象
     * @return
     */
    protected Member getMember() {
    	String token = getPara("token");
        if (StringUtils.isNotEmpty(token)) {
            return TokenManager.getMe().validate(token);
        }
        return null;
    }

    /**
	 * 枚举类型转换
	 * 
	 */
	public <T> T getParaEnum(Class<T> clazz, String value) {
		return (T) EnumUtils.convert(clazz, value);
	}
	
    /**
     * 响应接口不存在*
     */
    public void render404() {
        renderJson(new BaseResponse(Code.NOT_FOUND));      
    }

    /**
     * 响应请求参数有误*
     * @param message 错误信息
     */
    public void renderArgumentError(String message) {
        renderJson(new BaseResponse(Code.ARGUMENT_ERROR, message));
    }

    /**
     * 响应数组类型*
     * @param list 结果集合
     */
    public void renderDataResponse(List<?> list) {
        DataResponse resp = new DataResponse();
        resp.setData(list);
        if (CollectionUtils.isEmpty(list)) {
            resp.setMessage("未查询到数据");
        } else {
            resp.setMessage("success");
        }
        renderJson(resp);
    }

    /**
     * 响应操作成功*
     * @param message 响应信息
     */
    public void renderSuccess(String message) {
        renderJson(new BaseResponse().setMessage(message));        
    }

    /**
     * 响应操作失败*
     * @param message 响应信息
     */
    public void renderFailed(String message) {
        renderJson(new BaseResponse(Code.FAIL, message));    
    }
    
    /**
     * 判断参数值是否为空
     * @param rules
     * @return
     */
    public boolean notNull(Require rules) {
        if (rules == null || rules.getLength() < 1) {
            return true;
        }

        for (int i = 0, total = rules.getLength(); i < total; i++) {
            Object key = rules.get(i);
            String message = rules.getMessage(i);
            BaseResponse response = new BaseResponse(Code.ARGUMENT_ERROR);
            
            if (key == null) {
                renderJson(response.setMessage(message));
                return false;
            }

            if (key instanceof String && StringUtils.isEmpty((String) key)) {
                renderJson(response.setMessage(message));
                return false;
            }

            if (key instanceof Array) {
                Object[] arr = (Object[]) key;

                if (arr.length < 1) {
                    renderJson(response.setMessage(message));
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 判断请求类型是否相同*
     * @param name
     * @return
     */
    protected boolean methodType(String name) {
        return getRequest().getMethod().equalsIgnoreCase(name);
    }
    
    /**
	 * 货币格式化
	 * 
	 * @param amount
	 *            金额
	 * @param showSign
	 *            显示标志
	 * @param showUnit
	 *            显示单位
	 * @return 货币格式化
	 */
	protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
		Setting setting = SystemUtils.getSetting();
		String price = setting.setScale(amount).toString();
		if (showSign) {
			price = setting.getCurrencySign() + price;
		}
		if (showUnit) {
			price += setting.getCurrencyUnit();
		}
		return price;
	}
	
	
	/**
	 * 转换成前端所需的商品
	 * 
	 */
	protected List<Product> convertProduct(List<Product> products) {
		if (CollectionUtils.isNotEmpty(products)) {
			for(Product product : products) {
				Product pProduct = new Product();
				Sku defaultSku = product.getDefaultSku();
				pProduct.setId(defaultSku.getId());
				pProduct.setName(product.getName());
				pProduct.setImage(product.getImage());
				pProduct.setPrice(new BigDecimal(currency(product.getPrice(), false, false)));
				pProduct.setUnit(product.getUnit());
				pProduct.setWeight(product.getWeight());
				
				// 处理促销
				List<Promotion> promotions = new ArrayList<>();
				Set<Promotion> promotionSet = product.getValidPromotions();
				if (CollectionUtils.isNotEmpty(promotionSet)) {
					Promotion promotion = null;
					for (Iterator<Promotion> iterator = promotionSet.iterator(); iterator.hasNext();) {
						Promotion pPromotion = new Promotion();
						promotion = iterator.next();
						pPromotion.setTitle(promotion.getName());
						pPromotion.setTitle(promotion.getTitle());
						promotions.add(pPromotion);
					}
				}
				
				Brand brand = product.getBrand();
				String brandName = brand != null ? brand.getName() : "";
				Product.Type type = product.getTypeName();
				int availableStock = defaultSku.getAvailableStock();
				
				product.clear();
				product._setAttrs(pProduct);
				product.put("brand", brandName);
				product.put("promotions", promotions);
				product.put("type", type);
				product.put("availableStock", availableStock);
				product.put("marketPrice", currency(product.getMarketPrice() != null ? product.getMarketPrice() : BigDecimal.ZERO, false, false));
			}
		}
		return products;
	}
	
	/**
	 * 转换店铺信息
	 * 
	 */
	protected Store converterStore(Store store) {
		if (store == null) {
			return null;
		}
		Store pStore = new Store();
		pStore.setName(store.getName());
		pStore.put("type", store.getTypeName());
		return pStore;
	}
	
	/**
	 * 转换订单头
	 * 
	 */
	protected Order convertOrder(Order order) {
		Order pOrder = new Order();
		pOrder.setId(order.getId());
		pOrder.setSn(order.getSn());
		pOrder.setConsignee(order.getConsignee());
		pOrder.setPaymentMethodId(order.getPaymentMethodId());
		pOrder.setPaymentMethodName(order.getPaymentMethodName());
		pOrder.setAmount(order.getAmount());
		pOrder.setSource(order.getSource());
		pOrder.setCreatedDate(order.getCreatedDate());
		pOrder.setAreaName(order.getAreaName());
		pOrder.setAddress(order.getAddress());
		pOrder.setPhone(order.getPhone());
		pOrder.setMemo(order.getMemo());
		pOrder.setType(order.getType());
		pOrder.setStatus(order.getStatus());
		pOrder.setStoreId(order.getStoreId());
		
		order.clear();
		order._setAttrs(pOrder);
		Store store = pOrder.getStore();
		order.put("store_name", store != null ? pOrder.getStore().getName() : null);
		order.put("type_name", order.getTypeName());
		order.put("status_name", res.format("Order.Status." + order.getStatusName()));
		order.put("order_items", convertOrderItem(order.getOrderItems()));
		return order;
	}
	
	/**
	 * 转换订单详情
	 * 
	 */
	protected List<OrderItem> convertOrderItem(List<OrderItem> orderItems) {
		if (CollectionUtils.isNotEmpty(orderItems)) {
			for (OrderItem orderItem : orderItems) {
				Product product = orderItem.getSku().getProduct();
				Brand brand = product.getBrand();
				orderItem.remove("created_date", "last_modified_date", "version", "commission_totals", "is_delivery", "returned_quantity", "shipped_quantity", "specifications", "type", "order_id");
				orderItem.put("unit", product.getUnit());
				orderItem.put("brand", brand != null ? brand.getName() : null);
			}
		}
		return orderItems;
	}
	
	/**
	 * 评价转换
	 */
	protected List<Review> convertReview(List<Review> reviews) {
		if (CollectionUtils.isNotEmpty(reviews)) {
			for (Review review : reviews) {
				Member member = review.getMember();
				review.put("member", member.getUsername().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
				review.put("avatar", member.getAvatar());
				review.remove("last_modified_date", "version", "ip", "is_show", "product_id", "store_id");
			}
		}
		return reviews;
	}
	
	/**
	 * 咨询
	 */
	protected List<Consultation> convertConsultation(List<Consultation> consultations) {
		if (CollectionUtils.isNotEmpty(consultations)) {
			for (Consultation consultation : consultations) {
				Member member = consultation.getMember();
				consultation.put("member", member != null ? member.getUsername().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2") : null);
				consultation.put("avatar", member != null ? member.getAvatar() : null);
				consultation.remove("last_modified_date", "version", "ip", "is_show", "product_id", "store_id");
			}
		}
		return consultations;
	}
	
	/**
	 * 将String数组转换为Long类型数组
	 * 
	 */
	protected Long[] convertToLong(String name) {
		String[] values = StringUtils.split(name, ",");
		if (values == null)
			return null;
		Long[] result = new Long[values.length];
		for (int i=0; i<result.length; i++)
			result[i] = Long.parseLong(values[i]);
		return result;
	}
	
	
}
