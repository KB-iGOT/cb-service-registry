package com.igot.service_locator.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void testBuilderAndGetters() {
        ErrorResponse error = ErrorResponse.builder()
                .code("ERR001")
                .message("Something went wrong")
                .httpStatusCode(500)
                .build();

        assertThat(error.getCode()).isEqualTo("ERR001");
        assertThat(error.getMessage()).isEqualTo("Something went wrong");
        assertThat(error.getHttpStatusCode()).isEqualTo(500);
    }

    @Test
    void testEqualsHashCodeAndToStringForEqualObjects() {
        ErrorResponse e1 = ErrorResponse.builder()
                .code("ERR001")
                .message("Same")
                .httpStatusCode(400)
                .build();

        ErrorResponse e2 = ErrorResponse.builder()
                .code("ERR001")
                .message("Same")
                .httpStatusCode(400)
                .build();
        assertThat(e1)
                .isEqualTo(e2)
                .hasSameHashCodeAs(e2)
                .hasToString(e2.toString());
    }

    @Test
    void testEqualsWithDifferentValues() {
        ErrorResponse e1 = ErrorResponse.builder()
                .code("ERR001")
                .message("Same")
                .httpStatusCode(400)
                .build();
        ErrorResponse e2 = ErrorResponse.builder()
                .code("ERR002")
                .message("Different")
                .httpStatusCode(404)
                .build();
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        ErrorResponse e1 = ErrorResponse.builder()
                .code("ERR001")
                .message("Same")
                .httpStatusCode(400)
                .build();
        assertThat(e1)
                .isEqualTo(e1)
                .isNotEqualTo(null)
                .isNotEqualTo("string");
    }

    @Test
    void testEqualsWithNullVsNonNullFields() {
        ErrorResponse e1 = ErrorResponse.builder()
                .code(null)
                .message("msg")
                .httpStatusCode(200)
                .build();
        ErrorResponse e2 = ErrorResponse.builder()
                .code(null)
                .message("msg")
                .httpStatusCode(200)
                .build();
        assertThat(e1).isEqualTo(e2);
        ErrorResponse e3 = ErrorResponse.builder()
                .code("ERR001")
                .message("msg")
                .httpStatusCode(200)
                .build();
        assertThat(e1).isNotEqualTo(e3);
    }

    @Test
    void testHashCodeWithNullFields() {
        ErrorResponse e1 = ErrorResponse.builder()
                .code(null)
                .message(null)
                .httpStatusCode(0)
                .build();
        ErrorResponse e2 = ErrorResponse.builder()
                .code(null)
                .message(null)
                .httpStatusCode(0)
                .build();
        assertThat(e1)
                .isEqualTo(e2)
                .hasSameHashCodeAs(e2);
    }

}
