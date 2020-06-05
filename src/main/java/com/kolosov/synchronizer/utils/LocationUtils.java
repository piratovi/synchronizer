package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.enums.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class LocationUtils {

    private final Environment env;
    @Getter
    private static String pcRootPath;
    @Getter
    private static String phoneRootPath;
    @Getter
    public static Path PATH;

    @PostConstruct
    public void setUp() {
        pcRootPath = env.getProperty("com.kolosov.synchronizer.location.path.pc");
        phoneRootPath = env.getProperty("com.kolosov.synchronizer.location.path.phone");
        PATH = Paths.get(pcRootPath);
    }

    public static String getRootPath(Location location) {
        return Location.PC.equals(location) ? pcRootPath : phoneRootPath;
    }

}
