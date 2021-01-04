package com.kolosov.synchronizer.controller;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.SyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//TODO Дописать
class SyncRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SyncService syncService;

    @Test
    void test() throws Exception {
        mockMvc.perform(get("/rest/test"))
                .andExpect(content().string("test"))
                .andExpect(status().isOk());
    }

    @Test
    void syncs() throws Exception {
        TreeSync resultTree = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, resultTree);
        FileSync fileSyncPc = new FileSync("file", Location.PC, folderSyncPc);

//        when(syncService.getNotSynchronizedSyncs()).thenReturn(List.of(folderSyncPc));

        mockMvc.perform(get("/rest/all-syncs"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
    }

}