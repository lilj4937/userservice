package com.shcx.user.userservice.util;

/**
 * 字符串工具类
 * 
 * @Title StringUtils.java 
 * @description TODO 
 * @time 2017年11月22日 下午4:00:01 
 * @author LILJ 
 * @version 1.0
 */
public class StringUtils {

	/**
	 * 判断字符串为空
	 * 
	 * @param str
	 * @return boolean
	 * @time 2017年11月22日 下午3:59:45 
	 * @author LILJ
	 */
	public static boolean isEmpty(String str){
		return str == null || "".equals(str.trim());
	}
	
	/**
	 * 判断字符串不为空
	 * 
	 * @param str
	 * @return boolean
	 * @time 2017年11月22日 下午4:04:40 
	 * @author LILJ
	 */
	public static boolean isNotEmpty(String str){
		return str != null && !"".equals(str.trim());
	}
}
