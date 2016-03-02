package com.example.scancodecollectmoney.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * com.example.scancodecollectmoney.utils
 * @Email zhaoq_hero@163.com
 * @author zhaoQiang : 2016-3-1
 */
public class MapUtils {
	
	/**
	 * 对map进行  排序    按照  键的字母顺序进行排序  返回值拼接的字符串
	 * @param params
	 * @return
	 */
	public static String createLinkString(Map<String, String> map) {
		
		List<String> keys = new ArrayList<String>(map.keySet());//获取  所有键
		Collections.sort(keys);//进行排序
		
		/**
		 * 遍历获取  值
		 */
		String pStr = "";
		
		for(int i= 0;i<keys.size();i++){
			
			String key = keys.get(i);
			String value = map.get(key);
			
			/**
			 * sign 已经被添加过   则continue   否则  添加到字符串中
			 */
			if("sign".equals("key")){
				continue;
			}
			
			pStr = pStr + value;
		}
		return pStr;
	}


	/**
	 * 通过json  获取其中的参数对     并封装成   map集合
	 * @param jsonString
	 * @return
	 */
	public static Map<String, String> getParamsFromJson(String jsonString) {
		Map<String,String> result  = new HashMap<String,String>();

		JSONObject obj = JSON.parseObject(jsonString);
		
		for(Entry<String,Object> par : obj.entrySet()){
			result.put(par.getKey(), (String)par.getValue());
		}
		return result;
	}

}
