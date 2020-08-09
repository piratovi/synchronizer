package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import com.kolosov.synchronizer.utils.CalcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractTransferFileStrategy implements TransferStrategy {

    protected final PcWorker pcWorker;
    protected final PhoneWorker phoneWorker;
    protected final FileSync fileSync;

    protected void transferFileSync(LowLevelWorker workerFrom, LowLevelWorker workerTo, Location location) {
        float speed;
        try (
                InputStream inputStream = workerFrom.getInputStreamFrom(fileSync);
                OutputStream outputStream = workerTo.getOutputStreamTo(fileSync)
        ) {
            StopWatch watch = new StopWatch();
            watch.start();
            long bytes = inputStream.transferTo(outputStream);
            watch.stop();
            float milliseconds = watch.getTime(TimeUnit.MILLISECONDS);
            speed = CalcUtils.calculateTransferSpeed(bytes, milliseconds);
        } catch (Exception e) {
            throw new RuntimeException(fileSync.toString(), e);
        }
        phoneWorker.closeStream();
        String formattedOutput = String.format("Transferred to %5s : %s. Speed = %.2f Mbytes/sec", location, fileSync.relativePath, speed);
        log.info(formattedOutput);
    }

}
