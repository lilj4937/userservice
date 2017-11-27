package com.shcx.user.userservice.controller;

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
import com.shcx.user.userservice.pojo.User;
import com.shcx.user.userservice.service.RoleService;
import com.shcx.user.userservice.service.UserService;
import com.shcx.user.userservice.util.JSONConfigUtil;
import com.shcx.user.userservice.util.PasswordHash;
import com.shcx.user.userservice.util.PatternUtil;
import com.shcx.user.userservice.util.StaticValues;
import com.shcx.user.userservice.util.StringUtils;
import com.shcx.user.userservice.vo.PageVo;

/**
 * 用户接口
 * 
 * @Title UserController.java
 * @description TODO
 * @time 2017年11月22日 下午2:36:14
 * @author LILJ
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;

	/**
	 * 根据ID查询
	 * 
	 * @param request
	 * @param id
	 * @return JSON
	 * @time 2017年11月22日 下午6:39:34
	 * @author LILJ
	 */
	@RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
	public JSONObject findById(HttpServletRequest request, @PathVariable Long id) {
		JSONObject result = new JSONObject();
		if(id == null || id <= 0){
			result.put("code", 400);
			result.put("message", "用户ID输入不正确！");
			return result;
		}
		User user = userService.findById(id);
		if(user == null){
			result.put("code", 401);
			result.put("message", "没有发现对应用户！");
			return result;
		}
		user.setPassword(null);
		result.put("code", 200);
		result.put("message", "查询成功！");
		result.put("user", JSONObject.fromObject(user,JSONConfigUtil.getNullPropJSONConfigFilter()));
		return result;
	}
	

	/**
	 * 根据ID删除
	 * 
	 * @param request
	 * @param id
	 * @return JSON
	 * @time 2017年11月22日 下午6:39:34
	 * @author LILJ
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public JSONObject deleteById(HttpServletRequest request, @PathVariable Long id) {
		JSONObject result = new JSONObject();
		if(id == null || id <= 0){
			result.put("code", 400);
			result.put("message", "用户ID输入不正确！");
			return result;
		}
		boolean b = userService.deleteById(id);
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
	 * 修改密码
	 * 
	 * @param request
	 * @param username
	 * @param email
	 * @param phone
	 * @param password
	 * @param newpassword
	 * @return JSONObject
	 * @time 2017年11月24日 下午5:44:40
	 * @author LILJ
	 */
	@RequestMapping(value = "/password", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public JSONObject updatePassword(HttpServletRequest request,
			@RequestParam(defaultValue = "") String username,
			@RequestParam(defaultValue = "") String email,
			@RequestParam(defaultValue = "") String phone,
			@RequestParam String password, @RequestParam String newpassword) {
		JSONObject result = new JSONObject();

		if (StringUtils.isEmpty(username) && StringUtils.isEmpty(email)
				&& StringUtils.isEmpty(phone)) {
			result.put("code", 401);
			result.put("message", "用户名，邮箱，手机号必须任选其中一个！");
			return result;
		}
		if (StringUtils.isEmpty(password)) {
			result.put("code", 402);
			result.put("message", "旧密码不能为空！");
			return result;
		}
		if (StringUtils.isEmpty(newpassword)) {
			result.put("code", 403);
			result.put("message", "新密码不能为空！");
			return result;
		}
		if(!PatternUtil.checkPassword(newpassword)){
			result.put("code", 403);
			result.put("message", "新密码密码必须同时且只能包含数字和字母，长度在6-18位之间！");
			return result;
		}
		
		User user = null;
		if (StringUtils.isNotEmpty(username)) {
			user = userService.findByUsername(username);
		} else if (StringUtils.isNotEmpty(email)) {
			user = userService.findByEmail(email);
		} else if (StringUtils.isNotEmpty(phone)) {
			user = userService.findByPhone(phone);
		}

		if (user == null) {
			result.put("code", 404);
			result.put("message", "用户名，邮箱或手机号输入不正确！");
			return result;
		}

		/**
		 * 验证旧密码是否正确
		 */
		boolean b = PasswordHash.validatePassword(password, user.getPassword());
		if (!b) {
			result.put("code", 405);
			result.put("message", "旧密码输入不正确！");
			return result;
		}
		try {
			user.setPassword(PasswordHash.createHash(newpassword));
			user.setLastUpdateDate(System.currentTimeMillis());
			userService.save(user);
			result.put("code", 200);
			result.put("message", "修改成功！");
		} catch (Exception e) {
			result.put("code", 406);
			result.put("message", "修改失败！");
		}

		return result;
	}

	/**
	 * 查询所有
	 * 
	 * @param request
	 * @param username
	 * @param email
	 * @param phone
	 * @return List<User>
	 * @time 2017年11月22日 下午6:38:03
	 * @author LILJ
	 */
	/*
	 * @RequestMapping(value = "/query", method = RequestMethod.GET) public
	 * List<User> findAll(HttpServletRequest request,
	 * 
	 * @RequestParam(defaultValue = "") String username,
	 * 
	 * @RequestParam(defaultValue = "") String email,
	 * 
	 * @RequestParam(defaultValue = "") String phone) { User user = new User();
	 * user.setUsername(username); user.setEmail(email); user.setPhone(phone);
	 * Order order = new Order(Direction.DESC, "lastUpdateDate");//根据最后更新时间排序
	 * return userService.findAll(user, order); }
	 */

	/**
	 * 分页查询
	 * 
	 * @param request
	 * @param username
	 * @param email
	 * @param phone
	 * @return List<User>
	 * @time 2017年11月22日 下午6:37:57
	 * @author LILJ
	 */
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public PageVo<User> findPage(
			HttpServletRequest request,
			@RequestParam(value = "username", defaultValue = "") String username,
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "phone", defaultValue = "") String phone,
			@RequestParam(value = "pageno", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pagesize", defaultValue = "20") Integer pageSize) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPhone(phone);
		Order order = new Order(Direction.DESC, "lastUpdateDate");// 根据最后更新时间排序
		Page<User> page = userService.findPage(user, pageNo, pageSize, order);
		PageVo<User> result = new PageVo<User>();
		result.setPageNo(pageNo);
		result.setPageSize(pageSize);
		if (page != null && page.getContent() != null && page.getContent().size() > 0) {
			for (User u : page.getContent()) {
				u.setPassword(null);
			}
		}
		result.setData(page.getContent());
		result.setTotal(page.getTotalElements());
		result.setPages(page.getTotalPages());
		return result;
	}

	/**
	 * 注册用户
	 * 
	 * @param request
	 * @param user
	 * @return User
	 * @time 2017年11月22日 下午5:17:49
	 * @author LILJ
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public JSONObject register(HttpServletRequest request,
			@Valid @RequestBody User user, BindingResult bindingResult) {
		JSONObject result = new JSONObject();
		if (user == null) {
			result.put("code", 400);
			result.put("message", "没有需要注册用户！");
			return result;
		}

		if (StringUtils.isEmpty(user.getUsername())) {
			result.put("code", 401);
			result.put("message", "用户名不能为空！");
			return result;
		}

		if (StringUtils.isEmpty(user.getPassword())) {
			result.put("code", 402);
			result.put("message", "密码不能为空！");
			return result;
		}
		if(!PatternUtil.checkPassword(user.getPassword())){
			result.put("code", 403);
			result.put("message", "密码必须同时且只能包含数字和字母，长度在6-18位之间！");
			return result;
		}
		
		if(StringUtils.isNotEmpty(user.getPhone()) && !PatternUtil.checkPhone(user.getPhone())){
			result.put("code", 404);
			result.put("message", "手机号格式不正确！");
			return result;
		}
		Role role = null;
		if (user.getRole() != null && user.getRole().getId() != null) {
			role = roleService.findById(user.getRole().getId());
			if(role == null){
				result.put("code", 404);
				result.put("message", "角色不存在！");
				return result;
			}
		}else{
			role = roleService.findByCodeOrName(StaticValues.USER_ROLE_CODE, StaticValues.USER_ROLE_NAME);
			user.setRole(role);
		}
		if(role != null && StaticValues.ADMIN_ROLE_CODE.equals(role.getCode())){
			result.put("code", 405);
			result.put("message", "不可以设置超级管理员角色！");
			return result;
		}
		
		if (bindingResult.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				sb.append(((FieldError) objectError).getField() + " : ")
						.append(objectError.getDefaultMessage());
			}
			result.put("message", sb.toString());
			return result;
		}

		Long now = System.currentTimeMillis();
		user.setConfirmed(1);
		user.setLocked(0);
		user.setLogined(1);
		user.setPassword(PasswordHash.createHash(user.getPassword()));
		user.setCreateDate(now);
		user.setLastUpdateDate(now);
		return userService.editUser(user);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public JSONObject update(HttpServletRequest request,
			@Valid @RequestBody User user, BindingResult bindingResult) {
		JSONObject result = new JSONObject();
		if (user == null) {
			result.put("code", 400);
			result.put("message", "没有需要修改用户！");
			return result;
		}

		if (user.getId() == null) {
			result.put("code", 400);
			result.put("message", "用户ID不能为空！");
			return result;
		}

		if (bindingResult.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				sb.append(((FieldError) objectError).getField() + " : ")
						.append(objectError.getDefaultMessage());
			}
			result.put("message", sb.toString());
			return result;
		}

		return userService.editUser(user);
	}

}
