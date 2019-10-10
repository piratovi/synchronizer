package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.repository.FileEntityRepo;
import com.kolosov.synchronizer.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("files")
@AllArgsConstructor
public class FileController {

    private static final String REDIRECT_EXT_FILES = "redirect:/";

    FileService fileService;

    FileEntityRepo fileEntityRepo;

    @GetMapping(value = "pc")
    public String getFilesPC(Model model) throws IOException {

        List<FileEntity> fileEntities = fileService.getFileEntities();
        model.addAttribute("files", fileEntities);
        return "files-pc";
    }

    @GetMapping(value = "pc/ext")
    public String getFilesPCExt(Model model) throws IOException {
        Set<String> extSet = fileService.getExt();
        model.addAttribute("extensions", extSet);
        return "files-pc-ext";
    }

    @GetMapping(value = "pc/ext/{ext}")
    public String getFilesWithCurrentExt(@PathVariable String ext, Model model) {
        List<FileEntity> fileEntitiesByExt = fileService.getFileEntitiesByExt(ext);
        model.addAttribute("fileEntitiesByExt", fileEntitiesByExt);
        return "fileEntitiesByExt";
    }

    @GetMapping("pc/ext/delete/{id}")
    public String delete(@PathVariable Long id, Model model) throws IOException {
        fileService.deleteById(id);
        model.addAttribute("fileEntitiesByExt", fileService.getFileEntitiesByExt("ext"));
        return "redirect:/files/pc/ext";
    }

}
