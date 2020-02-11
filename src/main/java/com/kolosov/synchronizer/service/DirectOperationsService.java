package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectOperationsService {

    private final FtpWorker ftpWorker;
    private final PcWorker pcWorker;


    public List<AbstractSync> getFileEntitiesByLocation(Location location) {
        if (location.equals(Location.PC)) {
            return getFileEntities(pcWorker.getFileRelativePaths(), Location.PC);
        } else {
            return getFileEntities(ftpWorker.getFileRelativePaths(), Location.PHONE);
        }
    }

    private List<AbstractSync> getFileEntities(List<AbstractSync> fileRelativePaths, Location location) {
//        return fileRelativePaths.stream()
//                .map(s -> {
//                    String relativePath = s.getFirst();
//                    Boolean isFile = s.getSecond();
//                    String ext = null;
//                    if (isFile) {
//                        ext = FilenameUtils.getExtension(relativePath).toLowerCase();
//                    }
//                    return new AbstractSync(relativePath, ext, location);
//                })
//                .collect(Collectors.toList());
        return null;
    }

    public void deleteFile(AbstractSync abstractSync) {
//        LowLevelWorker worker = getWorkerByLocation(abstractSync.location);
//        worker.deleteFile(abstractSync);
        return;
    }

    private LowLevelWorker getWorkerByLocation(Location location) {
        if (Location.PC.equals(location)) {
            return pcWorker;
        }
        return ftpWorker;
    }

    public void copyFileFromPhoneToPc(AbstractSync abstractSync) {
        try (
                InputStream inputStream = ftpWorker.getInputStreamFromFile(abstractSync);
                OutputStream outputStream = pcWorker.getOutputStreamToFile(abstractSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpWorker.closeStream();

    }

    public void copyFileFromPcToPhone(AbstractSync abstractSync) {
        try (
                InputStream inputStream = pcWorker.getInputStreamFromFile(abstractSync);
                OutputStream outputStream = ftpWorker.getOutputStreamToFile(abstractSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpWorker.closeStream();
    }


}
