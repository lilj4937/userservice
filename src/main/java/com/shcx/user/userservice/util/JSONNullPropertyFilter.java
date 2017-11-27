package com.shcx.user.userservice.util;

import java.util.List;

import net.sf.json.util.PropertyFilter;

public class JSONNullPropertyFilter implements PropertyFilter {
	private JSONNullPropertyFilter(){}

	public boolean apply(Object target, String propName, Object propValue) {
		if(propValue == null){
			return true;
		} else if (propValue instanceof List) {
			List<?> list = (List<?>) propValue;
			if(list.size() == 0){
				return true;
			}
		}else if ("".equals(propValue.toString().trim())) {
			return true;
		}
		return false;
	}
	
	private static JSONNullPropertyFilter instance;
	public static JSONNullPropertyFilter getInstance(){
		if (instance == null) {
			instance = new JSONNullPropertyFilter();
		}
		return instance;
	}
}
