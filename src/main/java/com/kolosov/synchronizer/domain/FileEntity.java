package com.kolosov.synchronizer.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileEntity {

    @Id
    @GeneratedValue
    public Long id;

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

    public FileEntity(String relativePath, Boolean isFile, String ext, Location location) {
        this.relativePath = relativePath;
        this.isFile = isFile;
        this.ext = ext;
        this.location = location;
    }
}