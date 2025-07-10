package com.igot.service_locator.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestExceptionHandlingTest {

    private RestExceptionHandling restExceptionHandling;

    @BeforeEach
    void setUp() {
        restExceptionHandling = new RestExceptionHandling();
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<?> response = restExceptionHandling.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("ERROR", error.getCode());
        assertEquals("Something went wrong", error.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getHttpStatusCode());
    }

    @Test
    void testHandleCustomExceptionWithStatus() {
        CustomException ex = mock(CustomException.class);
        when(ex.getHttpStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(ex.getCode()).thenReturn("CUST_ERR");
        when(ex.getMessage()).thenReturn("Custom error message");

        ResponseEntity<?> response = restExceptionHandling.handleException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("CUST_ERR", error.getCode());
        assertEquals("Custom error message", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getHttpStatusCode());
    }

    @Test
    void testHandleCustomExceptionWithBlankMessage() {
        CustomException ex = mock(CustomException.class);
        when(ex.getHttpStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(ex.getCode()).thenReturn("CUST_ERR_BLANK");
        when(ex.getMessage()).thenReturn(""); // blank message

        ResponseEntity<?> response = restExceptionHandling.handleException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("CUST_ERR_BLANK", error.getCode());
        assertEquals("", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getHttpStatusCode());
    }
}
