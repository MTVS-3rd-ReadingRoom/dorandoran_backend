package org.dorandoran.dorandoran_backend.book;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookDTO {
    private Long no;

    private String isbn;

    private String name;

    private String author;

    private String category;

}
