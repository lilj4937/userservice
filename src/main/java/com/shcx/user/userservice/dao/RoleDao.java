package com.shcx.user.userservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shcx.user.userservice.pojo.Role;

/**
 * 角色持久层
 * 
 * @Title RoleDao.java 
 * @description TODO 
 * @time 2017年11月22日 下午3:07:57 
 * @author LILJ 
 * @version 1.0
 */
@Repository("roleDao")
public interface RoleDao extends JpaRepository<Role, Long>,
		JpaSpecificationExecutor<Role> {

	
	public Role findByCodeOrName(String code, String name); 
}
