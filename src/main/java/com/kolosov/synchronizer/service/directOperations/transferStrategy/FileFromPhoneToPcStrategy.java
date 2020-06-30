package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

import static com.kolosov.synchronizer.enums.Location.PC;

@RequiredArgsConstructor
@Slf4j
public class FileFromPhoneToPcStrategy implements TransferStrategy {

    private final Sync sync;
    private final PcWorker pcWorker;
    private final PhoneWorker phoneWorker;

    @Override
    public void transfer() {
        try (
                InputStream inputStream = phoneWorker.getInputStreamFrom(sync.asFile());
                OutputStream outputStream = pcWorker.getOutputStreamTo(sync.asFile())
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(sync.toString(), e);
        }
        phoneWorker.closeStream();
        sync.existOnPC = true;
        log.info(sync.relativePath + " transferred to " + PC);
    }
}
