package com.kolosov.synchronizer.service.transporter.validator;

import com.kolosov.synchronizer.domain.Sync;

import static com.kolosov.synchronizer.service.transporter.validator.TransferType.FILE_FROM_PC_TO_PHONE;
import static com.kolosov.synchronizer.service.transporter.validator.TransferType.FILE_FROM_PHONE_TO_PC;
import static com.kolosov.synchronizer.service.transporter.validator.TransferType.FOLDER_FROM_PC_TO_PHONE;
import static com.kolosov.synchronizer.service.transporter.validator.TransferType.FOLDER_FROM_PHONE_TO_PC;

public class TransferValidator {

    public static TransferType validate(Sync sync) {
        if (sync.isSynchronized()) {
            throw new RuntimeException(String.format("%s уже синхронизирована", sync.relativePath));
        }
        if (sync.existOnPC) {
            if (sync.isFolder()) {
                return FOLDER_FROM_PC_TO_PHONE;
            } else {
                return FILE_FROM_PC_TO_PHONE;
            }
        } else {
            if (sync.isFolder()) {
                return FOLDER_FROM_PHONE_TO_PC;
            } else {
                return FILE_FROM_PHONE_TO_PC;
            }
        }
    }

}
