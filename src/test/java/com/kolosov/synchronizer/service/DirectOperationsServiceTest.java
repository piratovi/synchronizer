package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
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
    public void deleteFile() {
//        FileEntity fileEntity = new FileEntity();
//        fileEntity.relativePath = "Музыка в Авто от LORDEGRAF\\й.torrent";
//        fileEntity.location = Location.PHONE;
//        directOperationsService.deleteFile(fileEntity);
    }

    @Test
    public void copyFileFromPcToPhone() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.relativePath = "test\\test";
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