package cnyl.catlover.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.blog.Comment;
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
	
	Page<Comment> findAllByBlogIdIn(String blogId,Pageable pageable);

	void deleteByBlogId(String blogId);

}
