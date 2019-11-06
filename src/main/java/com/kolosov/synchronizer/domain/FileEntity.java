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

    @Column(nullable = false)
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

    public FileEntity(String absolutePath, Location location) {
        this.absolutePath = absolutePath;
        final File file = new File(absolutePath);
        this.isFile = file.isFile();
        if (this.isFile) {
            this.ext = FilenameUtils.getExtension(absolutePath).toLowerCase();
        }
        this.relativePath = location.path.relativize(Path.of(absolutePath)).toString();
        this.location = location;
    }
}