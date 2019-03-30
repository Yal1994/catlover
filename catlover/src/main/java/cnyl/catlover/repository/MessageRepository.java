package cnyl.catlover.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cnyl.catlover.domain.chat.ChatRoom;
import cnyl.catlover.domain.chat.Message;
@Repository
@Transactional
public interface MessageRepository extends JpaRepository<Message, String>{

	void deleteByReciverRoomId(String roomId);

	void deleteByReciverIdAndSenderId(String reciverId, String senderId);

}
