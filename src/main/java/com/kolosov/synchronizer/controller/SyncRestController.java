package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.ExtensionStat;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.SyncService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/rest")
@Data
public class SyncRestController {

    private final SyncService syncService;

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }

    @GetMapping("/syncs")
    public ResponseEntity<List<FolderSync>> getSyncs() {
        TreeSync treeSync = syncService.getTreeSync();
        return new ResponseEntity<>(treeSync.folderSyncs, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh() {
        syncService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/emptyFolders")
    public ResponseEntity<List<FolderSync>> emptyFolders() {
        List<FolderSync> emptyFolders = syncService.getEmptyFolders();
        return new ResponseEntity<>(emptyFolders, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteSyncs(@RequestBody List<Long> ids) {
        ids.forEach(syncService::deleteById);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody List<Long> ids) {
        ids.forEach(syncService::transferSync);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/ext")
    public ResponseEntity<List<ExtensionStat>> ExtensionStat() {
        List<ExtensionStat> extensionStats = syncService.getExtensionStats();
        return new ResponseEntity<>(extensionStats, HttpStatus.OK);
    }
}
