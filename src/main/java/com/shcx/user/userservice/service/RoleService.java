package com.shcx.user.userservice.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shcx.user.userservice.dao.RoleDao;
import com.shcx.user.userservice.pojo.Role;
import com.shcx.user.userservice.util.StringUtils;


@Service("roleService")
public class RoleService {

	@Resource
	private RoleDao roleDao;
	
	public boolean deleteById(Long id){
		boolean b = true;
		try {
			roleDao.delete(id);
		} catch (Exception e) {
			b = false;
		}
		return b;
	}
	
	public Role findById(Long id){
		return roleDao.findOne(id);
	}
	
	public List<Role> findAll(Role role,
			Order... orders){
		Sort sort = null;
		if (orders != null && orders.length > 0)
			sort = new Sort(orders);
		return roleDao.findAll(getWhereClause(role), sort);
	}
	
	/**
	 * 分页查询
	 * 
	 * @param Role
	 * @param pageNo
	 * @param pageSize
	 * @param orders
	 * @return Page<Role>
	 * @time 2017年11月22日 下午7:29:18 
	 * @author LILJ
	 */
	public Page<Role> findPage(Role role, Integer pageNo, Integer pageSize,
			Order... orders) {
		Sort sort = null;
		if (orders != null && orders.length > 0)
			sort = new Sort(orders);
		Pageable pageable = new PageRequest(pageNo - 1, pageSize, sort);
		return roleDao.findAll(getWhereClause(role), pageable);
	}

	private Specification<Role> getWhereClause(final Role role) {
		return new Specification<Role>() {
			@Override
			public Predicate toPredicate(Root<Role> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (role == null) {
					return null;
				}
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(role.getCode())) {
					list.add(cb.like(root.get("code").as(String.class), "%"
							+ role.getCode().trim() + "%"));
				}
				if (StringUtils.isNotEmpty(role.getName())) {
					list.add(cb.like(root.get("name").as(String.class), "%"
							+ role.getName().trim() + "%"));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		};
	}
	
	public Role save(Role role){
		if(role == null){
			return null;
		}
		return roleDao.save(role);
	}

	public Role findByCodeOrName(String code, String name){
		return roleDao.findByCodeOrName(code, name);
	}
	
	/**
	 * 验证角色code或者名字不能重复
	 * 
	 * @param roleId
	 * @param code
	 * @param name
	 * @return boolean
	 * @time 2017年11月23日 上午10:31:41 
	 * @author LILJ
	 */
	public boolean checkRole(Long roleId,String code, String name){
		Role role = findByCodeOrName(code, name);
		if(role == null){
			return false;
		}
		if(role.getId() == roleId){
			return false;
		}
		return true;
	}
}
