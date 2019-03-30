package cnyl.catlover.common.util;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;

public class CommonUtils {
	
	/**
	 * 参数不为空
	 * @param request
	 * @param params
	 * @return
	 */
	public static boolean paramsAuth(HttpServletRequest request,String... params){
		for(String p : params){
			if(request.getAttribute(p) != null){
				if(StrUtil.isNotBlank(request.getAttribute(p).toString())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 参数不为空
	 * @param params
	 * @return
	 */
	public static boolean paramsAuth(String... params){
		for(String p : params){
			if(StrUtil.isNotBlank(p)){
				return true;
			}
		}
		return false;
	}

}
