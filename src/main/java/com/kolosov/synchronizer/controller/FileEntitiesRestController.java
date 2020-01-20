package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.service.FileService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/rest")
@Data
public class FileEntitiesRestController {

    private final FileService fileService;

//    @GetMapping(value = "/onlyPC")
//    public List<FileEntity> onlyPC() {
//        return fileService.onlyOnLocation(location);
//    }
//
//    @GetMapping(value = "/onlyPhone")
//    public List<FileEntity> onlyPhone() {
//        return fileService.onlyOnPhone();
//    }
//
    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }

}
