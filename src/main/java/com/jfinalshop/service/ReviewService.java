package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.dao.OrderDao;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.dao.ReviewDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 评论
 * 
 */
@Singleton
public class ReviewService extends BaseService<Review> {

	/**
	 * 构造方法
	 */
	public ReviewService() {
		super(Review.class);
	}
	
	@Inject
	private ReviewDao reviewDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private OrderDao orderDao;
	
	/**
	 * 查找评论
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 评论
	 */
	public List<Review> findList(Member member, Product product, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		return reviewDao.findList(member, product, type, isShow, count, filters, orders);
	}

	/**
	 * 查找评论
	 * 
	 * @param memberId
	 *            会员ID
	 * @param productId
	 *            商品ID
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 评论
	 */
	public List<Review> findList(Long memberId, Long productId, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		Member member = memberDao.find(memberId);
		if (memberId != null && member == null) {
			return Collections.emptyList();
		}
		Product product = productDao.find(productId);
		if (productId != null && product == null) {
			return Collections.emptyList();
		}
		return reviewDao.findList(member, product, type, isShow, count, filters, orders);
	}

	/**
	 * 查找评论分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 评论分页
	 */
	public Page<Review> findPage(Member member, Product product, Store store, Review.Type type, Boolean isShow, Pageable pageable) {
		return reviewDao.findPage(member, product, store, type, isShow, pageable);
	}

	/**
	 * 查找评论数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @return 评论数量
	 */
	public Long count(Member member, Product product, Review.Type type, Boolean isShow) {
		return reviewDao.count(member, product, type, isShow);
	}

	/**
	 * 评论回复
	 * 
	 * @param review
	 *            评论
	 * @param replyReview
	 *            回复评论
	 */
	public void reply(Review review, Review replyReview) {
		if (review == null || replyReview == null) {
			return;
		}

		replyReview.setIsShow(true);
		replyReview.setProductId(review.getProduct().getId());
		replyReview.setForReviewId(review.getId());
		replyReview.setStoreId(review.getStore().getId());
		replyReview.setScore(review.getScore());
		replyReview.setMemberId(review.getMember().getId());
		reviewDao.save(replyReview);
	}
	

	/**
	 * 判断是否拥有评论权限
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @return 是否拥有评论权限
	 */
	public boolean hasPermission(Member member, Product product) {
		Assert.notNull(member);
		Assert.notNull(product);

		long reviewCount = reviewDao.count(member, product, null, null);
		long orderCount = orderDao.count(null, com.jfinalshop.model.Order.Status.completed, null, member, product, null, null, null, null, null, null);
		return orderCount > reviewCount;
	}
	
	@Override
	public Review save(Review review) {
		Assert.notNull(review);

		Review pReview = super.save(review);
		Product product = pReview.getProduct();
		if (product != null) {
			long totalScore = reviewDao.calculateTotalScore(product);
			long scoreCount = reviewDao.calculateScoreCount(product);
			product.setTotalScore(totalScore);
			product.setScoreCount(scoreCount);
			productDao.update(product);
		}
		return pReview;
	}
	
	@Override
	public Review update(Review review) {
		Assert.notNull(review);

		Review pReview = super.update(review);
		Product product = pReview.getProduct();
		if (product != null) {
			long totalScore = reviewDao.calculateTotalScore(product);
			long scoreCount = reviewDao.calculateScoreCount(product);
			product.setTotalScore(totalScore);
			product.setScoreCount(scoreCount);
			productDao.update(product);
		}
		return pReview;
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
	public void delete(Review review) {
		if (review != null) {
			super.delete(review);
			Product product = review.getProduct();
			if (product != null) {
				long totalScore = reviewDao.calculateTotalScore(product);
				long scoreCount = reviewDao.calculateScoreCount(product);
				product.setTotalScore(totalScore);
				product.setScoreCount(scoreCount);
				productDao.update(product);
			}
		}
	}
}