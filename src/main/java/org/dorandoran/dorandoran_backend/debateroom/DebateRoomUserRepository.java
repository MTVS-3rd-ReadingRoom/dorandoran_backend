package org.dorandoran.dorandoran_backend.debateroom;

import org.dorandoran.dorandoran_backend.summary.SummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DebateRoomUserRepository extends JpaRepository<DebateRoomUser, Long> {


    @Query("SELECT new org.dorandoran.dorandoran_backend.summary.SummaryDTO(" +
            " dr.no" +
            ", bk.name" +
            ", bk.author" +
            ", bk.category" +
            ", dr.topic" +
            ", dr.summary" +
            ", dr.createdAtDate" +
            ", dr.createdAtTime " +
            ") " +
            "FROM DebateRoomUser dru " +
            "JOIN dru.debateroom dr " +
            "JOIN dr.book bk " +
            "JOIN dru.user u " +
            "WHERE u.no = :userNo AND dr.summary IS NOT NULL " +
            "ORDER BY dr.no DESC")
    List<SummaryDTO> findByUserNoWithJoin(@Param("userNo") Long userNo);

}
