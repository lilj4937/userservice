package com.shcx.user.userservice.listener;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.shcx.user.userservice.pojo.Role;
import com.shcx.user.userservice.pojo.User;
import com.shcx.user.userservice.service.RoleService;
import com.shcx.user.userservice.service.UserService;
import com.shcx.user.userservice.util.LoggerUtil;
import com.shcx.user.userservice.util.PasswordHash;
import com.shcx.user.userservice.util.StaticValues;

/**
 * 自定义ServletContextListener监听
 * 
 * @Title CustomServletContextListener.java
 * @description TODO
 * @time 2017年11月23日 上午10:19:27
 * @author LILJ
 * @version 1.0
 */
@WebListener
public class CustomServletContextListener implements ServletContextListener {

	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;

	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		System.out.println("servletContext销毁......");

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		try {
			Long now = System.currentTimeMillis();
			Role admin = new Role();
			admin.setCode(StaticValues.ADMIN_ROLE_CODE);
			admin.setName(StaticValues.ADMIN_ROLE_NAME);
			admin.setCreateDate(now);
			admin.setLastUpdateDate(now);
			Role newuser = new Role();
			newuser.setCode(StaticValues.USER_ROLE_CODE);
			newuser.setName(StaticValues.USER_ROLE_NAME);
			newuser.setCreateDate(now);
			newuser.setLastUpdateDate(now);
			if(!roleService.checkRole(admin.getId(), admin.getCode(), admin.getName())){
				admin = roleService.save(admin);
			}
			if(!roleService.checkRole(newuser.getId(), newuser.getCode(), newuser.getName())){
				newuser = roleService.save(newuser);
			}
			if(admin.getId() == null){
				admin = roleService.findByCodeOrName(admin.getCode(), admin.getName());
			}
			User user = new User();
			user.setConfirmed(0);
			user.setCreateDate(now);
			user.setEmail(StaticValues.ADMIN_EMAIL);
			user.setLastUpdateDate(now);
			user.setLocked(1);
			user.setPassword(PasswordHash.createHash(StaticValues.ADMIN_PASSWORD));
			user.setPhone(StaticValues.ADMIN_PHONE);
			user.setRole(admin);
			user.setSex(1);
			user.setUsername(StaticValues.ADMIN_USERNAME);
			userService.editUser(user);
		} catch (Exception e) {
			LoggerUtil.error("初始化超级管理员信息异常！", e);
		}

	}

}
