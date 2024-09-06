package org.dorandoran.dorandoran_backend.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Tag(name = "도서 API")
public class BookController {

    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository1) {
        this.bookRepository = bookRepository1;
    }

    @GetMapping("/api/book")
    @Operation(summary = "전체 책 조회")
    @ApiResponse(responseCode = "200", description = "전체 책 조회 성공",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(example = "[\n" +
                            "  {\n" +
                            "    \"no\": 1,\n" +
                            "    \"isbn\": \"string\",\n" +
                            "    \"name\": \"string\",\n" +
                            "    \"author\": \"string\",\n" +
                            "    \"category\": \"string\"\n" +
                            "  },\n" +
                            "  {\n" +
                            "    \"no\": 2,\n" +
                            "    \"isbn\": \"string\",\n" +
                            "    \"name\": \"string\",\n" +
                            "    \"author\": \"string\",\n" +
                            "    \"category\": \"string\"\n" +
                            "  }\n" +
                            "]"
                    ))))
    public List<BookDTO> getBook() {
        List<Book> books = bookRepository.findAll();
        List<BookDTO> bookDTO = books.stream()
                .map(book -> new BookDTO(book.getNo(), book.getIsbn(), book.getName(), book.getAuthor(), book.getCategory()))
                .collect(Collectors.toList());
        ;
        return bookDTO;
    }

    @PostMapping("/api/book")
    @Operation(summary = "책 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "책이 성공적으로 추가되었습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"bookId\": 1 }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 책",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    example = "이미 존재하는 책입니다"
                            )
                    )
            )
    })
    public ResponseEntity<?> createBook(
            @RequestParam("isbn") String isbn,
            @RequestParam("name") String name,
            @RequestParam("author") String author,
            @RequestParam("category") String category
    ) {
        Book book = new Book(isbn, name, author, category);
        if (bookRepository.findByBookIsbn(isbn) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("이미 존재하는 책입니다");
        }
        Long id = bookRepository.save(book).getNo();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("bookId", id);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(responseBody);
    }
}
