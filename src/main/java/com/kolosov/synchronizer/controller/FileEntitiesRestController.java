package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.DirectOperationsService;
import com.kolosov.synchronizer.service.FileService;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/rest")
@Data
public class FileEntitiesRestController {

    private final FileService fileService;
    private final PcWorker pcWorker;
    private final DirectOperationsService directOperationsService;

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }

   /* @GetMapping(value = "all/{urlLocation}")
    public ResponseEntity<List<AbstractSync>> getAllFileEntities(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<AbstractSync> fileEntities = fileService.getSyncs(location);
        return new ResponseEntity<>(fileEntities, HttpStatus.OK);
    }

    @GetMapping("ext/{urlLocation}")
    public ResponseEntity<Set<String>> getFileExtensions(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        Set<String> extSet = fileService.getExtensions(location);
        return new ResponseEntity<>(extSet, HttpStatus.OK);
    }*/

    @GetMapping("/syncs")
    public ResponseEntity<List<FolderSync>> getSyncs() {
        TreeSync treeSync = fileService.getTreeSync();
        return new ResponseEntity<>(treeSync.folderSyncs, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh() {
        fileService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/emptyFolders")
    public ResponseEntity<List<FolderSync>> emptyFolders() {
        List<FolderSync> emptyFolders = fileService.getEmptyFolders();
        return new ResponseEntity<>(emptyFolders, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSync(@PathVariable long id) {
        fileService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
