package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractSync {

    @Id
    @GeneratedValue
    public Long id;

    @Column(nullable = false)
    @NotBlank
    @EqualsAndHashCode.Include
    @NonNull
    public String relativePath;

    @Column(nullable = false)
    @NotBlank
    @NonNull
    public String name;

    @Column
    public Boolean existOnPC;

    @Column
    public Boolean existOnPhone;

    public AbstractSync(String relativePath, String name, Location location) {
        this.relativePath = relativePath;
        this.name = name;
        this.existOnPC = Location.PC.equals(location);
        this.existOnPhone = Location.PHONE.equals(location);
    }
}