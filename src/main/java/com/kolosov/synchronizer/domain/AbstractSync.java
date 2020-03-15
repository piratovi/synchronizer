package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kolosov.synchronizer.enums.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true)
@JsonSubTypes({
        @Type(value = FolderSync.class, name = "folder"),
        @Type(value = FileSync.class, name = "file")})
public abstract class AbstractSync {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank
    @EqualsAndHashCode.Include
    @NonNull
    public String relativePath;

    @Column(nullable = false)
    @NotBlank
    @NonNull
    public String name;

    @Column(nullable = false)
    public Boolean existOnPC;

    @Column(nullable = false)
    public Boolean existOnPhone;

    @ManyToOne()
    @JsonIgnore
    public FolderSync parent;

    public AbstractSync(String relativePath, String name, Location location, FolderSync parent) {
        this.relativePath = relativePath;
        this.name = name;
        this.existOnPC = Location.PC.equals(location);
        this.existOnPhone = Location.PHONE.equals(location);
        this.parent = parent;
    }

    @JsonIgnore
    public boolean isNotSynchronized() {
        return this.existOnPhone != this.existOnPC;
    }

    @JsonIgnore
    public boolean isSynchronized() {
        return this.existOnPhone && this.existOnPC;
    }

    public void removeFromParent() {
        this.parent.remove(this);
    }

    @JsonIgnore
    public boolean isFile() {
        return this instanceof FileSync;
    }

    @JsonIgnore
    public boolean isFolder() {
        return this instanceof FolderSync;
    }

    public FileSync asFile() {
        return (FileSync) this;
    }

    public FolderSync asFolder() {
        return (FolderSync) this;
    }

    @JsonIgnore
    public boolean hasParent() {
        return parent != null;
    }
}