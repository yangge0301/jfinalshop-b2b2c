package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.jfinal.kit.HttpKit;
import com.jfinalshop.util.MD5Util;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.dao.MemberDepositLogDao;
import com.jfinalshop.dao.MemberRankDao;
import com.jfinalshop.dao.PointLogDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberDepositLog;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.MemberRole;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.Assert;

/**
 * Service - 会员
 * 
 */
@Singleton
public class MemberService extends BaseService<Member> {

	/**
	 * 构造方法
	 */
	public MemberService() {
		super(Member.class);
	}

	@InjectSettings("${user_pay_to_wjn_jifen_url}")
	private String noticeUrl;
	@Inject
	private MemberDao memberDao;
	@Inject
	private MemberRankDao memberRankDao;
	@Inject
	private MemberDepositLogDao memberDepositLogDao;
	@Inject
	private PointLogDao pointLogDao;
	@Inject
	private ReceiverService receiverService;
//	@Inject
//	private MailService mailService;
//	@Inject
//	private SmsService smsService;
	
	private CacheManager cacheManager = CacheKit.getCacheManager();
	
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
		return memberDao.exists("username", username);
	}

	/**
	 * 根据用户名查找会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByUsername(String username) {
		return memberDao.find("username", username);
	}

	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		return memberDao.exists("email", StringUtils.lowerCase(email));
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
		return memberDao.unique(id, "email", StringUtils.lowerCase(email));
	}

	/**
	 * 根据E-mail查找会员
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByEmail(String email) {
		return memberDao.find("email", StringUtils.lowerCase(email));
	}


	/**
	 * 判断手机是否存在
	 * 
	 * @param mobile
	 *            手机(忽略大小写)
	 * @return 手机是否存在
	 */
	public boolean mobileExists(String mobile) {
		return memberDao.exists("mobile", StringUtils.lowerCase(mobile));
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
		return memberDao.unique(id, "mobile", StringUtils.lowerCase(mobile));
	}

	/**
	 * 根据手机查找会员
	 * 
	 * @param mobile
	 *            手机(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByMobile(String mobile) {
		return memberDao.find("mobile", StringUtils.lowerCase(mobile));
	}

	/**
	 * 查找会员分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param pageable
	 *            分页信息
	 * @return 会员分页
	 */
	public Page<Member> findPage(Member.RankingType rankingType, Pageable pageable) {
		return memberDao.findPage(rankingType, pageable);
	}

	/**
	 * 增加余额
	 * 
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param memo
	 *            备注
	 */
	public void addBalance(Member member, BigDecimal amount, MemberDepositLog.Type type, String memo) {
		Assert.notNull(member);
		Assert.notNull(amount);
		Assert.notNull(type);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(member.getBalance());
		Assert.state(member.getBalance().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setBalance(member.getBalance().add(amount));
		memberDao.update(member);

		MemberDepositLog memberDepositLog = new MemberDepositLog();
		memberDepositLog.setType(type.ordinal());
		memberDepositLog.setCredit(amount.compareTo(BigDecimal.ZERO) > 0 ? amount : BigDecimal.ZERO);
		memberDepositLog.setDebit(amount.compareTo(BigDecimal.ZERO) < 0 ? amount.abs() : BigDecimal.ZERO);
		memberDepositLog.setBalance(member.getBalance());
		memberDepositLog.setMemo(memo);
		memberDepositLog.setMemberId(member.getId());
		memberDepositLogDao.save(memberDepositLog);
	}

	/**
	 * 增加余额
	 *
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param memo
	 *            备注
	 */
	public void addBalanceV2(Member member, BigDecimal amount,BigDecimal addAmount, MemberDepositLog.Type type, String memo) {
		Assert.notNull(member);
		Assert.notNull(amount);
		Assert.notNull(type);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(member.getBalance());
		Assert.state(member.getBalance().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setBalance(amount);
		memberDao.update(member);

		MemberDepositLog memberDepositLog = new MemberDepositLog();
		memberDepositLog.setType(type.ordinal());
		memberDepositLog.setCredit(addAmount.compareTo(BigDecimal.ZERO) > 0 ? addAmount : BigDecimal.ZERO);
		memberDepositLog.setDebit(addAmount.compareTo(BigDecimal.ZERO) < 0 ? addAmount.abs() : BigDecimal.ZERO);
		memberDepositLog.setBalance(member.getBalance());
		memberDepositLog.setMemo(memo);
		memberDepositLog.setMemberId(member.getId());
		memberDepositLogDao.save(memberDepositLog);
	}

	/**
	 * 增加积分
	 * 
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param memo
	 *            备注
	 */
	public void addPoint(Member member, long amount, PointLog.Type type, String memo) {
		Assert.notNull(member);
		Assert.notNull(type);

		if (amount == 0) {
			return;
		}

		Assert.notNull(member.getPoint());
		Assert.state(member.getPoint() + amount >= 0);

		member.setPoint(member.getPoint() + amount);
		memberDao.update(member);

		PointLog pointLog = new PointLog();
		pointLog.setType(type.ordinal());
		pointLog.setCredit(amount > 0 ? amount : 0L);
		pointLog.setDebit(amount < 0 ? Math.abs(amount) : 0L);
		pointLog.setBalance(member.getPoint());
		pointLog.setMemo(memo);
		pointLog.setMemberId(member.getId());
		pointLogDao.save(pointLog);

		String account = member.getUsername();
		long timestamp = System.currentTimeMillis();

		SortedMap<Object,Object> parameters = new TreeMap<Object, Object>();
		parameters.put("account",account);
		parameters.put("jifen",amount);
		parameters.put("timestamp",timestamp);
		String sign = MD5Util.createSign(parameters);
		String url = noticeUrl +"&account="+account+"&timestamp="+timestamp+"&jifen=" +amount+"&sign="+sign;
		HttpKit.get(url);
	}

	public void addPointV2(Member member,long nowPoint,long amount, PointLog.Type type, String memo) {
		Assert.notNull(member);
		Assert.notNull(type);

		if (amount == 0) {
			return;
		}

		Assert.notNull(member.getPoint());
		Assert.state(nowPoint >= 0);

		member.setPoint(nowPoint);
		memberDao.update(member);

		PointLog pointLog = new PointLog();
		pointLog.setType(type.ordinal());
		pointLog.setCredit(amount > 0 ? amount : 0L);
		pointLog.setDebit(amount < 0 ? Math.abs(amount) : 0L);
		pointLog.setBalance(nowPoint);
		pointLog.setMemo(memo);
		pointLog.setMemberId(member.getId());
		pointLogDao.save(pointLog);
		String account = member.getUsername();
		long timestamp = System.currentTimeMillis();

		SortedMap<Object,Object> parameters = new TreeMap<Object, Object>();
		parameters.put("account",account);
		parameters.put("jifen",amount);
		parameters.put("timestamp",timestamp);
		String sign = MD5Util.createSign(parameters);
		String url = noticeUrl +"&account="+account+"&timestamp="+timestamp+"&jifen=" +amount+"&sign="+sign;
		HttpKit.get(url);
	}

	/**
	 * 增加消费金额
	 * 
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 */
	public void addAmount(Member member, BigDecimal amount) {
		Assert.notNull(member);
		Assert.notNull(amount);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

//		if (!LockModeType.PESSIMISTIC_WRITE.equals(memberDao.getLockMode(member))) {
//			memberDao.flush();
//			memberDao.refresh(member, LockModeType.PESSIMISTIC_WRITE);
//		}

		Assert.notNull(member.getAmount());
		Assert.state(member.getAmount().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setAmount(member.getAmount().add(amount));
		MemberRank memberRank = member.getMemberRank();
		if (memberRank != null && BooleanUtils.isFalse(memberRank.getIsSpecial())) {
			MemberRank newMemberRank = memberRankDao.findByAmount(member.getAmount());
			if (newMemberRank != null && newMemberRank.getAmount() != null && newMemberRank.getAmount().compareTo(memberRank.getAmount()) > 0) {
				member.setMemberRank(newMemberRank);
			}
		}
		//memberDao.flush();
		memberDao.update(member);
	}

	/**
	 * 用户解锁
	 * 
	 * @param user
	 *            用户
	 */
	public void unlock(Member member) {
		Assert.notNull(member);
		Assert.isTrue(!member.isNew());

		if (BooleanUtils.isFalse(member.getIsLocked())) {
			return;
		}

		member.setIsLocked(false);
		member.setLockDate(null);
		resetFailedLoginAttempts(member);
	}
	
	/**
	 * 重置登录失败尝试次数
	 * 
	 * @param user
	 *            用户
	 */
	public void resetFailedLoginAttempts(Member member) {
		Assert.notNull(member);
		Assert.isTrue(!member.isNew());

		Ehcache cache = cacheManager.getEhcache(Member.FAILED_LOGIN_ATTEMPTS_CACHE_NAME);
		cache.remove(member.getId());
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
	public Member getCurrentUser() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (!(SubjectKit.getUser() instanceof Member)) {
				 return null;
			}
			Member principal = SubjectKit.getUser();
			if (principal != null) {
				return memberDao.find(principal.getId());
			}
		}
		return null;
	}
	
	@Override
	public Member save(Member member) {
		Assert.notNull(member);
		super.save(member);
		// 增加默认角色
		MemberRole memberRole = new MemberRole();
		memberRole.setMembersId(member.getId());
		memberRole.setRolesId(3L);
		memberRole.save();
		
		// 内部用户增加默认地址
		if (member.getMemberRankId() == 5L ) {
			receiverService.save(member);
		}
		return member;
	}
}