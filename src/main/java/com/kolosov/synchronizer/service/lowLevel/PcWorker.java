package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
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
    public void deleteFile(FileEntity fileEntity) {
        try {
            Files.delete(Path.of(Location.PC.rootPath + fileEntity.relativePath));
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting " + fileEntity.relativePath);
        }
    }

    @Override
    public InputStream getInputStreamFromFile(FileEntity fileEntity) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(Utils.getAbsolutePath(fileEntity));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return fileInputStream;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public OutputStream getOutputStreamToFile(FileEntity fileEntity) {
        FileOutputStream outputStream;
        try {
            String absolutePath = Utils.getAbsolutePath(fileEntity);
            File file = new File(absolutePath);
            file.getParentFile().mkdirs();
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }

}
