package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.repository.FolderSyncRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FileServiceTest {

//    @Autowired
//    private FileService fileService;
//
//    @Autowired
//    private FolderSyncRepository folderSyncRepository;
//
//    @Test
//    public void PcSubtractionPhone() {
//        final List<String> relativeOnPhone = fileService.getSyncs(Location.PHONE).stream()
//                .map(fileEntity -> fileEntity.relativePath)
//                .collect(Collectors.toList());
//        List<AbstractSync> diff = new ArrayList<>(fileService.getSyncs(Location.PC));
//        diff = diff.stream()
//                .filter(fileEntity -> !relativeOnPhone.contains(fileEntity.relativePath))
//                .collect(Collectors.toList());
//        for (AbstractSync abstractSync : diff) {
//            System.out.println(abstractSync);
//        }
//    }
//
//    @Test
//    public void testEquals() {
//        final AbstractSync abstractSync1 = fileService.getSyncs(Location.PC).get(150);
//        abstractSync1.relativePath = "test1";
//        final AbstractSync abstractSync2 = fileService.getSyncs(Location.PC).get(151);
//        abstractSync2.relativePath = "test1";
//        System.out.println(abstractSync1);
//        System.out.println(abstractSync2);
//        assertEquals(abstractSync1, abstractSync2);
//    }
//
//    @Test
//    public void PcSubtractionPhone2() {
//        List<AbstractSync> diff = new ArrayList<>(fileService.getSyncs(Location.PC));
//        diff = diff.stream()
//                .filter(fileEntity -> !fileService.getSyncs(Location.PHONE).contains(fileEntity))
//                .collect(Collectors.toList());
//        for (AbstractSync abstractSync : diff) {
//            System.out.println(abstractSync);
//        }
//        System.out.println(diff.size());
//    }
//
//    @Test
//    public void timeRepo() {
////        long start = System.currentTimeMillis();
////        List<AbstractSync> all = folderSyncRepository.findAll();
////        System.out.println(System.currentTimeMillis() - start);
////        start = System.currentTimeMillis();
////        List<AbstractSync> fileEntitiesPC = folderSyncRepository.findAllByLocation(Location.PC);
////        System.out.println(System.currentTimeMillis() - start);
////        start = System.currentTimeMillis();
////        List<AbstractSync> fileEntitiesPhone = folderSyncRepository.findAllByLocation(Location.PHONE);
////        System.out.println(System.currentTimeMillis() - start);
//
//    }
//
//
}