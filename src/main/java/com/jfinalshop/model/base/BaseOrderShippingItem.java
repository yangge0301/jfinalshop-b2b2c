package com.jfinalshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOrderShippingItem<M extends BaseOrderShippingItem<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setCreatedDate(java.util.Date createdDate) {
		set("created_date", createdDate);
	}

	public java.util.Date getCreatedDate() {
		return get("created_date");
	}

	public void setLastModifiedDate(java.util.Date lastModifiedDate) {
		set("last_modified_date", lastModifiedDate);
	}

	public java.util.Date getLastModifiedDate() {
		return get("last_modified_date");
	}

	public void setVersion(java.lang.Long version) {
		set("version", version);
	}

	public java.lang.Long getVersion() {
		return getLong("version");
	}

	public void setIsDelivery(java.lang.Boolean isDelivery) {
		set("is_delivery", isDelivery);
	}

	public java.lang.Boolean getIsDelivery() {
		return get("is_delivery");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return getStr("name");
	}

	public void setQuantity(java.lang.Integer quantity) {
		set("quantity", quantity);
	}

	public java.lang.Integer getQuantity() {
		return getInt("quantity");
	}

	public void setSn(java.lang.String sn) {
		set("sn", sn);
	}

	public java.lang.String getSn() {
		return getStr("sn");
	}

	public void setSpecifications(java.lang.String specifications) {
		set("specifications", specifications);
	}

	public java.lang.String getSpecifications() {
		return getStr("specifications");
	}

	public void setOrderShippingId(java.lang.Long orderShippingId) {
		set("order_shipping_id", orderShippingId);
	}

	public java.lang.Long getOrderShippingId() {
		return getLong("order_shipping_id");
	}

	public void setSkuId(java.lang.Long skuId) {
		set("sku_id", skuId);
	}

	public java.lang.Long getSkuId() {
		return getLong("sku_id");
	}

}