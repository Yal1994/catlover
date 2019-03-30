package cnyl.catlover.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cnyl.catlover.common.constants.YesOrNo;
import cnyl.catlover.common.util.WxUtils;
import cnyl.catlover.domain.user.User;
import cnyl.catlover.domain.user.UserToken;
import cnyl.catlover.domain.user.WxLoginResult;
import cnyl.catlover.repository.UserRepository;
import cnyl.catlover.repository.UserTokenRepository;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value="/wxlogin",produces = "application/json;charset=UTF-8")
public class WxLoginController {
	
	@Value("${catlover.token.expired}")
	private int expiredTime;
	
	@Value("${catlover.token.key}")
	private String tokenKey;
	
	@Value("${catlover.wx.secret}")
	private  String secret;
	
	@Value("${catlover.wx.appid}")
	private  String appid;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserTokenRepository tokenRepository;
	
	@RequestMapping("/")
	@ResponseBody
	public String wxLogin(String code, String userInfo){
		
		JSONObject userObj = JSONObject.parseObject(userInfo).getJSONObject("userInfo");
		JSONObject ret = new JSONObject();
		
		if(StringUtils.isEmpty(code)){
			ret.put("errCode",40001);
			ret.put("errMsg", "Login Code is Empty!");
			ret.put("userInfo",userObj);
			return ret.toJSONString();
		}
		
		String restr = WxUtils.codeToSession(appid,secret,code);
		
		if(StringUtils.isEmpty(restr)){
			ret.put("errCode",40002);
			ret.put("errMsg", "Code To Seesion Validation Fail! Result Str is Empty!");
			ret.put("userInfo",userObj);
			return ret.toJSONString();
		}
		
		WxLoginResult loginResult = JSONObject.parseObject(restr, WxLoginResult.class);
		if(loginResult.getErrCode() == null || YesOrNo.NO.equals(loginResult.getErrCode())){
			loginResult.setErrCode(YesOrNo.ZERO);
			loginResult.setErrMsg(YesOrNo.OK);
			//登陆成功..
			String openId = loginResult.getOpenId();
			User user = userRepository.findByOpenId(openId);
			if(user == null){
				user = new User();
				user.setOpenId(openId);
				user.setStars(0);
				user.setFollows(0);
				//user.setUnionId(loginResult.getUnionId());
				user.setAvatarUrl(userObj.getString("avatarUrl"));
				user.setGender(userObj.getString("gender"));
				user.setNickName(userObj.getString("nickName"));
				user.setRegistDate(new Date());
				user.setLastVisit(user.getRegistDate());
				user.setWebId(IdUtil.simpleUUID());
				user.setBlogs(0);
				user.setBgImg("");
			}
			else {
				user.setAvatarUrl(userObj.getString("avatarUrl"));
				user.setGender(userObj.getString("gender"));
				user.setNickName(userObj.getString("nickName"));
				user.setLastVisit(new Date());
			}
			
			User retUser = userRepository.save(user);
			Optional<UserToken> optUserToken = tokenRepository.findByUserWebId(retUser.getWebId());
			String token = "";
			if(optUserToken.isPresent()){
				UserToken userToken = optUserToken.get();
				userToken.setExpiredTime(DateUtil.offsetHour(new Date(), expiredTime));
				userToken.setSessionKey(loginResult.getSessionKey());
				token = IdUtil.simpleUUID();
				userToken.setToken(token);
				tokenRepository.save(userToken);
			}else{
				UserToken userToken = new UserToken();
				userToken.setExpiredTime(DateUtil.offsetDay(new Date(), expiredTime));
				userToken.setSessionKey(loginResult.getSessionKey());
				userToken.setUserWebId(retUser.getWebId());
				token = IdUtil.simpleUUID();
				userToken.setToken(token);
				tokenRepository.save(userToken);
			}
			
			ret.put("token",token);
			ret.put("userInfo",retUser);
		}
		
//		else{
//			if("40029".equals(loginResult.getErrCode())){
//				
//			}
//			
//			if("1".equals(loginResult.getErrCode())){
//				
//			}
//			
//			if("45011".equals(loginResult.getErrCode())){
//				
//			}
//		}
		
		
		ret.put("errCode",loginResult.getErrCode());
		ret.put("errMsg", loginResult.getErrMsg());
		//ret.put("userInfo",userObj);
		
		return ret.toJSONString();
		
	}
	
	
	

}
