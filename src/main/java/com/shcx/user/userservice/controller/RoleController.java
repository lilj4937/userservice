package com.shcx.user.userservice.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.sf.json.JSONObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shcx.user.userservice.pojo.Role;
import com.shcx.user.userservice.service.RoleService;
import com.shcx.user.userservice.util.JSONConfigUtil;
import com.shcx.user.userservice.vo.PageVo;

/**
 * 角色接口
 * 
 * @Title UserController.java
 * @description TODO
 * @time 2017年11月22日 下午2:36:14
 * @author LILJ
 * @version 1.0
 */
@RestController
@RequestMapping("/role")
public class RoleController {
	
	@Resource
	private RoleService roleService;
	
	/**
	 * 注册用户
	 * 
	 * @param request
	 * @param role
	 * @return JSONObject
	 * @time 2017年11月22日 下午5:17:49
	 * @author LILJ
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public JSONObject register(HttpServletRequest request,
			@Valid @RequestBody Role role, BindingResult bindingResult) {
		JSONObject result = new JSONObject();
		if (role == null) {
			result.put("code", 400);
			result.put("message", "没有发现角色信息！");
			return result;
		}
		if (bindingResult.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				sb.append(((FieldError) objectError).getField() + " : ")
						.append(objectError.getDefaultMessage());
			}
			result.put("code", 401);
			result.put("message", sb.toString());
			return result;
		}

		if(roleService.checkRole(role.getId(), role.getCode(), role.getName())){
			result.put("code", 403);
			result.put("message", "角色已存在！");
			return result;
		}

		Long now = System.currentTimeMillis();
		if(role.getId() == null){
			role.setCreateDate(now);
			role.setLastUpdateDate(now);
		}else{
			Role oldRole = roleService.findById(role.getId());
			if(oldRole == null){
				result.put("code", 402);
				result.put("message", "没有找到需要修改的角色！");
				return result;
			}
			oldRole.setCode(role.getCode());
			oldRole.setName(role.getName());
			role = oldRole;
			role.setLastUpdateDate(now);
		}
		try {
			role = roleService.save(role);
			result.put("code", 200);
			result.put("message", "保存成功！");
		} catch (Exception e) {
			result.put("code", 404);
			result.put("message", "保存失败！");
		}
		return result;
	}
	
	/**
	 * 根据ID查询
	 * 
	 * @param request
	 * @param id
	 * @return JSONObject
	 * @time 2017年11月22日 下午6:39:34
	 * @author LILJ
	 */
	@RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
	public JSONObject findById(HttpServletRequest request, @PathVariable Long id) {
		JSONObject result = new JSONObject();
		if(id == null || id <= 0){
			result.put("code", 400);
			result.put("message", "角色ID输入不正确！");
			return result;
		}
		
		Role role = roleService.findById(id);
		if(role == null){
			result.put("code", 401);
			result.put("message", "没有发现角色！");
			return result;
		}

		result.put("code", 200);
		result.put("message", "查询成功！");
		result.put("role", JSONObject.fromObject(role,JSONConfigUtil.getNullPropJSONConfigFilter()));
		return result;
	}

	/**
	 * 根据ID删除
	 * 
	 * @param request
	 * @param id
	 * @return JSONObject
	 * @time 2017年11月22日 下午6:39:34
	 * @author LILJ
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public JSONObject deleteById(HttpServletRequest request, @PathVariable Long id) {
		JSONObject result = new JSONObject();
		if(id == null || id <= 0){
			result.put("code", 400);
			result.put("message", "角色ID输入不正确！");
			return result;
		}
		boolean b = roleService.deleteById(id);
		if(b){
			result.put("code", 200);
			result.put("message", "删除成功！");
			return result;
		}else{
			result.put("code", 500);
			result.put("message", "删除成功！");
		}
		return result;
	}
	
	/**
	 * 分页查询
	 * 
	 * @param request
	 * @param code
	 * @param name
	 * @return List<Role>
	 * @time 2017年11月22日 下午6:37:57
	 * @author LILJ
	 */
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public PageVo<Role> findPage(
			HttpServletRequest request,
			@RequestParam(value = "code", defaultValue = "") String code,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "pageno", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pagesize", defaultValue = "20") Integer pageSize) {
		Role role = new Role();
		role.setCode(code);
		role.setName(name);
		Order order = new Order(Direction.DESC, "lastUpdateDate");// 根据最后更新时间排序
		Page<Role> page = roleService.findPage(role, pageNo, pageSize, order);
		PageVo<Role> result = new PageVo<Role>();
		result.setPageNo(pageNo);
		result.setPageSize(pageSize);
		result.setData(page.getContent());
		result.setTotal(page.getTotalElements());
		result.setPages(page.getTotalPages());
		return result;
	}
	
	/**
	 * 查询所有
	 * 
	 * @param request
	 * @param code
	 * @param name
	 * @return List<Role>
	 * @time 2017年11月22日 下午6:37:57
	 * @author LILJ
	 */
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public List<Role> queryAll(
			HttpServletRequest request,
			@RequestParam(value = "code", defaultValue = "") String code,
			@RequestParam(value = "name", defaultValue = "") String name) {
		Role role = new Role();
		role.setCode(code);
		role.setName(name);
		Order order = new Order(Direction.DESC, "lastUpdateDate");// 根据最后更新时间排序
		return roleService.findAll(role, order);
	}
}
