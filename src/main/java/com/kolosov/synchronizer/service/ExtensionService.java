package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtensionService {

    private final TreeService treeService;

    public List<ExtensionStat> getExtensionStats() {
        return treeService.getTreeSync().getNestedSyncs()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .peek(fileSync -> fileSync.ext = FilenameUtils.getExtension(fileSync.relativePath).toLowerCase())
                .collect(Collectors.groupingBy(sync -> sync.ext, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> new ExtensionStat(entry.getKey(), entry.getValue().size(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
