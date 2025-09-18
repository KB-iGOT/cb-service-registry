package com.igot.service_locator.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        List<String> data = List.of("one", "two");

        PaginatedResponse<String> response = new PaginatedResponse<>(
                data,
                5,
                100L,
                20,
                0,
                10
        );

        assertThat(response.getResult()).isEqualTo(data);
        assertThat(response.getTotalPages()).isEqualTo(5);
        assertThat(response.getTotalElements()).isEqualTo(100L);
        assertThat(response.getNumberOfElements()).isEqualTo(20);
        assertThat(response.getOffset()).isZero();
        assertThat(response.getLimit()).isEqualTo(10);
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        PaginatedResponse<String> response = new PaginatedResponse<>();
        response.setResult(List.of("a", "b"));
        response.setTotalPages(3);
        response.setTotalElements(50L);
        response.setNumberOfElements(10);
        response.setOffset(5);
        response.setLimit(25);

        assertThat(response.getResult()).containsExactly("a", "b");
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(50L);
        assertThat(response.getNumberOfElements()).isEqualTo(10);
        assertThat(response.getOffset()).isEqualTo(5);
        assertThat(response.getLimit()).isEqualTo(25);
    }

    @Test
    void testEqualsHashCodeAndToString() {
        List<String> data = List.of("x", "y");
        PaginatedResponse<String> r1 = new PaginatedResponse<>(data, 2, 20L, 5, 0, 10);
        PaginatedResponse<String> r2 = new PaginatedResponse<>(data, 2, 20L, 5, 0, 10);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).hasSameHashCodeAs(r2.hashCode());
        assertThat(r1.toString()).contains("result", "totalPages", "totalElements");
    }


    @Test
    void testEqualsHashCodeAndToStringForEqualObjects() {
        List<String> data = List.of("x", "y");
        PaginatedResponse<String> r1 = new PaginatedResponse<>(data, 2, 20L, 5, 0, 10);
        PaginatedResponse<String> r2 = new PaginatedResponse<>(data, 2, 20L, 5, 0, 10);
        assertThat(r1)
                .isEqualTo(r2)
                .hasSameHashCodeAs(r2)
                .hasToString(r2.toString());
    }

    @Test
    void testEqualsSameReferenceNullAndDifferentClass() {
        PaginatedResponse<String> r1 = new PaginatedResponse<>(List.of("a"), 1, 1L, 1, 0, 1);
        assertThat(r1)
                .isEqualTo(r1)
                .isNotEqualTo(null)
                .isNotEqualTo("string");
    }

    @Test
    void testEqualsDifferentFieldValues() {
        List<String> data = List.of("a", "b");
        PaginatedResponse<String> base = new PaginatedResponse<>(data, 1, 2L, 3, 4, 5);
        assertThat(base)
                .isNotEqualTo(new PaginatedResponse<>(List.of("z"), 1, 2L, 3, 4, 5))
                .isNotEqualTo(new PaginatedResponse<>(data, 9, 2L, 3, 4, 5))
                .isNotEqualTo(new PaginatedResponse<>(data, 1, 99L, 3, 4, 5))
                .isNotEqualTo(new PaginatedResponse<>(data, 1, 2L, 42, 4, 5))
                .isNotEqualTo(new PaginatedResponse<>(data, 1, 2L, 3, 7, 5))
                .isNotEqualTo(new PaginatedResponse<>(data, 1, 2L, 3, 4, 99));
    }


    @Test
    void testEqualsWithNullListField() {
        PaginatedResponse<String> r1 = new PaginatedResponse<>(null, 1, 1L, 1, 0, 1);
        PaginatedResponse<String> r2 = new PaginatedResponse<>(null, 1, 1L, 1, 0, 1);
        assertThat(r1).isEqualTo(r2);
        r2 = new PaginatedResponse<>(List.of("x"), 1, 1L, 1, 0, 1);
        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void testHashCodeWithDifferentValues() {
        PaginatedResponse<String> r1 = new PaginatedResponse<>(List.of("a"), 1, 1L, 1, 0, 1);
        PaginatedResponse<String> r2 = new PaginatedResponse<>(List.of("b"), 2, 2L, 2, 1, 2);
        assertThat(r1.hashCode()).isNotEqualTo(r2.hashCode());
    }
}