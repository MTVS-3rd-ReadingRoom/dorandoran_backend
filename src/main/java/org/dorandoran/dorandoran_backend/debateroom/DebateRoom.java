package org.dorandoran.dorandoran_backend.debateroom;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dorandoran.dorandoran_backend.book.Book;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table
@Getter
@NoArgsConstructor
public class DebateRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String photon_debate_room_no;

    @Column
    private String topic;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDate createdAtDate;
    private LocalTime createdAtTime;


    @OneToMany(mappedBy = "debateroom", cascade = CascadeType.REMOVE)
    private Set<DebateRoomUser> debateRoomUsers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no")
    private Book book;

    public DebateRoom(String photon_debate_room_no, String topic, String summary, Set<DebateRoomUser> debateRoomUsers, Book book) {
        this.photon_debate_room_no = photon_debate_room_no;
        this.topic = topic;
        this.summary = summary;
        this.debateRoomUsers = debateRoomUsers;
        this.book = book;
    }
}
