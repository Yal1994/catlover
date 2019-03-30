package cnyl.catlover.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.relation.UserUserStar;

@Repository
@Transactional
public interface UserUserStarRepository extends JpaRepository<UserUserStar, String>{

	void deleteByStarterIdAndTargetId(String starterId, String targetId);

	Optional<UserUserStar> findByStarterIdAndTargetId(String starterId, String targetId);

	Page<UserUserStar> findByStarterId(String starterId, Pageable of);

	Page<UserUserStar> findByTargetId(String targetId, Pageable of);

	List<String> findTargetIdByStarterId(String starterId);

	List<String> findStarterIdByTargetId(String targetId);


}
