package com.shcx.user.userservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shcx.user.userservice.pojo.User;

/**
 * 用户持久层
 * 
 * @Title UserDao.java 
 * @description TODO 
 * @time 2017年11月22日 下午3:08:11 
 * @author LILJ 
 * @version 1.0
 */
@Repository("userDao")
public interface UserDao extends JpaRepository<User, Long>,
		JpaSpecificationExecutor<User> {
	
	/**
	 * 修改过期的token用户
	 * 
	 * @param expiretime
	 * @time 2017年11月22日 下午7:31:59 
	 * @author LILJ
	 */
	@Modifying
	@Query("update User u set u.expiretime = 0, u.logined = 1  where u.expiretime <= ?1 and u.expiretime > 0")
	public void updateExpiretimeUser(Long expiretime);
	
	/**
	 * 根据用户名查询
	 * 
	 * @param username
	 * @return User
	 * @time 2017年11月22日 下午3:03:18 
	 * @author LILJ
	 */
	public User findByUsername(String username);
	
	/**
	 * 根据邮箱查询
	 * 
	 * @param email
	 * @return User
	 * @time 2017年11月22日 下午3:04:03 
	 * @author LILJ
	 */
	public User findByEmail(String email);
	
	/**
	 * 根据手机号
	 * @param phone
	 * @return User
	 * @time 2017年11月22日 下午3:04:12 
	 * @author LILJ
	 */
	public User findByPhone(String phone);
	
	/**
	 * 锁定
	 * 
	 * @param id void
	 * @time 2017年11月22日 下午3:43:27 
	 * @author LILJ
	 */
	@Modifying
	@Query("update User u set u.locked = 0 where u.id = ?1")
	public void lockUser(Long id);
	
	/**
	 * 解锁
	 * 
	 * @param id
	 * @time 2017年11月22日 下午3:43:18 
	 * @author LILJ
	 */
	@Modifying
	@Query("update User u set u.locked = 1 where u.id = ?1")
	public void unlockUser(Long id);
	

	/**
	 * 确认
	 * 
	 * @param id
	 * @time 2017年11月22日 下午3:43:12 
	 * @author LILJ
	 */
	@Modifying
	@Query("update User u set u.confirmed = 0 where u.id = ?1")
	public void confirmedUser(Long id);
	
	/**
	 * 未确认
	 * 
	 * @param id
	 * @time 2017年11月22日 下午3:43:03 
	 * @author LILJ
	 */
	@Modifying
	@Query("update User u set u.confirmed = 1 where u.id = ?1")
	public void unConfirmedUser(Long id);
	
}
