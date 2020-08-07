package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class DirectOperationsServiceTest {

    @Autowired
    DirectOperationsService directOperationsService;

//    @Test
//    public void findFilesByLocationPhone() {
//        List<AbstractSync> fileEntities = directOperationsService.getFileEntitiesByLocation(Location.PHONE);
//        fileEntities.forEach(x -> System.out.println(x.relativePath));
//        System.out.println(fileEntities.size());
//    }
//
//    @Test
//    public void findFilesByLocationPC() {
//        List<AbstractSync> fileEntities = directOperationsService.getFileEntitiesByLocation(Location.PC);
//        fileEntities.forEach(x -> System.out.println(x.relativePath));
//        System.out.println(fileEntities.size());
//    }

//    @Test
//    public void copyFileFromPcToPhone() {
//        AbstractSync abstractSync = new AbstractSync();
//        abstractSync.relativePath = "test\\inner\\test";
//        abstractSync.location = Location.PC;
//        directOperationsService.copyFileFromPcToPhone(abstractSync);
//    }
//
//    @Test
//    public void copyFileFromPhoneToPc() {
//        AbstractSync abstractSync = new AbstractSync();
//        abstractSync.relativePath = "test\\test";
//        abstractSync.location = Location.PHONE;
//        directOperationsService.copyFileFromPhoneToPc(abstractSync);
//    }

        @Test
    public void merge() {
            directOperationsService.connectPhone();
            TreeSync mergedList = directOperationsService.getNewTreeSync();
            directOperationsService.disconnectPhone();
            mergedList.getNestedSyncs().forEach(System.out::println);
    }

}