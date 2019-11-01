package com.kolosov.synchronizer.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Path;

import static com.kolosov.synchronizer.service.DirectFileOperationsService.PATH_TO_MUSIC_PC;
import static com.kolosov.synchronizer.service.DirectFileOperationsService.PATH_TO_MUSIC_PHONE;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileEntity {

    @Id
    @GeneratedValue
    public Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @NonNull
    public String absolutePath;

    @Column(nullable = false )
    @NotBlank
    @EqualsAndHashCode.Include
    @NonNull
    public String relativePath;

    @Column
    @NotNull
    public Boolean isFile;

    @Column
    public String ext;

    @Column
    public Location location;

    public FileEntity(String absolutePath) {
        this.absolutePath = absolutePath;
        final File file = new File(absolutePath);
        this.isFile = file.isFile();
        if (this.isFile) {
            this.ext = FilenameUtils.getExtension(absolutePath).toLowerCase();
        }
        if (absolutePath.startsWith(PATH_TO_MUSIC_PC.toString())) {
            this.relativePath = PATH_TO_MUSIC_PC.relativize(Path.of(absolutePath)).toString();
            this.location = Location.PC;
        } else {
            this.relativePath = PATH_TO_MUSIC_PHONE.relativize(Path.of(absolutePath)).toString();
            this.location = Location.Phone;
        }
    }

    public enum Location {
        PC,
        Phone
    }
}
