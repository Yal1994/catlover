package cnyl.catlover.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.blog.Blog;
import cnyl.catlover.domain.user.User;

@Repository
@Transactional
public interface BlogRepository extends JpaRepository<Blog, String>{
	
	@Modifying
	@Query(value="update Blog b set b.stars =?1 where b.id =?2")
	public void starBlog(int star,String blogId);

	public Page<Blog> findByPublisher(User publisher,Pageable pageable);

}
