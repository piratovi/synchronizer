package com.kolosov.synchronizer.service.transporter.validator;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferValidatorTest {

    @Test
    public void folderFromPcToPhone() {
        // setup
        Sync sync = new FolderSync("", Location.PC, null);

        // act
        TransferType result = TransferValidator.validate(sync);

        // verify
        assertEquals(TransferType.FOLDER_FROM_PC_TO_PHONE, result);
    }

    @Test
    public void fileFromPcToPhone() {
        // setup
        Sync sync = new FileSync("", Location.PC, null);

        // act
        TransferType result = TransferValidator.validate(sync);

        // verify
        assertEquals(TransferType.FILE_FROM_PC_TO_PHONE, result);
    }

    @Test
    public void folderFromPhoneToPc() {
        // setup
        Sync sync = new FolderSync("", Location.PHONE, null);

        // act
        TransferType result = TransferValidator.validate(sync);

        // verify
        assertEquals(TransferType.FOLDER_FROM_PHONE_TO_PC, result);
    }

    @Test
    public void fileFromPhoneToPc() {
        // setup
        Sync sync = new FileSync("", Location.PHONE, null);

        // act
        TransferType result = TransferValidator.validate(sync);

        // verify
        assertEquals(TransferType.FILE_FROM_PHONE_TO_PC, result);
    }

    @Test
    public void syncAlreadyTransferred() {
        // setup
        Sync sync = new FileSync("file", Location.PHONE, null);
        sync.existOnPc = true;

        // act
        // verify
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> TransferValidator.validate(sync));
        assertEquals("file уже синхронизирован", runtimeException.getMessage());
    }

}