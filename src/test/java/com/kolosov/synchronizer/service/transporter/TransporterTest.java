package com.kolosov.synchronizer.service.transporter;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import com.kolosov.synchronizer.service.transporter.validator.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransporterTest {

    @Mock
    DirectOperationsService directOperationsService;

    @Mock
    SyncRepository syncRepository;

    @InjectMocks
    Transporter transporter;

    @Test
    void transfer() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        treeSync.setSynchronized();
        FolderSync folderSync1 = new FolderSync("folder1", Location.PC, treeSync);
        folderSync1.setSynchronized();
        FolderSync folderSync2 = new FolderSync("folder2", Location.PC, treeSync);
        FolderSync subFolderSync = new FolderSync("subFolder", Location.PC, folderSync2);
        FileSync fileSync1 = new FileSync("file1", Location.PC, folderSync1);
        fileSync1.setSynchronized();
        FileSync fileSync2 = new FileSync("file2", Location.PC, subFolderSync);
        FileSync fileSync3 = new FileSync("file3", Location.PHONE, subFolderSync);

        when(syncRepository.findById(anyInt())).thenReturn(Optional.of(subFolderSync));
        doAnswer(invocation -> {
            Sync argument = invocation.getArgument(0, Sync.class);
            argument.setSynchronized();
            return null;
        }).when(directOperationsService).transfer(any(), any());

        // act
        transporter.transfer(List.of(1));

        // verify
        verify(directOperationsService).transfer(folderSync2, TransferType.FOLDER_FROM_PC_TO_PHONE);
        verify(directOperationsService).transfer(subFolderSync, TransferType.FOLDER_FROM_PC_TO_PHONE);
        verify(directOperationsService).transfer(fileSync2, TransferType.FILE_FROM_PC_TO_PHONE);
        verify(directOperationsService).transfer(fileSync3, TransferType.FILE_FROM_PHONE_TO_PC);
    }

}