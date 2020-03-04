package com.kolosov.synchronizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SyncRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void refresh() throws Exception {

        mockMvc.perform(get("/rest/refresh"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/syncs"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void syncs() throws Exception {

        mockMvc.perform(get("/rest/syncs"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void transfer() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();
        byte[] bytes = jsonMapper.writeValueAsBytes(List.of(81245L));

        mockMvc.perform(post("/rest/transfer").contentType(MediaType.APPLICATION_JSON).content(bytes))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void deleteTest() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();
        byte[] bytes = jsonMapper.writeValueAsBytes(List.of(218146));

        mockMvc.perform(delete("/rest").contentType(MediaType.APPLICATION_JSON).content(bytes))
                .andDo(print())
                .andExpect(status().isOk());

    }


}