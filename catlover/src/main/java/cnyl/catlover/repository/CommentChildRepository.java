package cnyl.catlover.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.blog.CommentChild;

@Repository
@Transactional
public interface CommentChildRepository extends JpaRepository<CommentChild, String> {
	
	
	

}
