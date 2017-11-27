package com.shcx.user.userservice.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则验证工具类
 * 
 * @Title PatternUtil.java 
 * @description TODO 
 * @time 2017年11月24日 下午5:59:08 
 * @author LILJ 
 * @version 1.0
 */
public class PatternUtil {
	
	/**
	 * 验证手机号格式
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone){
		//验证手机号
		String regex = "1[34578]\\d{9}";
		return pattern(phone, regex);
	}
	
	/**
	 * 验证密码
	 * 
	 * @param password
	 * @return boolean
	 * @time 2017年11月24日 下午5:58:56 
	 * @author LILJ
	 */
	public static boolean checkPassword(String password){
		//只能包含数字和字母，并且长度在6-18位之间！
		String regex = "(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,18}";
		return pattern(password, regex);
	}
	
	/**
	 * 正则验证
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	private static boolean pattern(String str, String regex){
		try {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(str);
			return m.find();
		} catch (Exception e) {
		}
		return false;
	}
	
}
