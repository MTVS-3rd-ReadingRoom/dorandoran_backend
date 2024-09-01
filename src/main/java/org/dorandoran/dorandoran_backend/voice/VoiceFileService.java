package org.dorandoran.dorandoran_backend.voice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VoiceFileService {

    private final String uploadDir = "uploads/";
    private final List<VoiceFileDTO> fileStorage = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong();

    public VoiceFileDTO saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file provided");
        }

        // 파일 저장 경로 설정
        String fileName = file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.createDirectories(path.getParent());

        // 파일 저장
        Files.write(path, file.getBytes());

        // 파일 정보 저장
        VoiceFileDTO voiceFileDTO = new VoiceFileDTO(idCounter.incrementAndGet(), fileName, path.toString());
        fileStorage.add(voiceFileDTO);

        return voiceFileDTO;
    }

    public List<VoiceFileDTO> getAllFiles() {
        return fileStorage;
    }
}