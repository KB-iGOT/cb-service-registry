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
        assertInstanceOf(ErrorResponse.class, response.getBody());

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
        assertInstanceOf(ErrorResponse.class, response.getBody());

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
        assertInstanceOf(ErrorResponse.class, response.getBody());

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("CUST_ERR_BLANK", error.getCode());
        assertEquals("", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getHttpStatusCode());
    }



    @Test
    void testHandleCustomExceptionWithNullMessage() {
        CustomException ex = mock(CustomException.class);
        when(ex.getHttpStatusCode()).thenReturn(HttpStatus.CONFLICT);
        when(ex.getCode()).thenReturn("NULL_MSG");
        when(ex.getMessage()).thenReturn(null); // null message
        ResponseEntity<?> response = restExceptionHandling.handleException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("NULL_MSG", error.getCode());
        assertNull(error.getMessage());
        assertEquals(HttpStatus.CONFLICT.value(), error.getHttpStatusCode());
    }


    @Test
    void testHandleCustomExceptionWithInvalidHttpStatus() {
        CustomException ex = mock(CustomException.class);
        when(ex.getHttpStatusCode())
                .thenThrow(new IllegalArgumentException("Invalid status"))
                .thenReturn(HttpStatus.BAD_REQUEST);
        when(ex.getCode()).thenReturn("BAD_STATUS");
        when(ex.getMessage()).thenReturn("Invalid http status");
        ResponseEntity<?> response = restExceptionHandling.handleException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("BAD_STATUS", error.getCode());
        assertEquals("Invalid http status", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getHttpStatusCode());
    }

    @Test
    void testHandleCustomExceptionWithNullHttpStatus() {
        CustomException ex = mock(CustomException.class);
        when(ex.getHttpStatusCode()).thenReturn(null);
        when(ex.getCode()).thenReturn("NULL_STATUS");
        when(ex.getMessage()).thenReturn("Message with null status");
        ResponseEntity<?> response = restExceptionHandling.handleException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("NULL_STATUS", error.getCode());
        assertEquals("Message with null status", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getHttpStatusCode());
    }

}
