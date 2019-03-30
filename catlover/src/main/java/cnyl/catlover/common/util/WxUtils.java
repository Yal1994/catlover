package cnyl.catlover.common.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

public class WxUtils {
	
	private static String codeToSessionUrl = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";
	
	public static String codeToSession(String appid,String secret,String code){
		
		String url = StrUtil.format(codeToSessionUrl, appid, secret, code);
		String loginResult = null;
		try {
			loginResult = HttpUtil.get(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginResult;
	}
	
	/**
	 * 解密数据
	 * @return
	 * @throws Exception
	 */
	public static boolean decryptUserInfo(String appId, String encryptedData, String sessionKey, String iv){
		boolean isMatched = false;
		try {
			AesKit aes = new AesKit();  
		    byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));  
		    if(null != resultByte && resultByte.length > 0){  
		        String result = new String(WxPKCS7Encoder.decode(resultByte));  
		    	JSONObject jsonObject = JSON.parseObject(result);
		    	String decryptAppid = jsonObject.getJSONObject("watermark").getString(appId);
		    	if(appId.equals(decryptAppid)){
		    		isMatched = true;
		    	}
	        }  
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return isMatched;
	}
}
