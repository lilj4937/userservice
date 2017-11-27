package com.shcx.user.userservice.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.sf.json.JSONObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shcx.user.userservice.dao.UserDao;
import com.shcx.user.userservice.pojo.User;
import com.shcx.user.userservice.util.PasswordHash;
import com.shcx.user.userservice.util.StringUtils;

@Service("userService")
public class UserService {

	@Resource
	private UserDao userDao;
	
	/**
	 * 修改过期用户的登录状态
	 * 
	 *  void
	 * @time 2017年11月22日 下午7:36:24 
	 * @author LILJ
	 */
	public void updateExpiretimeUser(){
		Long nowtime = System.currentTimeMillis();
		userDao.updateExpiretimeUser(nowtime);
	}
	
	/**
	 * 删除用户
	 * 
	 * @param id
	 * @return boolean
	 * @time 2017年11月24日 下午6:46:14 
	 * @author LILJ
	 */
	public boolean deleteById(Long id){
		boolean b = true;
		try {
			userDao.delete(id);
		} catch (Exception e) {
			b = false;
		}
		return b;
	}

	public List<User> findAll(User user, Order... orders) {
		Sort sort = null;
		if (orders != null && orders.length > 0)
			sort = new Sort(orders);
		return userDao.findAll(getWhereClause(user), sort);
	}

	/**
	 * 分页查询
	 * 
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @param orders
	 * @return Page<User>
	 * @time 2017年11月22日 下午7:29:18 
	 * @author LILJ
	 */
	public Page<User> findPage(User user, Integer pageNo, Integer pageSize,
			Order... orders) {
		Sort sort = null;
		if (orders != null && orders.length > 0)
			sort = new Sort(orders);
		Pageable pageable = new PageRequest(pageNo - 1, pageSize, sort);
		return userDao.findAll(getWhereClause(user), pageable);
	}

