package cnyl.catlover.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import cnyl.catlover.common.constants.BlogType;
import cnyl.catlover.common.constants.CommentType;
import cnyl.catlover.common.constants.YesOrNo;
import cnyl.catlover.common.util.FileUtils;
import cnyl.catlover.common.util.JSONUtils;
import cnyl.catlover.domain.blog.Attachment;
import cnyl.catlover.domain.blog.Blog;
import cnyl.catlover.domain.blog.Comment;
import cnyl.catlover.domain.blog.CommentChild;
import cnyl.catlover.domain.relation.UserBlogStar;
import cnyl.catlover.domain.user.User;
import cnyl.catlover.repository.AttachmentRepository;
import cnyl.catlover.repository.BlogRepository;
import cnyl.catlover.repository.CommentChildRepository;
import cnyl.catlover.repository.CommentRepository;
import cnyl.catlover.repository.UserBlogStarRepository;
import cnyl.catlover.repository.UserRepository;
import cnyl.catlover.service.BlogService;
import cnyl.catlover.service.CommentService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping(value="/wx",produces = "application/json;charset=UTF-8")
public class BlogController {
	
	@Autowired
	private BlogService blogService;
	
	@Autowired
	private BlogRepository blogRepository;
	
	@Autowired
	private UserBlogStarRepository userBlogStarRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CommentChildRepository childRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private AttachmentRepository attachRepository;
	
	@Autowired
	private CommentService commentService;
	
	
	
	@Value("${catlover.pager.pageSize}")
	private int pageSize;
	
	@Value("${catlover.profile}")
	private String filePrefix ;
	
	@Value("${catlover.imgAddr}")
	private String resImgAddr;
	
	@Value("${catlover.viAddr}")
	private String resViAddr;
	
	@GetMapping("/blog/list")
	public String getBlogs(String page){
		
		JSONObject ret = new JSONObject();
		
		if(NumberUtil.isNumber(page)){
			int curPage = Integer.valueOf(page) > 1 ? Integer.valueOf(page) : 1;
			Page<Blog> blogs = blogService.getBlogs(curPage, pageSize);
			
			if(blogs != null){
				ret.put("blogsList",blogs.getContent());
			}
			
			ret.put("errCode", YesOrNo.ZERO);
			ret.put("errMsg", YesOrNo.OK);
			return JSON.toJSONString(ret,SerializerFeature.DisableCircularReferenceDetect);
		}
		ret.put("errCode", "40001");
		ret.put("errMsg", "WRONG PARAMETERS!");
		return ret.toJSONString();
		
	}
	
	@GetMapping("/blog/comments")
	public String getBlogComments( String blogId, String page){
		JSONObject ret = new JSONObject();
		if(NumberUtil.isNumber(page) && StrUtil.isNotEmpty(blogId)){
			ret.put("errCode", YesOrNo.ZERO);
			ret.put("errMsg", YesOrNo.OK);
			
			int curPage = Integer.valueOf(page) > 1 ? Integer.valueOf(page) : 1;
			
			Page<Comment> comments = commentService.getCommentList(blogId, curPage, pageSize);
			
			ret.put("comments", comments.getContent());
			//System.out.println(JSON.toJSONString(ret,SerializerFeature.DisableCircularReferenceDetect));
			return JSON.toJSONString(ret,SerializerFeature.DisableCircularReferenceDetect);
			
		}
		
		ret.put("errCode", "40001");
		ret.put("errMsg", "WRONG PARAMETERS!");
		return ret.toJSONString();
		
	}
	
	@PostMapping("/au/blog/star")
	public String starBlog(String webId, String blogId){
		JSONObject ret = new JSONObject();
		if(StrUtil.isNotEmpty(blogId)){
			boolean isStar = false;
			ret.put("errCode", YesOrNo.ZERO);
			ret.put("errMsg", YesOrNo.OK);
			
			//Long blgId = Long.valueOf(blogId);
			Optional<Blog> optBlog = blogRepository.findById(blogId);
			Optional<UserBlogStar> optUStar = userBlogStarRepository.findByWebIdAndBlogId(webId,blogId);
			if(optBlog.isPresent()){
				Blog blog = optBlog.get();
				if(optUStar.isPresent()){
					blog.setStars(blog.getStars() - 1);
				}else{
					blog.setStars(blog.getStars() + 1);
				}
				
				blogRepository.save(blog);
				//blogRepository.starBlog(blog.getStars(), blgId);
			}
			
			
			if(optUStar.isPresent()){
				userBlogStarRepository.deleteByWebIdAndBlogId(webId, blogId);
			}else{
				isStar = true;
				UserBlogStar userStar = new UserBlogStar();
				userStar.setBlogId(blogId);
				userStar.setWebId(webId);
				userStar.setStarTime(new Date());
				userBlogStarRepository.save(userStar);
			}
			ret.put("isStar",isStar);
			return ret.toJSONString();
		}
		ret.put("errCode", "40001");
		ret.put("errMsg", "WRONG PARAMETERS!");
		return ret.toJSONString();
	}
	
