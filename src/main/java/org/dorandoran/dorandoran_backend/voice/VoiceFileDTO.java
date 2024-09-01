package org.dorandoran.dorandoran_backend.voice;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceFileDTO {
    private Long id;
    private String fileName;
    private String filePath;
}