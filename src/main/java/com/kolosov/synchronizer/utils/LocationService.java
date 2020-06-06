package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class LocationService {

    @Getter
    @Value("${com.kolosov.synchronizer.location.dir.pc}")
    private String pcRootPath;
    @Getter
    @Value("${com.kolosov.synchronizer.location.dir.phone}")
    private String phoneRootPath;

    public String relativizePcPath(File file) {
        Path path = Paths.get(pcRootPath);
        return path.relativize(file.toPath()).toString();
    }

    public String convertPathForFtp(String relativePath) {
        String replaced = relativePath.replaceAll("\\\\", "/");
        return new String(replaced.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    public String getAbsolutePathForPc(Sync sync) {
        return pcRootPath + File.separator + sync.relativePath;
    }
}
