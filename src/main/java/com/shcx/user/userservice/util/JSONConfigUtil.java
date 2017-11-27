package com.shcx.user.userservice.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JSONConfigUtil {
	private static JsonConfig nullPropJSONConfigFilter;
	public static JsonConfig getNullPropJSONConfigFilter() {
		if (nullPropJSONConfigFilter == null) {
			nullPropJSONConfigFilter = new JsonConfig();
			nullPropJSONConfigFilter.setJsonPropertyFilter(JSONNullPropertyFilter.getInstance());
		}
		return nullPropJSONConfigFilter;
	}
	/**
	 * @param path /excel/imp/yjnetworik.json
	 * @return
	 */
	public static JSONObject getJSONObject(String path){
		StringBuffer sb = new StringBuffer();
		try {
			FileReader fr = new FileReader(readFile(path));
			BufferedReader br = new BufferedReader(fr);
			while (true) {
				String str = br.readLine();
				if (str == null) {
					break;
				} else {
					sb.append(str);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//System.out.println(sb.toString());
		JSONObject jo = JSONObject.fromObject(sb.toString());
		return jo;
	}
	private static File readFile(String path){
		File file=new File(path);
		if(!file.isAbsolute()){
			try {
				String s=JSONConfigUtil.class.getResource(path).toURI().getPath();
				file=new File(s);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return null;
			}
		}
		return file;
	}
	public static boolean isJsonPathOrClass(String path){
		boolean b=true;
		JSONObject jo=JSONConfigUtil.getJSONObject(path);
		String clazzName=jo.getString("clazzName");
		JSONArray ja=jo.getJSONArray("columns");
		Class<?> cla=null;
			try {
			 cla =  Class.forName(clazzName);
			}catch (ClassNotFoundException e) {
				
				System.out.println(e.getMessage());
				e.printStackTrace();
				return false;
			}
		for(int i=0;i<ja.size();i++){
			JSONObject j=ja.getJSONObject(i);
			String getName=j.getString("data");
			try {
				cla.getDeclaredField(getName);
			} catch (SecurityException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				b=false;
			} catch (NoSuchFieldException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				b=false;
			}
		}
		return b;
	}
}