	@PostMapping("/au/blog/comment")
	public String comment(String content ,String itemType ,String itemId,String webId){
		JSONObject ret = JSONUtils.defaultRet();
		if((CommentType.COMMENT.contentEquals(itemType)||CommentType.BLOG.contentEquals(itemType))
				&& StrUtil.isNotEmpty(content) 
				&& StrUtil.isNotEmpty(itemId)
				&& StrUtil.isNotEmpty(webId)){
			
			//Long id = Long.valueOf(item);
			//Long uid = Long.valueOf(webId);
			Optional<User> existUser = userRepository.findByWebId(webId);
			Object retObj = null;
			if( existUser.isPresent()){
				if(CommentType.COMMENT.contentEquals(itemType)){
					//回复评论
					Optional<Comment> optComment = commentRepository.findById(itemId);
					if(optComment.isPresent()){
						User user = existUser.get();
						CommentChild child = new CommentChild();
						child.setCommenter(user);
						child.setCommentTime(new Date());
						child.setText(content);
						child.setComment(optComment.get());
						CommentChild retChild = childRepository.save(child);
						retObj = retChild;
					}
//					commentRepository.findById(itemId).ifPresent(new Consumer<Comment>() {
//
//						@Override
//						public void accept(Comment c) {
//							User user = existUser.get();
//							CommentChild child = new CommentChild();
//							child.setCommenter(user);
//							child.setCommentTime(new Date());
//							child.setText(content);
//							child.setComment(c);
//							childRepository.save(child);
//						}
//					});
				}else{
					//评论文章
					Optional<Blog> optBlog = blogRepository.findById(itemId);
					if(optBlog.isPresent()){
						Comment comment = new Comment();
						comment.setBlogId(itemId);
						comment.setCommenter(existUser.get());
						comment.setText(content);
						comment.setCommentTime(new Date());
						Comment retComment = commentRepository.save(comment);
						retObj = retComment;
					}
//					blogRepository.findById(itemId).ifPresent(new Consumer<Blog>() {
//
//						@Override
//						public void accept(Blog t) {
//							Comment comment = new Comment();
//							comment.setBlogId(itemId);
//							comment.setCommenter(existUser.get());
//							comment.setText(content);
//							comment.setCommentTime(new Date());
//							commentRepository.save(comment);
//						}
//					});
				}
				if(CommentType.COMMENT.contentEquals(itemType)){
					ret.put("item", (CommentChild)retObj);
				}else{
					ret.put("item", (Comment)retObj);
				}
				return ret.toJSONString();
			}
			
		}
		ret = JSONUtils.wrongParamsRet();
		return ret.toJSONString();
	}
	
	@PostMapping("/au/blog/publish")
	public String publish(String webId,String content,String hasFile){
		JSONObject ret = JSONUtils.defaultRet();
		if(StrUtil.isNotEmpty(webId)){
			Optional<User> optUser = userRepository.findByWebId(webId);
			if(optUser.isPresent()){
				Blog blog = new Blog();
				blog.setContent(content);
				blog.setMediaType(BlogType.TEXT);
				blog.setComments(0);
				blog.setPublishTime(new Date());
				blog.setStars(0);
				blog.setViews(0);
				User publisher = optUser.get();
				publisher.setBlogs(publisher.getBlogs() + 1);
				userRepository.save(publisher);
				blog.setPublisher(publisher);
				Blog retBlog = blogRepository.save(blog);
				ret.put("blogId",retBlog.getId());
				
				return ret.toJSONString();
			}
		}
		
		return JSONUtils.wrongParamsRet().toJSONString();
	}
	
	@PostMapping("/au/blog/upload")
	public JSONObject uploadFile(@RequestParam("file") MultipartFile orginalFile,String webId,String blogId){
		JSONObject ret = JSONUtils.wrongParamsRet();
		if(StrUtil.isNotEmpty(webId) && StrUtil.isNotEmpty(blogId)){
			Optional<User> optUser = userRepository.findByWebId(webId);
			Optional<Blog> optBlog = blogRepository.findById(blogId);
			if(optUser.isPresent() && optBlog.isPresent()){
				Blog blog = optBlog.get();
				try {
					if (orginalFile != null) {
						String orginalFileName = orginalFile.getOriginalFilename();
						String originalFileSuffix = orginalFileName.substring(orginalFileName.lastIndexOf("."));
						String fileName = IdUtil.fastSimpleUUID() + originalFileSuffix;
						String filePath = "";
						String addr = "";
						Attachment attachment = new Attachment();
						
						if (FileUtils.isImage(originalFileSuffix)) {
							filePath = filePrefix + "/image/";
							addr = this.resImgAddr + "/" + fileName;
							attachment.setType(BlogType.IMAGE+"");
							blog.setMediaType(BlogType.IMAGE);
						}else if(FileUtils.isVideo(originalFileSuffix)){
							filePath = filePrefix + "/video/";
							addr = this.resViAddr + "/" + fileName;
							attachment.setType(BlogType.VIDEO+"");
							blog.setMediaType(BlogType.VIDEO);
						}else{
							return JSONUtils.wrongParamsRet("文件格式不正确");
						}
						
						orginalFile.transferTo(new File( filePath+ fileName));
						
						attachment.setUrl(addr);
						attachment.setBlog(blog);
						attachRepository.save(attachment);
						return JSONUtils.defaultRet();
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
	

}
