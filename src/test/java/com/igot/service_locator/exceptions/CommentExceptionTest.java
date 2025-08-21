package com.igot.service_locator.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentExceptionTest {

    @Test
    void testNoArgsConstructor() {
        CustomException ex = new CustomException();
        assertNull(ex.getCode());
        assertNull(ex.getMessage());
        assertNull(ex.getHttpStatusCode());
    }

    @Test
    void testTwoArgConstructor() {
        CustomException ex = new CustomException("ERR001", "Something went wrong", HttpStatus.BAD_REQUEST);
        assertEquals("ERR001", ex.getCode());
        assertEquals("Something went wrong", ex.getMessage());
    }

    @Test
    void testThreeArgConstructor() {
        CustomException ex = new CustomException("ERR002", "Bad Request", HttpStatus.BAD_REQUEST);
        assertEquals("ERR002", ex.getCode());
        assertEquals("Bad Request", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    }


    @Test
    void testSettersAndGetters() {
        CustomException ex = new CustomException();

        ex.setCode("ERR003");
        ex.setMessage("Another error");
        ex.setHttpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        Map<String, String> errors = new HashMap<>();
        errors.put("key", "value");

        assertEquals("ERR003", ex.getCode());
        assertEquals("Another error", ex.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatusCode());
    }
}
