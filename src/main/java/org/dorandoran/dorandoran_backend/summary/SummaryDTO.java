package org.dorandoran.dorandoran_backend.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDTO {

    private Long debateRoomNo;
    private String bookName;
    private String bookAuthor;
    private String category;
    private String topic;
    private String summary;
    private LocalDate createdAtDate;
    private LocalTime createdAtTime;
}
