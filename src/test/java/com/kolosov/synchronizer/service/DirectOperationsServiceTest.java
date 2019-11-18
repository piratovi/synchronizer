package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DirectOperationsServiceTest {

    @Autowired
    DirectOperationsService directOperationsService;

    @Test
    public void findFilesByLocation() {
        List<String> filesByLocation = directOperationsService.findFilesByLocation(Location.PHONE);
        filesByLocation.forEach(System.out::println);
        System.out.println(filesByLocation.size());
    }
}