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
}