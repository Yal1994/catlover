package cnyl.catlover.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.blog.Attachment;
@Repository
@Transactional
public interface AttachmentRepository extends JpaRepository<Attachment, String>{

	void deleteByBlogId(String blogId);

}
