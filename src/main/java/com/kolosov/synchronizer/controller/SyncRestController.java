package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.dto.HistorySyncDTO;
import com.kolosov.synchronizer.service.SyncService;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(value = "/rest")
@RequiredArgsConstructor
@CrossOrigin
public class SyncRestController {

    private final SyncService syncService;

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }

    @GetMapping("/syncs")
    public ResponseEntity<List<Sync>> getSyncs() {
        List<Sync> syncs = syncService.getNotSynchronizedSyncs().list;
        return new ResponseEntity<>(syncs, HttpStatus.OK);
    }

    @GetMapping("/actions")
    public ResponseEntity<List<HistorySyncDTO>> getSyncsWithActions() {
        List<HistorySyncDTO> historySyncsDTO = syncService.getHistorySyncs().stream()
                .map(HistorySyncDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(historySyncsDTO, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh() {
        syncService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll() {
        syncService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/emptyFolders")
    public ResponseEntity<List<FolderSync>> emptyFolders() {
        List<FolderSync> emptyFolders = syncService.getEmptyFolders();
        return new ResponseEntity<>(emptyFolders, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteSyncs(@RequestBody List<Integer> ids) {
        syncService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody List<Integer> ids) {
        syncService.transfer(ids);
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

    @GetMapping("/disconnect-phone")
    public ResponseEntity<Void> disconnectPhone() {
        syncService.disconnect();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/auto-synchronization")
    public ResponseEntity<Void> autoSynchronization() {
        syncService.autoSynchronization();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/connect-phone")
    public ResponseEntity<Void> connectPhone() {
        syncService.connectPhone();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/duplicate-syncs")
    public ResponseEntity<List<List<Sync>>> findDuplicateSyncs() {
        List<List<Sync>> duplicateSyncs = syncService.findDuplicateSyncs();
        return new ResponseEntity<>(duplicateSyncs, HttpStatus.OK);
    }

    //TODO посмотреть проблему с маппингом урлов
    @DeleteMapping("/delete-duplicate-syncs")
    public ResponseEntity<Void> deleteDuplicateSyncs() {
        syncService.deleteDuplicateSyncs();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all-syncs")
    public ResponseEntity<TreeSync> getAllSyncs() {
        TreeSync treeSync = syncService.getTreeSync();
        return new ResponseEntity<>(treeSync, HttpStatus.OK);
    }
}
