package com.kolosov.synchronizer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.nio.file.Path;

import static com.kolosov.synchronizer.service.FileService.PATH_TO_MUSIC;

@Data
@Entity
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotBlank
    public String absolutePath;

    @Column
    @NotBlank
    public String relativePath;
//
//    @Column
//    public String ext;

    public FileEntity(String absolutePath) {
        this.absolutePath = absolutePath;
        this.relativePath = PATH_TO_MUSIC.relativize(Path.of(absolutePath)).toString();
    }

    public FileEntity(File file) {
        this(file.getAbsolutePath());
    }
}
