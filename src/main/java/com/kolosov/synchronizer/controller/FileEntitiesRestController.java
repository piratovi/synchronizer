package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import com.kolosov.synchronizer.service.FileService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping(value = "/rest")
@Data
public class FileEntitiesRestController {

    private final FileService fileService;

    @GetMapping(value = "all/{urlLocation}")
    public ResponseEntity<List<FileEntity>> getAllFileEntities(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntities = fileService.getFileEntitiesByLocation(location);
        return new ResponseEntity<>(fileEntities, HttpStatus.OK);
    }

    @GetMapping("ext/{urlLocation}")
    public ResponseEntity<Set<String>> getFileExtensions(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        Set<String> extSet = fileService.getExtensions(location);
        return new ResponseEntity<>(extSet, HttpStatus.OK);
    }

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }

}
