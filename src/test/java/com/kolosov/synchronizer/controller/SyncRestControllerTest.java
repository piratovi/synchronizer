package com.kolosov.synchronizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.SyncService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        when(syncService.getNotSynchronizedSyncs()).thenReturn(List.of(folderSyncPc));

        Object asyncResult = mockMvc.perform(get("/rest/syncs"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getAsyncResult();
    }

    @Test
    @Disabled
    void refresh() throws Exception {
        mockMvc.perform(get("/rest/refresh"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/syncs"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    void transfer() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        byte[] bytes = jsonMapper.writeValueAsBytes(List.of(81245L));

        mockMvc.perform(post("/rest/transfer").contentType(MediaType.APPLICATION_JSON).content(bytes))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    void deleteTest() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        byte[] bytes = jsonMapper.writeValueAsBytes(List.of(218146));

        mockMvc.perform(delete("/rest").contentType(MediaType.APPLICATION_JSON).content(bytes))
                .andDo(print())
                .andExpect(status().isOk());
    }


}