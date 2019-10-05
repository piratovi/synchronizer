package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
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

    FileService fileService;

    @GetMapping(value = "pc")
    public String getFilesPC(Model model) throws IOException {
        Path pathToMusic = Path.of("D:", "Music", "MusicBase");
        List<String> fileList;
        try (Stream<Path> stream = Files.walk(pathToMusic)) {
            fileList = stream.map(Path::toFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        model.addAttribute("files", fileList);
        return "files-pc";
    }

    @GetMapping(value = "pc/ext")
    public String getFilesPCExt(Model model) throws IOException {
        Set<String> extSet;
        extSet = fileService.getExt();
        model.addAttribute("extensions", extSet);
        return "files-pc-ext";
    }

}
