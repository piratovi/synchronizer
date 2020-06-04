package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@RequiredArgsConstructor
@Slf4j
public class FileFromPcToPhoneStrategy implements TransferStrategy {

    private final Sync sync;
    private final PcWorker pcWorker;
    private final PhoneWorker phoneWorker;

    @Override
    public void transfer()
    {
        try (
                InputStream inputStream = pcWorker.getInputStreamFrom(sync.asFile());
                OutputStream outputStream = phoneWorker.getOutputStreamTo(sync.asFile())
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(sync.toString(), e);
        }
        phoneWorker.closeStream();
        sync.existOnPhone = true;
        log.info(sync.relativePath + " transferred to " + PHONE);
    }
}
