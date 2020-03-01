package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.ExtensionStat;
import com.kolosov.synchronizer.HistorySyncDTO;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.SyncService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(value = "/rest")
@Data
@CrossOrigin
public class SyncRestController {

    private final SyncService syncService;

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }
    @CrossOrigin
    @GetMapping("/syncs")
    public ResponseEntity<List<FolderSync>> getSyncs() {
        TreeSync treeSync = syncService.getTreeSync();
        return new ResponseEntity<>(treeSync.folderSyncs, HttpStatus.OK);
    }

    @GetMapping("/actions")
    public ResponseEntity<List<HistorySyncDTO>> getSyncsWithActions() {
        TreeSync treeSync = syncService.getTreeSync();
        List<HistorySyncDTO> historySyncsDTO = treeSync.historySyncs.stream()
                .map(HistorySyncDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(historySyncsDTO, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh() {
        syncService.refresh();
        return new ResponseEntity<>("refresh", HttpStatus.OK);
    }

    @GetMapping("/clear")
    public ResponseEntity<String> clear() {
        syncService.clear();
        return new ResponseEntity<>("clear", HttpStatus.OK);
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
    public ResponseEntity<Map<String, Integer>> ExtensionStat() {
        List<ExtensionStat> extensionStats = syncService.getExtensionStats();
        Map<String, Integer> map = extensionStats.stream()
                .collect(Collectors.toMap(extStat -> extStat.name, extStat -> extStat.count));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/ext/{name}")
    public ResponseEntity<ExtensionStat> ExtensionStat(@PathVariable String name) {
        List<ExtensionStat> extensionStats = syncService.getExtensionStats();
        ExtensionStat extensionStat = extensionStats.stream()
                .filter(stat -> stat.name.equals(name))
                .findFirst()
                .orElse(null);
        return new ResponseEntity<>(extensionStat, HttpStatus.OK);
    }

}
