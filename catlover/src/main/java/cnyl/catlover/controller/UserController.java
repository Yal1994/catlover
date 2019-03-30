package cnyl.catlover.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cnyl.catlover.common.constants.YesOrNo;
import cnyl.catlover.common.util.FileUtils;
import cnyl.catlover.common.util.JSONUtils;
import cnyl.catlover.domain.blog.Attachment;
import cnyl.catlover.domain.blog.Blog;
import cnyl.catlover.domain.chat.ChatRoom;
import cnyl.catlover.domain.relation.UserUserStar;
import cnyl.catlover.domain.user.User;
import cnyl.catlover.repository.AttachmentRepository;
import cnyl.catlover.repository.ChatRoomRepository;
import cnyl.catlover.repository.CommentRepository;
import cnyl.catlover.repository.UserBlogStarRepository;
import cnyl.catlover.repository.UserRepository;
import cnyl.catlover.repository.UserUserStarRepository;
import cnyl.catlover.service.BlogService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping(value="/wx",produces = "application/json;charset=UTF-8")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserUserStarRepository userUserStarRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserBlogStarRepository userBlogStarRepository;
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private BlogService blogService;
	
	@Value("${catlover.pager.pageSize}")
	private int pageSize;
	
	@Value("${catlover.profile}")
	private String filePrefix ;
	
	@Value("${catlover.imgAddr}")
	private String resImgAddr;
	
	@Value("${catlover.viAddr}")
	private String resVideoAddr;
	
	
	@GetMapping("/user")
	public String userDetail(String webId,String targetId){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(StrUtil.isNotBlank(targetId) && StrUtil.isNotBlank(webId)){
			Optional<User> optUser= userRepository.findByWebId(webId);
			Optional<User> targetUserOpt= userRepository.findByWebId(targetId);
			
			if(optUser.isPresent() && targetUserOpt.isPresent()){
				User targetUser = targetUserOpt.get();
				Optional<UserUserStar> uus = userUserStarRepository.findByStarterIdAndTargetId(webId,targetId);
				
				ret = JSONUtils.defaultRet();
				boolean isStar = false;
				if(uus.isPresent()){
					isStar = true;
				}
				ret.put("target", targetUser);
				ret.put("isStar", isStar);
				return ret.toJSONString();
			}
		}
		log.info(webId+targetId);
		return ret.toJSONString();
	}
	
	@PostMapping("/au/user/star")
	public String star(String isStar,String webId,String targetId){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(StrUtil.isNotBlank(targetId) && StrUtil.isNotBlank(webId) && StrUtil.isNotBlank(isStar)){
			Optional<User> optUser = userRepository.findByWebId(webId);
			Optional<User> optTarget = userRepository.findByWebId(targetId);
			if(optUser.isPresent() && optTarget.isPresent()){
				User user = optUser.get();
				User target = optTarget.get();
				if(Boolean.valueOf(isStar)){
					UserUserStar uus = new UserUserStar();
					uus.setStarterId(webId);
					uus.setTargetId(targetId);
					uus.setStarTime(new Date());
					userUserStarRepository.save(uus);
					user.setStars(user.getStars() + 1);
					target.setFollows(target.getFollows() + 1);
				}else{
					userUserStarRepository.deleteByStarterIdAndTargetId(webId,targetId);
					user.setStars(user.getStars() - 1 < 0 ? 0 : user.getStars() - 1);
					target.setFollows((target.getFollows() - 1) < 0 ? 0 : target.getFollows() - 1);
				}
				
				User userNew = userRepository.save(user);
				User targetNew = userRepository.save(target);
				
				ret = JSONUtils.defaultRet();
				ret.put("userInfo", userNew);
				ret.put("targetInfo", targetNew);
				return ret.toJSONString();
			}
		}
		return ret.toJSONString();
	}
	
	
	@GetMapping("/user/blogs")
	public String getBlogs(String webId,String targetId,String curPage){
		JSONObject ret = JSONUtils.wrongParamsRet();
		
		if(StrUtil.isNotBlank(webId) && StrUtil.isNotBlank(targetId) && NumberUtil.isNumber(curPage)){
			Optional<User> user = userRepository.findByWebId(webId);
			if(user.isPresent()){
				Optional<User> target = userRepository.findByWebId(targetId);
				if(target.isPresent()){
					int curPageI = Integer.valueOf(curPage) < 1 ? 1 : Integer.valueOf(curPage);
					Page<Blog> blogs = blogService.findByPublisher(target.get(),curPageI,pageSize);
					ret = JSONUtils.defaultRet();
					ret.put("targetInfo", target.get());
					ret.put("targetBlogs", blogs.getContent());
				}
			}
			
		}
		return JSON.toJSONString(ret,SerializerFeature.DisableCircularReferenceDetect);
	}
	
	
	@PostMapping("/au/user/bgimg")
	public JSONObject postBackgrounImg(@RequestParam("file") MultipartFile bgimgFile,String webId){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(StrUtil.isNotEmpty(webId)){
			Optional<User> optUser = userRepository.findByWebId(webId);
			if(optUser.isPresent()){
				User user = optUser.get();
				try {
					if (bgimgFile != null) {
						String orginalFileName = bgimgFile.getOriginalFilename();
						String originalFileSuffix = orginalFileName.substring(orginalFileName.lastIndexOf("."));
						String fileName = IdUtil.fastSimpleUUID() + originalFileSuffix;
						String filePath = "";
						String addr = "";	
						ret = JSONUtils.defaultRet();
						if (FileUtils.isImage(originalFileSuffix)) {
							filePath = filePrefix + "/image/";
							addr = this.resImgAddr + "/" + fileName;
							String oldBgImg = user.getBgImg();
							user.setBgImg(addr);
							User retUser = userRepository.save(user);
							if(StrUtil.isNotBlank(oldBgImg)){
								this.deleteAttach(oldBgImg);
							}
							ret.put("userInfo",retUser);
						}else{
							return JSONUtils.wrongParamsRet("请上传图片格式的文件");
						}
						
						bgimgFile.transferTo(new File( filePath+ fileName));
						return ret;
					}
				} catch (IllegalStateException e) {
					log.error(e.getMessage());
					return ret;
				} catch (IOException e) {
					log.error(e.getMessage());
					return ret;
				}
			}
		}
		return ret;
	}
	
	@PostMapping("/au/user/openChat")
	public JSONObject openChat(String openChat,String webId){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(StrUtil.isNotBlank(webId) && StrUtil.isNotBlank(openChat)){
			Optional<User> optUser = userRepository.findByWebId(webId);
			if(optUser.isPresent()){
				User user = optUser.get();
				openChat = YesOrNo.YES.equals(openChat) ? YesOrNo.YES : YesOrNo.NO;
				user.setOpenChat(openChat);
				User retUser = userRepository.save(user);
				ret =  JSONUtils.defaultRet();
				ret.put("userInfo", retUser);
				return ret;
			}
		}
		return ret;
	}
	/**
	 * 如果已经开启私聊则需要返回对应的房间号
	 * @param targetId
	 * @return
	 */
	@GetMapping("/user/openChat")
	public String openChatOrNot(String targetId,String webId){
		JSONObject ret = JSONUtils.defaultRet();
		Optional<User> optUser = userRepository.findByWebId(targetId);
		Optional<User> optSender = userRepository.findByWebId(webId);
		boolean isOpen = false;
		if(optUser.isPresent() && optSender.isPresent()){
			User reciver = optUser.get();
			User sender = optSender.get();
			if(YesOrNo.NO.equals(sender.getOpenChat())){
				sender.setOpenChat(YesOrNo.YES);
				sender = userRepository.save(sender);
			}
			if(YesOrNo.YES.equals(optUser.get().getOpenChat())){
				isOpen = true;
				ChatRoom ownerRoom = chatRoomRepository.findByOwnerIdAndReciverId(webId, targetId);
				ChatRoom senderRoom = chatRoomRepository.findByOwnerIdAndReciverId(targetId, webId);
				if(ownerRoom == null){
					ownerRoom = new ChatRoom();
					ownerRoom.setHasNew(false);
					ownerRoom.setOwner(sender);
					ownerRoom.setReciver(reciver);
					ownerRoom.setUnreadMessage(null);
					ownerRoom = chatRoomRepository.save(ownerRoom);
				}
				
				if(senderRoom == null){
					senderRoom = new ChatRoom();
					senderRoom.setHasNew(false);
					senderRoom.setOwner(reciver);
					senderRoom.setReciver(sender);
					senderRoom.setUnreadMessage(null);
					chatRoomRepository.save(senderRoom);
				}
				
				ret.put("room", ownerRoom);
			}
		}
		ret.put("isOpen", isOpen);
		
		log.info(targetId);
		return ret.toJSONString();
	}
	
	
	@PostMapping("/au/user/blogs/del")
	public String deleteBlogs(String webId, String blogId){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(StrUtil.isNotBlank(blogId) && StrUtil.isNotBlank(webId)){
			Optional<User> optUser = userRepository.findByWebId(webId);
			Optional<Blog> optBlog = blogService.findById(blogId);
			if(optUser.isPresent() && optBlog.isPresent()){
				User user = optUser.get();
				if(user.getWebId().equals(optBlog.get().getPublisher().getWebId())){
					Blog blog = optBlog.get();
					attachmentRepository.deleteByBlogId(blog.getId());
					//commentRepository.deleteByBlogId(blog.getId());
					userBlogStarRepository.deleteByWebIdAndBlogId(webId,blog.getId());
					blogService.deleteById(blog.getId());
					for(Attachment at : blog.getAttachments()){
						deleteAttach(at.getUrl());
					}
					user.setBlogs((user.getBlogs() - 1) < 0 ? 0 : user.getBlogs() - 1);
					User retUser = userRepository.save(user);
					ret = JSONUtils.defaultRet();
					ret.put("userInfo", retUser);
					return ret.toJSONString();
				}
			}
		}
		return ret.toJSONString();
	}
	
	@GetMapping("/user/relation")
	public String userRelation(String webId, String targetId, String isStar,String pageNum){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(paramsNotBlank(webId,targetId,isStar,pageNum) && NumberUtil.isNumber(pageNum)){
			int curPage = Integer.valueOf(pageNum);
			ret = JSONUtils.defaultRet();
			List<User> userList = new ArrayList<User>();
			if(Boolean.valueOf(isStar)){
				Page<UserUserStar> uusList = userUserStarRepository.findByStarterId(targetId,PageRequest.of(curPage - 1, pageSize, Direction.DESC, "starTime"));
				for(UserUserStar uus : uusList.getContent()){
					Optional<User> optUser = userRepository.findByWebId(uus.getTargetId());
					if(optUser.isPresent()){
						userList.add(optUser.get());
					}
				}
			}else{
				Page<UserUserStar> uusList = userUserStarRepository.findByTargetId(targetId,PageRequest.of(curPage - 1, pageSize, Direction.DESC, "starTime"));
				for(UserUserStar uus : uusList.getContent()){
					Optional<User> optUser = userRepository.findByWebId(uus.getStarterId());
					if(optUser.isPresent()){
						userList.add(optUser.get());
					}
				}
			}
			ret.put("userList", userList);
			return ret.toJSONString();
		}
		
		log.info(ret.toJSONString());
		return ret.toJSONString();
	}
	
	
	private boolean paramsNotBlank(String... params){
		for(String str : params){
			if(StrUtil.isBlank(str)){
				return false;
			}
		}
		return true;
	}
	
	private void deleteAttach(String addr){
		String suffix = addr.substring(addr.lastIndexOf(FileUtils.DOT));
		if(FileUtils.isImage(suffix)){
			String path = filePrefix + addr.replace("img", "image");
			if(FileUtils.exist(path)){
				boolean b = FileUtils.del(path);
				log.info("delete attachment {}:{}",path,b);
			}
		}
		
		if(FileUtils.isVideo(suffix)){
			String path = filePrefix + addr.replace("vi", "video");
			if(FileUtils.exist(path)){
				boolean b = FileUtils.del(path);
				log.info("delete attachment {}:{}",path,b);
			}
		}
	}
}
