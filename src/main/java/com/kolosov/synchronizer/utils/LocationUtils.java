package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class LocationUtils {

    private final Environment env;
    @Getter
    private static String pcRootPath;
    @Getter
    private static String phoneRootPath;

    @PostConstruct
    public void setUp() {
        pcRootPath = env.getProperty("location.path.pc");
        phoneRootPath = env.getProperty("location.path.phone");
    }

    public static String getRootPath(Location location) {
        return Location.PC.equals(location) ? pcRootPath : phoneRootPath;
    }

}
