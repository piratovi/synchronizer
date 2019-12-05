package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import com.kolosov.synchronizer.service.FileService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("files")
@RequiredArgsConstructor
public class FileController {

    public static final String REDIRECT_ROOT = "redirect:/";

    private final FileService fileService;

    @GetMapping(value = "all/{urlLocation}")
    public String getAllFileEntities(@PathVariable String urlLocation, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntities = fileService.getFileEntitiesByLocation(location);
        model.addAttribute("files", fileEntities);
        //TODO Move to templates
        model.addAttribute("location", urlLocation);
        return "files-pc";
    }

    @GetMapping("ext/{urlLocation}")
    public String getFileExtensions(@PathVariable String urlLocation, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        Set<String> extSet = fileService.getExtensions(location);
        model.addAttribute("extensions", extSet);
        return "files-pc-ext";
    }

    @GetMapping("ext/{urlLocation}/{ext}")
    public String getFilesByExtension(@PathVariable String urlLocation, @PathVariable String ext, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntitiesByExt = fileService.getFileEntitiesWithExt(location, ext);
        model.addAttribute("fileEntitiesByExt", fileEntitiesByExt);
        return "fileEntitiesByExt";
    }

    @GetMapping("ext/{urlLocation}/{ext}/deleteAll")
    public String deleteFileEntitiesByExtension(@PathVariable String urlLocation, @PathVariable String ext) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        fileService.deleteExtAll(location, ext);
        return "redirect:/files/ext/" + urlLocation;
    }

    @GetMapping("delete/{id}")
    public String deleteFileEntityById(@PathVariable Long id) {
        fileService.deleteById(id);
        return REDIRECT_ROOT;
    }

    @GetMapping("refresh/{urlLocation}")
    public String refresh(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        fileService.refresh(location);
        return REDIRECT_ROOT;
    }

    @GetMapping("empty/{urlLocation}")
    public String getEmptyFolders(@PathVariable String urlLocation, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntities = fileService.getEmptyFolders(location);
        model.addAttribute("files", fileEntities);
        return "empty-folders-pc";
    }

    @GetMapping("empty/{urlLocation}/delete")
    public String deleteEmptyFolders(@PathVariable String urlLocation) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        fileService.deleteEmptyFolders(location);
        return REDIRECT_ROOT;
    }

    @GetMapping("onlyOn/{urlLocation}")
    public String getOnlyOnLocationFileEntities(@PathVariable String urlLocation, Model model) {
        Location location = Location.valueOf(urlLocation.toUpperCase());
        List<FileEntity> fileEntities = fileService.onlyOnLocation(location);
        model.addAttribute("files", fileEntities);
        model.addAttribute("location", urlLocation);
        return "files-pc";
    }
}
