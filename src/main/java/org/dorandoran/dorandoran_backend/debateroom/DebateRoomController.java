package org.dorandoran.dorandoran_backend.debateroom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dorandoran.dorandoran_backend.customexception.ErrorResponseHandler;
import org.dorandoran.dorandoran_backend.book.Book;
import org.dorandoran.dorandoran_backend.book.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "토론 API")
public class DebateRoomController {

    private final DebateRoomRepository debateRoomRepository;
    private final BookRepository bookRepository;

    public DebateRoomController(DebateRoomRepository debateRoomRepository, BookRepository bookRepository) {
        this.debateRoomRepository = debateRoomRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/api/debate-room")
    @Operation(summary = "토론방 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Room created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"roomId\": 1 }"
                    )
            )
    )
    public ResponseEntity<?> createDebateRoom(@Parameter(description = "책 이름", example = "사랑과 전쟁") @RequestParam("bookName") String bookName,
                                              @Parameter(description = "포톤에서 생성된 토론방 식별값", example = "1") @RequestParam("photonDebateRoomNo") String photonDebateRoomNo
                                              // @Parameter(description = "AI에서 생성된 주제", example = "사랑은 전쟁인가?") @RequestParam("topic") String topic,
                                              // @Parameter(description = "요약글", example = "사랑은 전쟁이라는 결론") @RequestParam("summary") String summary
    ) {
        Book book = bookRepository.findByBookName(bookName);
        if (book == null){
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "책을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
        DebateRoom debateroom = new DebateRoom(photonDebateRoomNo, null, null, null, book);
        Long id = debateRoomRepository.save(debateroom).getNo();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("roomId", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Operation(summary = "토론방 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "토론방 삭제 성공", content = @Content(mediaType = "text/plain;charset=UTF-8", schema = @Schema(example = "토론방이 삭제되었습니다."))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(mediaType = "application/json",schema = @Schema(example = "{\"error\": \"string\"}")))
    })
    @DeleteMapping("/api/debate-room/{id}")
    public ResponseEntity<?> deleteDebateRoom(@PathVariable("id") Long debateRoomId) {
        try{
            DebateRoom debateRoom = debateRoomRepository.findById(debateRoomId).orElseThrow(() -> new IllegalArgumentException("해당 토론방이 존재하지 않습니다."));
            debateRoomRepository.delete(debateRoom);
            return ResponseEntity.status(HttpStatus.OK).body("토론방이 삭제되었습니다.");
        }catch (Exception e){
            return ErrorResponseHandler.get(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    
}
