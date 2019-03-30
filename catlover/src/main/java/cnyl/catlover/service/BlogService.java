package cnyl.catlover.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import cnyl.catlover.domain.blog.Blog;
import cnyl.catlover.domain.user.User;
import cnyl.catlover.repository.BlogRepository;

@Service
public class BlogService {
	@Autowired
	private BlogRepository blogRepository;
	
	public Page<Blog> getBlogs(int curPage, int pageSize){
		
		return blogRepository.findAll(PageRequest.of(curPage - 1, pageSize, new Sort(Direction.DESC,"publishTime")));
		
	}
	
	public Page<Blog> findByPublisher(User publisher,int curPage, int pageSize){
		
		return blogRepository.findByPublisher(publisher,PageRequest.of(curPage - 1, pageSize, new Sort(Direction.DESC,"publishTime")));
		
	}
	
	public Optional<Blog> findById(String id){
		return blogRepository.findById(id);
	}
	
	public void deleteById(String id){
		System.out.println("service blogId:"+id);
		blogRepository.deleteById(id);
	}

}
