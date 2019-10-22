package com.kolosov.synchronizer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Path;

import static com.kolosov.synchronizer.service.PcDirectFileOperationsService.PATH_TO_MUSIC;

@Data
@Entity
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String absolutePath;

    @Column
    @NotBlank
    private String relativePath;

    @Column
    @NotNull
    private Boolean isFile;

    @Column
    private String ext;

    public FileEntity(String absolutePath) {
        this.absolutePath = absolutePath;
        this.relativePath = PATH_TO_MUSIC.relativize(Path.of(absolutePath)).toString();
        final File file = new File(absolutePath);
        this.isFile = file.isFile();
        if (this.isFile) {
            this.ext = FilenameUtils.getExtension(absolutePath);
        }
    }
}
