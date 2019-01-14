package com.jfinalshop.api.controller.shop;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.*;
import com.jfinalshop.service.*;
import com.jfinalshop.util.SystemUtils;
import net.hasor.core.Inject;
import org.apache.commons.lang.BooleanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListController extends Controller {

    @Inject
    private AdPositionService adPositionService ;
    @Inject
    private ProductCategoryService productCategoryService;

    @Inject
    private ProductService productService;
    @ActionKey("/getcategory")
    public void category(){
        List<ProductCategory> list = productCategoryService.findRoots();
        List<ProductCategory> list2 = new ArrayList<ProductCategory>();
        List<Product> list3 = new ArrayList<Product>();
        if(list!=null&&list.size()>0){
            for(ProductCategory p : list){
                p.setChildId(p.getChildren());
                p.setProInfo(p.getProducts());
                list2.add(p);
            }
        }
        renderJson(list2);
    }

    @ActionKey("/getad")
    public void getad(){
        Long id = getParaToLong("id");
        AdPosition adPosition = adPositionService.find(id);
        List<Ad> list = new ArrayList<Ad>();
        if(adPosition!=null&&adPosition.getAds()!=null&&adPosition.getAds().size()>0){
            for(Ad ad : adPosition.getAds()){
                if (!(ad == null)) {
                    ad.setPath(SystemUtils.getSetting().getSiteImageUrl()+ad.getPath());
                    list.add(ad);
                }
            }
            adPosition.setAd(list);
        }
        renderJson(adPosition);
    }


    /**
     * 详情
     */
    @ActionKey("/detailreturn")
    public void detailreturn() {
        Long productId = getParaToLong("id");
        Product product = productService.find(productId);
        if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
            renderJson("");
        }
        List<ProductImage> list = product.getProductImagesConverter();
        List<ProductImage> listtmp = new ArrayList<ProductImage>();
        if (list == null || list.size()==0) {
            renderJson("");
        }
        for(ProductImage p : list){
            if(p!=null&&p.getLarge()!=null&&!p.getLarge().trim().equals("")&&p.getMedium()!=null&&!p.getMedium().trim().equals("")){
                listtmp.add(p);
            }
        }
        product.setProductImages(listtmp);
        renderJson(product);
    }

    @Inject
    private SkuService skuService;
    @Inject
    protected CartService cartService;
    private Res res = I18n.use();
    @ActionKey("/addCart")
    public void addCart(){
        Long skuId = getParaToLong("skuId");
        Integer quantity = getParaToInt("quantity");
        String cartKey = getRequest().getSession().getId();

        Map<String, Object> data = new HashMap<>();
        Cart currentCart = cartService.getCurrent(cartKey);
        if (quantity == null || quantity < 1) {
            renderJson("数量不能为空哟!");
            return;
        }
        Sku sku = skuService.find(skuId);
        if (sku == null) {
            renderJson("商品没有找到哟!");
            return;
        }
        if (!Product.Type.general.equals(sku.getType())) {
            renderJson(res.format("shop.cart.skuNotForSale"));
            return;
        }
        if (!sku.getIsActive()) {
            renderJson(res.format("shop.cart.skuNotActive"));
            return;
        }
        if (!sku.getIsMarketable()) {
            renderJson(res.format("shop.cart.skuNotMarketable"));
            return;
        }

        int cartItemSize = 1;
        int skuQuantity = quantity;
        if (currentCart != null) {
            if (currentCart.contains(sku, null)) {
                CartItem cartItem = currentCart.getCartItem(sku, null);
                cartItemSize = currentCart.size();
                skuQuantity = cartItem.getQuantity() + quantity;
            } else {
                cartItemSize = currentCart.size() + 1;
                skuQuantity = quantity;
            }
        }
        if (Cart.MAX_CART_ITEM_SIZE != null && cartItemSize > Cart.MAX_CART_ITEM_SIZE) {
            renderJson(res.format("shop.cart.addCartItemCountNotAllowed" , Cart.MAX_CART_ITEM_SIZE));
            return;
        }
        if (CartItem.MAX_QUANTITY != null && skuQuantity > CartItem.MAX_QUANTITY) {
            renderJson(res.format("shop.cart.addQuantityNotAllowed" , CartItem.MAX_QUANTITY));
            return;
        }
        if (skuQuantity > sku.getAvailableStock()) {
            renderJson(res.format("shop.cart.skuLowStock"));
            return;
        }
        if (currentCart == null) {
            currentCart = cartService.create();
        }
        cartService.add(currentCart, sku, quantity);
        if (currentCart != null) {
            data.put("cartKey", currentCart.getCartKey());
            data.put("quantity", currentCart.getSkuQuantity(null));
        }
        renderJson(new DatumResponse(data));
    }
}
