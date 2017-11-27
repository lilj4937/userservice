package com.shcx.user.userservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志信息输出工具类
 * 
 * @Title LoggerUtil.java 
 * @description TODO 
 * @time 2017年11月23日 上午10:37:39 
 * @author LILJ 
 * @version 1.0
 */
public class LoggerUtil {

	private final static Logger logger = LoggerFactory
			.getLogger(LoggerUtil.class);

	public final static void debug(String message, Throwable e) {
		logger.debug(message, e);
	}

	public final static void debug(String message) {
		logger.debug(message);
	}

	public final static void info(String message) {
		logger.info(message);
	}

	public final static void warn(String message, Throwable e) {
		logger.warn(message, e);
	}

	public final static void warn(String message) {
		logger.warn(message);
	}

	public final static void error(String mssage, Throwable e) {
		logger.error(mssage, e);
	}

	public final static void error(String mssage) {
		logger.error(mssage);
	}

}
