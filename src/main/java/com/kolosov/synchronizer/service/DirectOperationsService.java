package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectOperationsService {

    private final FtpWorker ftpWorker;
    private final PcWorker pcWorker;


    public List<FileEntity> getFileEntitiesByLocation(Location location) {
        if (location.equals(Location.PC)) {
            return getFileEntities(pcWorker.getFileRelativePaths(), Location.PC);
        } else {
            return getFileEntities(ftpWorker.getFileRelativePaths(), Location.PHONE);
        }
    }

    private List<FileEntity> getFileEntities(List<Pair<String, Boolean>> fileRelativePaths, Location location) {
        return fileRelativePaths.stream()
                .map(s -> {
                    String relativePath = s.getFirst();
                    Boolean isFile = s.getSecond();
                    String ext = null;
                    if (isFile) {
                        ext = FilenameUtils.getExtension(relativePath).toLowerCase();
                    }
                    return new FileEntity(relativePath, isFile, ext, location);
                })
                .collect(Collectors.toList());
    }

    public void deleteFile(FileEntity fileEntity) {
        if (fileEntity.location.equals(Location.PC)) {
            pcWorker.deleteFile(fileEntity);
        } else {
            ftpWorker.deleteFile(fileEntity);
        }
    }


}
