package org.dorandoran.dorandoran_backend.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.name = ?1")
    Book findByBookName(String bookName);

    @Query("SELECT b FROM Book b WHERE b.isbn = ?1")
    Book findByBookIsbn(String isbn);
}
