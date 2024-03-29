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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true)
@JsonSubTypes({
        @Type(value = FolderSync.class, name = "folder"),
        @Type(value = FileSync.class, name = "file")})
public abstract class Sync implements Component {

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
    public Boolean existOnPc;

    @Column(nullable = false)
    public Boolean existOnPhone;

    @ManyToOne()
    @JsonIgnore
    public FolderSync parent;

    @OneToOne(cascade = {CascadeType.ALL}, mappedBy = "sync")
    private HistorySync historySync;

    protected Sync(String name, Location location, FolderSync parent) {
        this.name = name;
        this.existOnPc = Location.PC.equals(location);
        this.existOnPhone = Location.PHONE.equals(location);
        if (parent != null) {
            parent.add(this);
            this.relativePath = parent.relativePath.concat("\\").concat(name);
        } else {
            this.relativePath = name;
        }
    }

    @JsonIgnore
    public boolean isNotSynchronized() {
        return this.existOnPhone != this.existOnPc;
    }

    @JsonIgnore
    public boolean isSynchronized() {
        return this.existOnPhone && this.existOnPc;
    }

    @Override
    public void removeFromParent() {
        parent.remove(this);
        this.parent = null;
    }

    @JsonIgnore
    public boolean isFile() {
        return this instanceof FileSync;
    }

    @JsonIgnore
    public boolean isFolder() {
        return this instanceof FolderSync;
    }

    @JsonIgnore
    public boolean isTree() {
        return this instanceof TreeSync;
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

    public void setSynchronized() {
        this.existOnPc = true;
        this.existOnPhone = true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ". relativePath = " + relativePath;
    }
}