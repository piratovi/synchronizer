package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.repository.FileEntityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Test
    public void PcSubtractionPhone() {
        final List<String> relativeOnPhone = fileService.fileEntitiesOnPhone.stream()
                .map(fileEntity -> fileEntity.relativePath)
                .collect(Collectors.toList());
        List<FileEntity> diff = new ArrayList<>(fileService.fileEntitiesOnPC);
        diff = diff.stream()
                .filter(fileEntity -> !relativeOnPhone.contains(fileEntity.relativePath))
                .collect(Collectors.toList());
        for (FileEntity fileEntity : diff) {
            System.out.println(fileEntity);
        }
    }

    @Test
    public void testEquals() {
        final FileEntity fileEntity1 = fileService.fileEntitiesOnPC.get(150);
        fileEntity1.relativePath = "test1";
        final FileEntity fileEntity2 = fileService.fileEntitiesOnPC.get(151);
        fileEntity2.relativePath = "test1";
        System.out.println(fileEntity1);
        System.out.println(fileEntity2);
        assertEquals(fileEntity1 ,fileEntity2);
    }

    @Test
    public void PcSubtractionPhone2() {
        List<FileEntity> diff = new ArrayList<>(fileService.fileEntitiesOnPC);
        diff = diff.stream()
                .filter(fileEntity -> !fileService.fileEntitiesOnPhone.contains(fileEntity))
                .collect(Collectors.toList());
        for (FileEntity fileEntity : diff) {
            System.out.println(fileEntity);
        }
        System.out.println(diff.size());
    }

    @Test
    public void timeRepo() {
        long start = System.currentTimeMillis();
        List<FileEntity> all = fileEntityRepository.findAll();
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        List<FileEntity> fileEntitiesPC = fileEntityRepository.findAllByLocation(FileEntity.Location.PC);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        List<FileEntity> fileEntitiesPhone = fileEntityRepository.findAllByLocation(FileEntity.Location.PHONE);
        System.out.println(System.currentTimeMillis() - start);

    }


}