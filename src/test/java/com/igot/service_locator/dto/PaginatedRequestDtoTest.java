package com.igot.service_locator.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedRequestDtoTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        PaginatedRequestDto dto = new PaginatedRequestDto(10, 20, true);

        assertThat(dto.getOffset()).isEqualTo(10);
        assertThat(dto.getLimit()).isEqualTo(20);
        assertThat(dto.getIsActive()).isTrue();
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        PaginatedRequestDto dto = new PaginatedRequestDto();

        dto.setOffset(5);
        dto.setLimit(15);
        dto.setIsActive(false);

        assertThat(dto.getOffset()).isEqualTo(5);
        assertThat(dto.getLimit()).isEqualTo(15);
        assertThat(dto.getIsActive()).isFalse();
    }

    @Test
    void testEqualsHashCodeAndToString() {
        PaginatedRequestDto dto1 = new PaginatedRequestDto(1, 2, true);
        PaginatedRequestDto dto2 = new PaginatedRequestDto(1, 2, true);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).hasSameHashCodeAs(dto2.hashCode());
        assertThat(dto1.toString()).contains("offset=1", "limit=2", "isActive=true");
    }
}
