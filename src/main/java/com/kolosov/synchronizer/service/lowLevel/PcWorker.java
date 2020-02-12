package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.utils.LocationUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kolosov.synchronizer.utils.LocationUtils.PATH;

@Service
public class PcWorker implements LowLevelWorker {

    @Override
    public List<FolderSync> getFileRelativePaths() {
//        Path path = Paths.get(LocationUtils.getPcRootPath());
//        List<String> absolutePaths = findFiles(path);
//        return absolutePaths.stream()
//                .map(s -> {
//                    File file = new File(s);
//                    String relativePath = path.relativize(Path.of(s)).toString();
//                    return Pair.of(relativePath, file.isFile());
//                })
//                .collect(Collectors.toList());
        List<FolderSync> syncList = new ArrayList<>();
        File root = new File(LocationUtils.getPcRootPath());
        listDirectory(root, syncList, null);
        return syncList;
    }

    //TODO проверка на null
    public void listDirectory(File parentDir, List<FolderSync> result, FolderSync parentFolderSync) {
        for (final File file : parentDir.listFiles()) {
            String relativePath = PATH.relativize(file.toPath()).toString();
            String name = file.getName();
            if (file.isDirectory()) {
                FolderSync current;
                if (parentFolderSync == null) {
                    current = new FolderSync(relativePath, name, Location.PC, null);
                    result.add(current);
                } else {
                    current = new FolderSync(relativePath, name, Location.PC, parentFolderSync);
                    parentFolderSync.list.add(current);
                }
                listDirectory(file, result, current);
            }
            if (file.isFile()) {
                FileSync current = new FileSync(relativePath, name, Location.PC, parentFolderSync);
                parentFolderSync.list.add(current);
            }
        }
    }

    //TODO Sneaky?
    private List<String> findFiles(Path pathToMusic) {
        List<String> fileEntities;
        try (Stream<Path> stream = Files.walk(pathToMusic)) {
            fileEntities = stream
                    .skip(1)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from " + pathToMusic);
        }
        return fileEntities;
    }

    @Override
    @SneakyThrows
    public void deleteFile(AbstractSync abstractSync) {
        Files.delete(Path.of(LocationUtils.getPcRootPath() + abstractSync.relativePath));
    }

    @Override
    @SneakyThrows
    public InputStream getInputStreamFromFile(AbstractSync abstractSync) {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(Utils.getAbsolutePath(abstractSync));
        return fileInputStream;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public OutputStream getOutputStreamToFile(AbstractSync abstractSync) {
        FileOutputStream outputStream;
        String absolutePath = Utils.getAbsolutePath(abstractSync);
        File file = new File(absolutePath);
        file.getParentFile().mkdirs();
        outputStream = new FileOutputStream(file);
        return outputStream;
    }

}
