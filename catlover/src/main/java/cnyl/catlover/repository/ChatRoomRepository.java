package cnyl.catlover.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.chat.ChatRoom;
import cnyl.catlover.domain.user.User;
@Repository
@Transactional
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String>{

	List<ChatRoom> findAllByOwnerIdAndHasNew(String webId, boolean hasNew, Pageable of);

	List<ChatRoom> findAllByOwnerIdAndHasNew(String webId, boolean hasNew);

	List<ChatRoom> findAllByOwnerAndHasNew(User owner, boolean b);
	/**
	 * 
	 * @param senderId :=> ownerId 消息发送者ID
	 * @param reciverId:=> reciverId 消息接收者ID
	 * @return
	 */
	@Query("select t from ChatRoom t where t.owner.webId =?1 and t.reciver.webId=?2")
	ChatRoom findByOwnerIdAndReciverId(String senderId, String reciverId);

	List<ChatRoom> findAllByOwner(User user);

	List<ChatRoom> findAllByOwnerOrderByHasNewDesc(User user);

}
