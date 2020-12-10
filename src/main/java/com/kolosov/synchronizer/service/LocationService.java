package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.Sync;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties("com.kolosov.synchronizer.location")
@Data
public class LocationService {

    private String rootPc;

    private String rootPhone;

    private List<String> folders;

    public String getAbsolutePathForPc(Sync sync) {
        return rootPc + File.separator + sync.relativePath;
    }

    public List<Pair<String, String>> getFolderNamesAndAbsolutePathsForPcRootFolders() {
        return combineRootPathAndFolderNames(rootPc);
    }

    public List<Pair<String, String>> getFolderNamesAndAbsolutePathsForPhoneRootFolders() {
        return combineRootPathAndFolderNames(rootPhone);
    }

    private List<Pair<String, String>> combineRootPathAndFolderNames(String root) {
        return folders.stream()
                .map(folderPath -> Pair.of(folderPath, root + folderPath))
                .collect(Collectors.toList());
    }
}
