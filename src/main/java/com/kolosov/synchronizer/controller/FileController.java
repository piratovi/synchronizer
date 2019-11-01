package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.repository.FileEntityRepository;
import com.kolosov.synchronizer.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("files")
@AllArgsConstructor
public class FileController {

    public static final String REDIRECT_ROOT = "redirect:/";

    FileService fileService;

    @GetMapping(value = "pc")
    public String getFilesPC(Model model) {

        List<FileEntity> fileEntities = fileService.getFileEntitiesOnPC();
        model.addAttribute("files", fileEntities);
        return "files-pc";
    }

    @GetMapping(value = "pc/ext")
    public String getFilesPCExt(Model model) {
        Set<String> extSet = fileService.getExtensions();
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
    public String delete(@PathVariable Long id) {
        fileService.deleteById(id);
        return "redirect:/files/pc/ext";
    }

    @GetMapping("pc/ext/{ext}/deleteAll")
    public String deleteAll(@PathVariable String ext) {
        fileService.deleteExtAll(ext);
        return "redirect:/files/pc/ext";
    }

    @GetMapping("pc/refresh")
    public String refresh() {
        fileService.refresh();
        return REDIRECT_ROOT;
    }

    @GetMapping(value = "pc/empty")
    public String getEmpty(Model model) {
        List<FileEntity> fileEntities = fileService.getEmptyFolders();
        model.addAttribute("files", fileEntities);
        return "empty-folders-pc";
    }

    @GetMapping(value = "pc/empty/delete")
    public String deleteEmpty() {
        fileService.deleteEmptyFolders();
        return REDIRECT_ROOT;
    }
}
