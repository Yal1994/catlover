package cnyl.catlover.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.relation.UserBlogStar;

@Repository
@Transactional
public interface UserBlogStarRepository extends JpaRepository<UserBlogStar, String>{

	Optional<UserBlogStar> findByWebIdAndBlogId(String webId, String blogId);

	void deleteByWebIdAndBlogId(String webId, String blogId);

}
