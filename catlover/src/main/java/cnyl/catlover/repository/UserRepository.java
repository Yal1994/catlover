package cnyl.catlover.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.user.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String>{
	
	public User findByOpenId(String openId);
	
	public Optional<User> findByWebId(String webId);
	
}
