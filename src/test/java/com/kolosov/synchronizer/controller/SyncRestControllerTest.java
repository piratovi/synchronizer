package com.kolosov.synchronizer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SyncRestControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void test() {
        String forObject = testRestTemplate.getForObject("/rest/test", String.class);
        assertEquals("test", forObject);
    }

    @Test
    void syncs() {
        String syncs = testRestTemplate.getForObject("/rest/syncs", String.class);
        assertEquals("[" +
                "{\"type\":\"folder\",\"id\":2,\"relativePath\":\"\\\\\\\\Music Folder1\",\"name\":\"Music Folder1\",\"existOnPc\":true,\"existOnPhone\":true,\"historySync\":null,\"list\":" +
                "[{\"type\":\"file\",\"id\":5,\"relativePath\":\"\\\\\\\\Music Folder1\\\\Composition 2\",\"name\":\"Composition 2\",\"existOnPc\":true,\"existOnPhone\":false,\"historySync\":null,\"ext\":null}]}]", syncs);
    }

}