package org.dorandoran.dorandoran_backend.utils;

import org.dorandoran.dorandoran_backend.book.Book;
import org.dorandoran.dorandoran_backend.book.BookRepository;
import org.dorandoran.dorandoran_backend.user.UserInfo;
import org.dorandoran.dorandoran_backend.user.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Dummy implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public Dummy(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    private void masterUser(){
        UserInfo master = new UserInfo("1", "1", BCrypt.hashpw("1", BCrypt.gensalt()), "1", LocalDateTime.now());
        userRepository.save(master);
    }

    private void book(){
        Book book = new Book("1", "1", "김영하", "문학");
        bookRepository.save(book);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("RUN Command");
        masterUser();
        book();
    }
}
