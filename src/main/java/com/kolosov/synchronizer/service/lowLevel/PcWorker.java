package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PcWorker implements LowLevelWorker {

    @Override
    public List<Pair<String, Boolean>> getFileRelativePaths() {
        Path path = Paths.get(Location.PC.rootPath);
        List<String> absolutePaths = findFiles(path);
        return absolutePaths.stream()
                .map(s -> {
                    File file = new File(s);
                    String relativePath = path.relativize(Path.of(s)).toString();
                    return Pair.of(relativePath, file.isFile());
                })
                .collect(Collectors.toList());
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
    public void deleteFile(FileEntity fileEntity) {
        Files.delete(Path.of(Location.PC.rootPath + fileEntity.relativePath));
    }

    @Override
    @SneakyThrows
    public InputStream getInputStreamFromFile(FileEntity fileEntity) {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(Utils.getAbsolutePath(fileEntity));
        return fileInputStream;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public OutputStream getOutputStreamToFile(FileEntity fileEntity) {
        FileOutputStream outputStream;
        String absolutePath = Utils.getAbsolutePath(fileEntity);
        File file = new File(absolutePath);
        file.getParentFile().mkdirs();
        outputStream = new FileOutputStream(file);
        return outputStream;
    }

}
