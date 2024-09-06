package org.dorandoran.dorandoran_backend.debateroom;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dorandoran.dorandoran_backend.book.Book;

import java.util.Set;

@Entity
@Table
@Getter
@NoArgsConstructor
public class Debateroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String photon_debater_room_no;

    @Column(nullable = false)
    private String topic;

    @Column
    private String summary;

    @OneToMany(mappedBy = "debateroom")
    private Set<DebateroomUser> debateroomUsers;  // 컬렉션을 사용하여 여러 연관된 엔티티를 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no")  // 외래 키 컬럼 이름 설정
    private Book book;

    public Debateroom(String photon_debater_room_no, String topic, String summary, Set<DebateroomUser> debateroomUsers, Book book) {
        this.photon_debater_room_no = photon_debater_room_no;
        this.topic = topic;
        this.summary = summary;
        this.debateroomUsers = debateroomUsers;
        this.book = book;
    }
}
