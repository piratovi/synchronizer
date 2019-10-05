package com.kolosov.synchronizer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

import static com.kolosov.synchronizer.service.FileService.PATH_TO_MUSIC;

@AllArgsConstructor
@Data
public class FileEntity {

//    private Long id;
    private Path absolutePath;
    private Path relativePath;

    public FileEntity(Path absolutePath) {
        this.absolutePath = absolutePath;
        this.relativePath = PATH_TO_MUSIC.relativize(absolutePath);
    }
}
