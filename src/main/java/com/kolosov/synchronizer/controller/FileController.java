package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.FileEntity.Location;
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

    @GetMapping(value = "/all/{urlLocation}")
    public String getAllFileEntities(@PathVariable String urlLocation, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntities = fileService.getFileEntitiesByLocation(location);
        model.addAttribute("files", fileEntities);
        //TODO Move to templates
        model.addAttribute("location", urlLocation);
        return "files-pc";
    }

    @GetMapping(value = "ext/{urlLocation}")
    public String getFilesPCExt(@PathVariable String urlLocation, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        Set<String> extSet = fileService.getExtensions(location);
        model.addAttribute("extensions", extSet);
        return "files-pc-ext";
    }

    @GetMapping(value = "ext/{urlLocation}/{ext}")
    public String getFilesWithCurrentExt(@PathVariable String urlLocation, @PathVariable String ext, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntitiesByExt = fileService.getFileEntitiesWithExt(location, ext);
        model.addAttribute("fileEntitiesByExt", fileEntitiesByExt);
        return "fileEntitiesByExt";
    }

    @GetMapping("pc/ext/delete/{id}")
    public String delete(@PathVariable Long id) {
        fileService.deleteById(id);
        return "redirect:/files/pc/ext";
    }

    @GetMapping("/ext/{urlLocation}/{ext}/deleteAll")
    public String deleteFileEntitiesWithExtension(@PathVariable String urlLocation, @PathVariable String ext) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        fileService.deleteExtAll(location, ext);
        return "redirect:/files/" + urlLocation + "/ext";
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
