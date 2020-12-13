package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LocationServiceTest {

    @Autowired
    LocationService locationService;

    @Test
    void getAbsolutePathForPc() {
        // setup
        FolderSync folderSync = new FolderSync("Music", Location.PC, null);

        // act
        String result = locationService.getAbsolutePathForPc(folderSync);

        // verify
        assertEquals("D:\\Music", result);
    }

    @Test
    void getFolderNamesAndAbsolutePathsForPcRootFolders() {
        // setup
        // act
        List<Pair<String, String>> result = locationService.getFolderNamesAndAbsolutePathsForPcRootFolders();

        // verify
        assertEquals(3, result.size());
        assertEquals("Music", result.get(0).getKey());
        assertEquals("D:\\Music", result.get(0).getValue());
        assertEquals("AudioBooks", result.get(1).getKey());
        assertEquals("D:\\AudioBooks", result.get(1).getValue());
        assertEquals("Other", result.get(2).getKey());
        assertEquals("D:\\Other", result.get(2).getValue());
    }

    @Test
    void getFolderNamesAndAbsolutePathsForPhoneRootFolders() {
        // setup
        // act
        List<Pair<String, String>> result = locationService.getFolderNamesAndAbsolutePathsForPhoneRootFolders();

        // verify
        assertEquals(3, result.size());
        assertEquals("Music", result.get(0).getKey());
        assertEquals("/Music", result.get(0).getValue());
        assertEquals("AudioBooks", result.get(1).getKey());
        assertEquals("/AudioBooks", result.get(1).getValue());
        assertEquals("Other", result.get(2).getKey());
        assertEquals("/Other", result.get(2).getValue());
    }
}