package org.dorandoran.dorandoran_backend.debateroom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DebateRoomRepository extends JpaRepository<DebateRoom, Long> {

    //NOTE: 테스트를 위해 debateRoomNo가 아닌 phton_debate_room_no로 수정
    @Modifying
    @Query("update DebateRoom d set d.summary = ?2 where d.photon_debate_room_no = ?1")
    void updateSummary(String chatRoomId, String summary);

    //NOTE: 테스트를 위해 debateRoomNo가 아닌 phton_debate_room_no로 수정
    @Modifying
    @Query("update DebateRoom d set d.topic = ?2 where d.photon_debate_room_no = ?1")
    void updateTopic(String debateRoomNo, String topic);


    @Modifying
    @Query("update DebateRoom d set d.summary = ?2 where d.photon_debate_room_no = ?1")
    void updateSummary(Long debateRoomNo, String summary);


    @Modifying
    @Query("update DebateRoom d set d.topic = ?2 where d.photon_debate_room_no = ?1")
    void updateTopic(Long debateRoomNo, String topic);
}