	private Specification<User> getWhereClause(final User user) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (user == null) {
					return null;
				}
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(user.getUsername())) {
					list.add(cb.like(root.get("username").as(String.class), "%"
							+ user.getUsername().trim() + "%"));
				}
				if (StringUtils.isNotEmpty(user.getEmail())) {
					list.add(cb.like(root.get("email").as(String.class), "%"
							+ user.getEmail().trim() + "%"));
				}
				if (StringUtils.isNotEmpty(user.getPhone())) {
					list.add(cb.like(root.get("phone").as(String.class), "%"
							+ user.getPhone().trim() + "%"));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		};
	}

	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	public User findByPhone(String phone) {
		return userDao.findByPhone(phone);
	}

	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	public User save(User user) {
		return userDao.save(user);
	}

	public User findById(Long id) {
		return userDao.findOne(id);
	}

	/**
	 * 
	 * 编辑用户，没有输入合法验证，所以在调用该方法前应该先做输入合法验证
	 * 
	 * @param user
	 * @return JSONObject
	 * @time 2017年11月22日 下午3:49:22
	 * @author LILJ
	 */
	public JSONObject editUser(User user) {
		JSONObject result = new JSONObject();
		if (user == null) {
			result.put("code", 404);
			result.put("message", "没有编辑对象！");
			return result;
		}

		if (checkUsername(user.getUsername(), user.getId())) {
			result.put("code", 400);
			result.put("message", "用户名（username）已存在！");
			return result;
		}

		if (StringUtils.isNotEmpty(user.getEmail())
				&& checkEmail(user.getEmail(), user.getId())) {
			result.put("code", 400);
			result.put("message", "邮箱（email）已存在！");
			return result;
		}

		if (StringUtils.isNotEmpty(user.getPhone())
				&& checkPhone(user.getPhone(), user.getId())) {
			result.put("code", 400);
			result.put("message", "手机号（phone）已存在！");
			return result;
		}

		Long now = System.currentTimeMillis();
		if (user.getId() == null) {
			user = save(user);
		} else {
			/**
			 * 修改项：邮箱，角色，手机号，性别，是否认证过， 是否锁定
			 */
			User oldUser = findById(user.getId());
			if (StringUtils.isNotEmpty(user.getEmail())) {// 修改邮箱
				oldUser.setEmail(user.getEmail());
			}
			if (StringUtils.isNotEmpty(user.getPhone())) {// 修改手机号
				oldUser.setPhone(user.getPhone());
			}
			if (user.getRole() != null && user.getRole().getId() != null) {// 角色
				oldUser.setRole(user.getRole());
			}
			if (user.getSex() != null) {// 修改性别
				oldUser.setSex(user.getSex());
			}
			if (user.getConfirmed() != null) {// 用户认证
				oldUser.setConfirmed(user.getConfirmed());
			}
			if (user.getLocked() != null) {// 用户锁定
				oldUser.setLocked(user.getLocked());
			}

			oldUser.setLastUpdateDate(now);
			user = save(oldUser);
		}
		user.setPassword(null);// 禁止响应密码信息
		result.put("code", 200);
		result.put("user", user);
		return result;
	}

	/**
	 * 锁定
	 * 
	 * @param id
	 *            void
	 * @time 2017年11月22日 下午3:43:27
	 * @author LILJ
	 */
	public void lockUser(Long id) {
		userDao.lockUser(id);
	}

	/**
	 * 解锁
	 * 
	 * @param id
	 *            void
	 * @time 2017年11月22日 下午3:43:18
	 * @author LILJ
	 */
	public void unlockUser(Long id) {
		userDao.unlockUser(id);
	}

	/**
	 * 未确认用户
	 * 
	 * @param id
	 *            void
	 * @time 2017年11月22日 下午3:43:12
	 * @author LILJ
	 */
	public void unConfirmedUser(Long id, Integer val) {
		userDao.unConfirmedUser(id);
	}

	/**
	 * 确认用户
	 * 
	 * @param id
	 *            void
	 * @time 2017年11月22日 下午3:43:03
	 * @author LILJ
	 */
	public void confirmedUser(Long id) {
		userDao.confirmedUser(id);
	}

	/**
	 * 验证手机号是否存在
	 * 
	 * @param phone
	 * @param id
	 * @return boolean
	 * @time 2017年11月22日 下午3:36:53
	 * @author LILJ
	 */
	public boolean checkPhone(String phone, Long id) {
		User user = findByPhone(phone);
		return checkExistUser(user, id);
	}

	/**
	 * 验证邮箱是否存在
	 * 
	 * @param email
	 * @param id
	 * @return boolean
	 * @time 2017年11月22日 下午3:35:49
	 * @author LILJ
	 */
	public boolean checkEmail(String email, Long id) {
		User user = findByEmail(email);
		return checkExistUser(user, id);
	}

	/**
	 * 验证用户名是否存在
	 * 
	 * @param username
	 * @param id
	 * @return boolean
	 * @time 2017年11月22日 下午3:34:50
	 * @author LILJ
	 */
	public boolean checkUsername(String username, Long id) {
		User user = findByUsername(username);
		return checkExistUser(user, id);
	}

	/**
	 * 验证 User 是否存在
	 * 
	 * @param user
	 * @param id
	 * @return boolean
	 * @time 2017年11月22日 下午3:34:33
	 * @author LILJ
	 */
	public boolean checkExistUser(User user, Long id) {
		if (user == null) {// 不存在
			return false;
		}
		if (user.getId() == id) {// 是它本身
			return false;
		}
		return true;// 存在
	}

	/**
	 * 使用用户名登录
	 * 
	 * @param username
	 * @param password
	 * @return User
	 * @time 2017年11月22日 下午3:15:34
	 * @author LILJ
	 */
	public User loginByUsername(String username, String password) {
		if (username == null) {
			return null;
		}
		User user = findByUsername(username);
		return checkLoginUser(user, password);
	}

	/**
	 * 使用email登录
	 * 
	 * @param email
	 * @param password
	 * @return User
	 * @time 2017年11月22日 下午3:23:25
	 * @author LILJ
	 */
	public User loginByEmail(String email, String password) {
		if (email == null) {
			return null;
		}
		User user = findByEmail(email);
		return checkLoginUser(user, password);
	}

	/**
	 * 使用手机号登录
	 * 
	 * @param phone
	 * @param password
	 * @return User
	 * @time 2017年11月22日 下午3:24:02
	 * @author LILJ
	 */
	public User loginByPhone(String phone, String password) {
		if (phone == null) {
			return null;
		}
		User user = findByPhone(phone);
		return checkLoginUser(user, password);
	}

	/**
	 * 验证用户密码
	 * 
	 * @param user
	 * @param password
	 * @return User
	 * @time 2017年11月22日 下午3:25:32
	 * @author LILJ
	 */
	private User checkLoginUser(User user, String password) {
		if (user == null) {
			return null;
		}

		if (PasswordHash.validatePassword(password, user.getPassword())) {
			return user;
		}

		return null;
	}

	/**
	 * 注销
	 * 
	 * @param id
	 * @return boolean
	 * @time 2017年11月22日 下午3:22:15
	 * @author LILJ
	 */
	public boolean logout(Long id) {
		if (id == null) {
			return false;
		}
		boolean b = true;
		try {
			User user = findById(id);
			if (user == null) {
				return false;
			}
			user.setLogined(1);// 修改成未登录状态
			save(user);
		} catch (Exception e) {
			b = false;
		}
		return b;
	}
}
