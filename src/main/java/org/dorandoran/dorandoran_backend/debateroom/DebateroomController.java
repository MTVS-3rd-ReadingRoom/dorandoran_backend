package org.dorandoran.dorandoran_backend.debateroom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dorandoran.dorandoran_backend.book.Book;
import org.dorandoran.dorandoran_backend.book.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "토론 API")
public class DebateroomController {

    private final DebateroomRepository debateroomRepository;
    private final BookRepository bookRepository;

    public DebateroomController(DebateroomRepository debateroomRepository, BookRepository bookRepository) {
        this.debateroomRepository = debateroomRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/api/debate-room")
    @Operation(summary = "토론방 생성")
    public ResponseEntity<?> createDebateRoom(@RequestParam("book_name") String bookName,
                                @RequestParam("photon_debater_room_no") String photonDebaterRoomNo,
                                @RequestParam("topic") String topic,
                                @RequestParam("summary") String summary
    ) {
        Book book = bookRepository.findByBookName(bookName);
        if (book == null){
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "책을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
        Debateroom debateroom = new Debateroom(photonDebaterRoomNo, topic, summary, null, book);
        Long id = debateroomRepository.save(debateroom).getNo();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("roomId", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}
