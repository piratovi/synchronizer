package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.DirectOperationsService;
import com.kolosov.synchronizer.service.FileService;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final PcWorker pcWorker;
    private final DirectOperationsService directOperationsService;

    @GetMapping(value = "all/{urlLocation}")
    public ResponseEntity<List<AbstractSync>> getAllFileEntities(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<AbstractSync> fileEntities = fileService.getFileEntitiesByLocation(location);
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

    @GetMapping("/data")
    public ResponseEntity<List<FolderSync>> getData() {
//        List<FolderSync> mergedList = directOperationsService.getMergedList();
        List<FolderSync> mergedList = fileService.createFileEntities();
        return new ResponseEntity<>(mergedList, HttpStatus.OK);
    }

}
