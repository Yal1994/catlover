package cnyl.catlover.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.user.UserToken;

@Repository
@Transactional
public interface UserTokenRepository extends JpaRepository<UserToken, String>{
	
	Optional<UserToken> findByUserWebId(String userWebId);
}
