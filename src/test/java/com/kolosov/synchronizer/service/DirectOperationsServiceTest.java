package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class DirectOperationsServiceTest {

    @Autowired
    DirectOperationsService directOperationsService;

    @Test
    public void findFilesByLocationPhone() {
        List<FileEntity> fileEntities = directOperationsService.getFileEntitiesByLocation(Location.PHONE);
        fileEntities.forEach(x -> System.out.println(x.relativePath));
        System.out.println(fileEntities.size());
    }

    @Test
    public void findFilesByLocationPC() {
        List<FileEntity> fileEntities = directOperationsService.getFileEntitiesByLocation(Location.PC);
        fileEntities.forEach(x -> System.out.println(x.relativePath));
        System.out.println(fileEntities.size());
    }

    @Test
    public void copyFileFromPcToPhone() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.relativePath = "test\\inner\\test";
        fileEntity.location = Location.PC;
        directOperationsService.copyFileFromPcToPhone(fileEntity);
    }

    @Test
    public void copyFileFromPhoneToPc() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.relativePath = "test\\test";
        fileEntity.location = Location.PHONE;
        directOperationsService.copyFileFromPhoneToPc(fileEntity);
    }

}