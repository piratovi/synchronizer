package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.Sync;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties("com.kolosov.synchronizer.location")
@Data
public class LocationService {

    private String rootPc;

    private String rootPhone;

    private List<String> folders;

    public String relativizePcPath(File file) {
        Path path = Paths.get(rootPc);
        return path.relativize(file.toPath()).toString();
    }

    public String getAbsolutePathForPc(Sync sync) {
        return rootPc + File.separator + sync.relativePath;
    }

    public List<Pair<String, String>> getAbsolutePathsForPcFolders() {
        return combineRootAndFolders(rootPc);
    }

    public List<Pair<String, String>> getAbsolutePathsForPhoneFolders() {
        return combineRootAndFolders(rootPhone);
    }

    private List<Pair<String, String>> combineRootAndFolders(String root) {
        return folders.stream()
                .map(folderPath -> Pair.of(folderPath, root + folderPath))
                .collect(Collectors.toList());
    }
}
