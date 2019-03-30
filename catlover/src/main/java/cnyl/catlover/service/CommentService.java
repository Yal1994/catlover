package cnyl.catlover.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import cnyl.catlover.domain.blog.Comment;
import cnyl.catlover.repository.CommentRepository;

@Service
public class CommentService {
	@Autowired
	private CommentRepository repository;
	
	public Page<Comment> getCommentList(String blogId, int curPage, int pageSize){
		return repository.findAllByBlogIdIn(blogId, PageRequest.of(curPage - 1, pageSize, Direction.DESC, "commentTime"));
	}

}
