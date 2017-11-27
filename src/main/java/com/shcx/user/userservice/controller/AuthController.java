package com.shcx.user.userservice.controller;

import io.jsonwebtoken.Claims;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.shcx.user.userservice.jwt.JwtHelper;
import com.shcx.user.userservice.pojo.User;
import com.shcx.user.userservice.service.UserService;
import com.shcx.user.userservice.util.StaticValues;
import com.shcx.user.userservice.util.StringUtils;

/**
 * 用户授权接口（登录）
 * 
 * @Title AuthController.java
 * @description TODO
 * @time 2017年11月22日 下午2:44:12
 * @author LILJ
 * @version 1.0
 */
@RestController
public class AuthController {

	@Resource
	private UserService userService;

	/**
	 * 定时修改过期用户的登录状态
	 * 
	 *  void
	 * @time 2017年11月22日 下午7:37:01 
	 * @author LILJ
	 */
	@Scheduled(cron = "0 0/1 * * * ? *")// 间隔1分钟执行
	public void updateExpireUserTaskCycle() {
		try {
			userService.updateExpiretimeUser();
		} catch (Exception e) {
		}
	}

	/**
	 * 退出,目前只能改变登录状态，Token可能依然有效
	 * 
	 * @param request
	 * @return JSONObject
	 * @time 2017年11月22日 下午7:25:01
	 * @author LILJ
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public JSONObject login(HttpServletRequest request) {
		JSONObject result = new JSONObject();
		result.put("code", 400);
		String auth = request.getHeader("Authorization");
		if (StringUtils.isEmpty(auth)) {
			result.put("message", "没有发现登录信息！");
			return result;
		}
		Claims claims = JwtHelper.parseJWT(auth);
		if (claims == null) {
			result.put("message", "已过期");
			return result;
		}
		try {
			String userid = claims.getAudience();
			User user = userService.findById(Long.valueOf(userid));
			if (user == null) {
				result.put("message", "Token信息不正确");
				return result;
			} else {
				result.put("code", 200);
				result.put("message", "注销成功！");
				user.setLogined(1);
				user.setExpiretime(0L);
				userService.save(user);
			}
		} catch (Exception e) {
			result.put("message", "Token信息不正确");
		}
		return result;
	}

	/**
	 * 根据Token获取本身信息
	 * 
	 * @param request
	 * @return JSONObject
	 * @time 2017年11月22日 下午7:25:01
	 * @author LILJ
	 */
	@RequestMapping(value = "/self", method = RequestMethod.GET)
	public JSONObject  myslef(HttpServletRequest request){
		JSONObject result = new JSONObject();
		result.put("code", 400);
		String auth = request.getHeader("Authorization");
		if (StringUtils.isEmpty(auth)) {
			result.put("message", "没有发现登录信息！");
			return result;
		}
		Claims claims = JwtHelper.parseJWT(auth);
		if (claims == null) {
			result.put("message", "已过期");
			return result;
		}
		try {
			String userid = claims.getAudience();
			User user = userService.findById(Long.valueOf(userid));
			if (user == null) {
				result.put("message", "Token信息不正确");
				return result;
			} else {
				result.put("code", 200);
				result.put("message", "获取成功！");
				result.put("user", user);
			}
		} catch (Exception e) {
			result.put("message", "Token信息不正确");
		}
		return result;
	}

	/**
	 * 用户登录（授权）
	 * 
	 * @param request
	 * @param user
	 * @return JSONObject
	 * @time 2017年11月22日 下午6:46:21
	 * @author LILJ
	 */
	@RequestMapping(value = "/auth", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public JSONObject login(HttpServletRequest request, @RequestBody User user) {
		JSONObject result = new JSONObject();
		if (user == null) {
			result.put("code", 401);
			result.put("message", "没有登录用户！");
			return result;
		}

		if (StringUtils.isEmpty(user.getUsername())
				&& StringUtils.isEmpty(user.getEmail())
				&& StringUtils.isEmpty(user.getPhone())) {
			result.put("code", 402);
			result.put("message", "用户名，邮箱，手机号必须任选其中一个！");
			return result;
		}
		if (StringUtils.isEmpty(user.getPassword())) {
			result.put("code", 403);
			result.put("message", "密码不能为空！");
			return result;
		}

		if (StringUtils.isNotEmpty(user.getUsername())) {
			user = userService.loginByUsername(user.getUsername(),
					user.getPassword());
		} else if (StringUtils.isNotEmpty(user.getEmail())) {
			user = userService
					.loginByEmail(user.getEmail(), user.getPassword());
		} else if (StringUtils.isNotEmpty(user.getPhone())) {
			user = userService
					.loginByPhone(user.getPhone(), user.getPassword());
		}

		if (user == null) {
			result.put("code", 404);
			result.put("message", "登录失败！登录信息不正确！");
			return result;
		}
		
		if(user.getConfirmed() == 1){//未确认（认证）用户
			result.put("code", 405);
			result.put("message", "未认证用户！");
			return result;
		}
		
		if(user.getLocked() == 0){//被锁定用户
			result.put("code", 406);
			result.put("message", "该用户已被锁定！");
			return result;
		}

		result.put("code", 200);
		result.put("message", "登录成功！");

		/**
		 * 创建JWT token
		 */
		long nowMillis = System.currentTimeMillis();
		Date nowDate = new Date(nowMillis);
		long expMillis = nowMillis + StaticValues.JWT_VALID_TIME;
		Date expDate = new Date(expMillis);
		String token = JwtHelper.createJWT(user.getUsername(), user
				.getId().toString(), user.getRole().getName(), user.getId()
				.toString(), StaticValues.JWT_ISSUER, nowDate, expDate);
		result.put("token", token);
		result.put("expire_time", expMillis);
		user.setLogined(0);// 修改已登录状态
		user.setExpiretime(expMillis);
		userService.save(user);
		return result;
	}
}
