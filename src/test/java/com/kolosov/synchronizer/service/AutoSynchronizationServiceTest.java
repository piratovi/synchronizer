package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.service.refresher.Refresher;
import com.kolosov.synchronizer.service.transporter.Transporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSynchronizationServiceTest {

    @Mock
    HistorySyncRepository historySyncRepository;

    @Mock
    Transporter transporter;

    @Mock
    Remover remover;

    @Mock
    Refresher refresher;

    @InjectMocks
    AutoSynchronizationService autoSynchronizationService;

    @Test
    void autoSynchronization() {
        // setup
        FileSync fileSync = new FileSync("file", Location.PC, null);
        HistorySync historySync1 = new HistorySync(fileSync, ProposedAction.TRANSFER);
        FolderSync folderSync = new FolderSync("folder", Location.PC, null);
        HistorySync historySync2 = new HistorySync(folderSync, ProposedAction.REMOVE);

        when(historySyncRepository.findAll()).thenReturn(List.of(historySync1, historySync2));

        // act
        autoSynchronizationService.autoSynchronization();

        // verify
        verify(transporter, times(1)).transfer(fileSync);
        verify(historySyncRepository).delete(historySync1);
        assertNull(fileSync.getHistorySync());

        verify(remover, times(1)).remove(folderSync);
        verify(historySyncRepository).delete(historySync2);
        assertNull(folderSync.getHistorySync());
    }

}