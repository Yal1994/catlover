package cnyl.catlover.common.util;

import com.alibaba.fastjson.JSONObject;

import cnyl.catlover.common.constants.Code;

public class JSONUtils {
	
	public void JSONIgnoreFilter(Object object, String name){
		
	}
	
	public static  JSONObject defaultRet(){
		JSONObject ret = new JSONObject();
		ret.put("errCode", Code.OK);
		ret.put("errMsg",Code.OK_STR);
		return ret;
	}
	
	public static  JSONObject wrongParamsRet(){
		JSONObject ret = new JSONObject();
		ret.put("errCode", Code.WRONG_PARAMS);
		ret.put("errMsg",Code.WRONG_PARAMS_STR);
		return ret;
	}
	
	public static  JSONObject wrongParamsRet(String errMsg){
		JSONObject ret = new JSONObject();
		ret.put("errCode", Code.WRONG_PARAMS);
		ret.put("errMsg",errMsg);
		return ret;
	}

}
