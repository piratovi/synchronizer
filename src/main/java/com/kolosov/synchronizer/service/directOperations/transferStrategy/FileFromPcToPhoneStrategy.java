package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@RequiredArgsConstructor
@Slf4j
public class FileFromPcToPhoneStrategy implements TransferStrategy {

    private final Sync sync;
    private final PcWorker pcWorker;
    private final PhoneWorker phoneWorker;

    @Override
    public void transfer() {
        float speed;
        try (
                InputStream inputStream = pcWorker.getInputStreamFrom(sync.asFile());
                OutputStream outputStream = phoneWorker.getOutputStreamTo(sync.asFile())
        ) {
            StopWatch watch = new StopWatch();
            watch.start();
            float bytes = inputStream.transferTo(outputStream);
            watch.stop();
            float milliseconds = watch.getTime(TimeUnit.MILLISECONDS);
            speed = calculateSpeed(bytes, milliseconds);
        } catch (Exception e) {
            throw new RuntimeException(sync.toString(), e);
        }
        phoneWorker.closeStream();
        sync.existOnPhone = true;
        String formattedOutput = String.format("%s transferred to %s. Speed = %.2f Mbytes/sec", sync.relativePath, PHONE, speed);
        log.info(formattedOutput);
    }

    private float calculateSpeed(float bytes, float milliseconds) {
        return (bytes / (1024 * 1024)) / (milliseconds / 1000);
    }
}
