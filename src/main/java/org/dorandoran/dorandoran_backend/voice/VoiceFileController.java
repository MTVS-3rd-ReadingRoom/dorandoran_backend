package org.dorandoran.dorandoran_backend.voice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/voice")
public class VoiceFileController {

    private final VoiceFileService voiceFileService;

    @Autowired
    public VoiceFileController(VoiceFileService voiceFileService) {
        this.voiceFileService = voiceFileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVoiceFile(@RequestParam("file") MultipartFile file) {
        System.out.println(file);
        try {
            VoiceFileDTO savedFile = voiceFileService.saveFile(file);
            return new ResponseEntity<>("File uploaded successfully: " + savedFile.getFileName(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<VoiceFileDTO>> getAllFiles() {
        List<VoiceFileDTO> files = voiceFileService.getAllFiles();
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}