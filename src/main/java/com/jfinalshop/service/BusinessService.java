package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.BusinessDao;
import com.jfinalshop.dao.BusinessDepositLogDao;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;
import com.jfinalshop.model.BusinessRole;
import com.jfinalshop.model.Role;
import com.jfinalshop.model.Store;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 商家
 * 
 */
@Singleton
public class BusinessService extends BaseService<Business>{

	/**
	 * 构造方法
	 */
	public BusinessService() {
		super(Business.class);
	}
	
	private CacheManager cacheManager = CacheKit.getCacheManager();
	
	@Inject
	private BusinessDao businessDao;
	@Inject
	private BusinessDepositLogDao businessDepositLogDao;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	
	/**
	 * E-mail身份配比
	 */
	private static final Pattern EMAIL_PRINCIPAL_PATTERN = Pattern.compile(".*@.*");

	/**
	 * 手机身份配比
	 */
	private static final Pattern MOBILE_PRINCIPAL_PATTERN = Pattern.compile("\\d+");
	
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		return businessDao.exists("username",username);
	}

	/**
	 * 根据用户名查找商家
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 商家，若不存在则返回null
	 */
	public Business findByUsername(String username) {
		return businessDao.find("username", StringUtils.lowerCase(username));
	}

	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		return businessDao.exists("email", StringUtils.lowerCase(email));
	}

	/**
	 * 判断E-mail是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否唯一
	 */
	public boolean emailUnique(Long id, String email) {
		return businessDao.unique(id, "email", StringUtils.lowerCase(email));
	}

	/**
	 * 根据E-mail查找商家
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 商家，若不存在则返回null
	 */
	public Business findByEmail(String email) {
		return businessDao.find("email", StringUtils.lowerCase(email));
	}

	/**
	 * 判断手机是否存在
	 * 
	 * @param mobile
	 *            手机(忽略大小写)
	 * @return 手机是否存在
	 */
	public boolean mobileExists(String mobile) {
		return businessDao.exists("mobile", StringUtils.lowerCase(mobile));
	}

	/**
	 * 判断手机是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param mobile
	 *            手机(忽略大小写)
	 * @return 手机是否唯一
	 */
	public boolean mobileUnique(Long id, String mobile) {
		return businessDao.unique(id, "mobile", StringUtils.lowerCase(mobile));
	}

	/**
	 * 通过名称查找商家
	 * 
	 * @param keyword
	 *            关键词
	 * @param count
	 *            数量
	 * @return 商家
	 */
	public List<Business> search(String keyword, Integer count) {
		return businessDao.search(keyword, count);
	}

	/**
	 * 根据手机查找商家
	 * 
	 * @param mobile
	 *            手机(忽略大小写)
	 * @return 商家，若不存在则返回null
	 */
	public Business findByMobile(String mobile) {
		return businessDao.find("mobile", StringUtils.lowerCase(mobile));
	}

	/**
	 * 增加余额
	 * 
	 * @param business
	 *            商家
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param memo
	 *            备注
	 */
	public void addBalance(Business business, BigDecimal amount, BusinessDepositLog.Type type, String memo) {
		Assert.notNull(business);
		Assert.notNull(amount);
		Assert.notNull(type);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(business.getBalance());
		Assert.state(business.getBalance().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		business.setBalance(business.getBalance().add(amount));
		businessDao.update(business);

		BusinessDepositLog businessDepositLog = new BusinessDepositLog();
		businessDepositLog.setType(type.ordinal());
		businessDepositLog.setCredit(amount.compareTo(BigDecimal.ZERO) > 0 ? amount : BigDecimal.ZERO);
		businessDepositLog.setDebit(amount.compareTo(BigDecimal.ZERO) < 0 ? amount.abs() : BigDecimal.ZERO);
		businessDepositLog.setBalance(business.getBalance());
		businessDepositLog.setMemo(memo);
		businessDepositLog.setBusinessId(business.getId());
		businessDepositLogDao.save(businessDepositLog);
	}

	/**
	 * 增加冻结金额
	 * 
	 * @param business
	 *            商家
	 * @param amount
	 *            值
	 */
	public void addFrozenFund(Business business, BigDecimal amount) {
		Assert.notNull(business);
		Assert.notNull(amount);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(business.getFrozenFund());
		Assert.state(business.getFrozenFund().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		business.setFrozenFund(business.getFrozenFund().add(amount));
		businessDao.update(business);
	}
	
	/**
	 * 获取登录失败尝试次数
	 * 
	 * @param user
	 *            用户
	 * @return 登录失败尝试次数
	 */
	public int getFailedLoginAttempts(Business business) {
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		Ehcache cache = cacheManager.getEhcache(Business.FAILED_LOGIN_ATTEMPTS_CACHE_NAME);
		Element element = cache.get(business.getId());
		AtomicInteger failedLoginAttempts = element != null ? (AtomicInteger) element.getObjectValue() : null;
		return failedLoginAttempts != null ? failedLoginAttempts.get() : 0;
	}

	/**
	 * 增加登录失败尝试次数
	 * 
	 * @param user
	 *            用户
	 */
	public void addFailedLoginAttempt(Business business) {
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		Long userId = business.getId();
		Ehcache cache = cacheManager.getEhcache(Business.FAILED_LOGIN_ATTEMPTS_CACHE_NAME);
		cache.acquireWriteLockOnKey(userId);
		try {
			Element element = cache.get(userId);
			AtomicInteger failedLoginAttempts = element != null ? (AtomicInteger) element.getObjectValue() : null;
			if (failedLoginAttempts != null) {
				failedLoginAttempts.incrementAndGet();
			} else {
				cache.put(new Element(userId, new AtomicInteger(1)));
			}
		} finally {
			cache.releaseWriteLockOnKey(userId);
		}
	}

	/**
	 * 重置登录失败尝试次数
	 * 
	 * @param user
	 *            用户
	 */
	public void resetFailedLoginAttempts(Business business) {
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		Ehcache cache = cacheManager.getEhcache(Business.FAILED_LOGIN_ATTEMPTS_CACHE_NAME);
		cache.remove(business.getId());
	}

	/**
	 * 尝试用户锁定
	 * 
	 * @param user
	 *            用户
	 * @return 是否锁定
	 */
	public boolean tryLock(Business business) {
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		if (BooleanUtils.isTrue(business.getIsLocked())) {
			return true;
		}

		Setting setting = SystemUtils.getSetting();
		if (setting.getMaxFailedLoginAttempts() != null) {
			int failedLoginAttempts = getFailedLoginAttempts(business);
			if (failedLoginAttempts >= setting.getMaxFailedLoginAttempts()) {
				business.setIsLocked(true);
				business.setLockDate(new Date());
				return true;
			}
		}
		return false;
	}

	/**
	 * 尝试用户解锁
	 * 
	 * @param user
	 *            用户
	 * @return 是否解锁
	 */
	public boolean tryUnlock(Business business) {
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		if (BooleanUtils.isFalse(business.getIsLocked())) {
			return true;
		}

		Setting setting = SystemUtils.getSetting();
		if (setting.getPasswordLockTime() != null) {
			Date lockDate = business.getLockDate();
			Date unlockDate = DateUtils.addMinutes(lockDate, setting.getPasswordLockTime());
			if (new Date().after(unlockDate)) {
				unlock(business);
				return true;
			}
		}
		return false;
	}

	/**
	 * 用户解锁
	 * 
	 * @param user
	 *            用户
	 */
	public void unlock(Business business) {
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		if (BooleanUtils.isFalse(business.getIsLocked())) {
			return;
		}

		business.setIsLocked(false);
		business.setLockDate(null);
		resetFailedLoginAttempts(business);
	}
	
	/**
	 * 判断商家是否登录
	 * 
	 * @return 商家是否登录
	 */
	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			return subject.isAuthenticated();
		}
		return false;
	}
	
	/**
	 * 获取当前登录商家
	 * 
	 * @return 当前登录管理员，若不存在则返回null
	 */
	public Business getCurrentUser() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (!(SubjectKit.getUser() instanceof Business)) {
				 return null;
			}
			Business principal = SubjectKit.getUser();
			if (principal != null) {
				return businessDao.find(principal.getId());
			}
		}
		return null;
	}
	
	
	/**
	 * 获取当前登录商家
	 * 
	 * @return 当前登录管理员，若不存在则返回null
	 */
	public Store getCurrentStore() {
		Business business = getCurrentUser();
		if (business != null) {
			return business.getStore();
		}
		return null;
	}
	
	@Override
	public Business save(Business business) {
		Assert.notNull(business);
		Business pBusiness = super.save(business);
		// 关联保存
		List<Role> roles = business.getRoles();
		if (CollectionUtil.isNotEmpty(roles)) {
			for (Role role : roles) {
				BusinessRole businessRole = new BusinessRole();
				businessRole.setBusinessId(business.getId());
				businessRole.setRolesId(role.getId());
				businessRole.save();
			}
		}
		mailService.sendRegisterBusinessMail(pBusiness);
		smsService.sendRegisterBusinessSms(pBusiness);
		return pBusiness;
	}

	@Override
	public Business update(Business business, String... ignoreProperties) {
		super.update(business, ignoreProperties);
		
		//先清除，再保存
		Db.deleteById("business_role", "business_id", business.getId());
		List<Role> roles = business.getRoles();
		if (CollectionUtil.isNotEmpty(roles)) {
			for (Role role : roles) {
				BusinessRole businessRole = new BusinessRole();
				businessRole.setBusinessId(business.getId());
				businessRole.setRolesId(role.getId());
				businessRole.save();
			}
		}
		return business;
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
	public void delete(Business business) {
		super.delete(business);
	}
}