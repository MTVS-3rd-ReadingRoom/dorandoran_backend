package org.dorandoran.dorandoran_backend.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dorandoran.dorandoran_backend.debateroom.DebateRoom;

import java.util.Set;

@Entity
@Table
@NoArgsConstructor
@Getter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String category;

    @OneToMany(mappedBy = "book")
    private Set<DebateRoom> debateRooms;

    public Book(String isbn, String name, String author, String category) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.category = category;
    }
}
