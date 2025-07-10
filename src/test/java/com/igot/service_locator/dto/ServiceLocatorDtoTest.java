package com.igot.service_locator.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceLocatorDtoTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        List<String> ids = List.of("1", "2");
        String url = "http://example.com";
        String serviceCode = "CODE123";
        String serviceName = "MyService";
        String operationType = "READ";
        Boolean active = true;

        ServiceLocatorDto dto = new ServiceLocatorDto(
                ids, url, serviceCode, serviceName, operationType, active
        );

        assertThat(dto.getIds()).isEqualTo(ids);
        assertThat(dto.getUrl()).isEqualTo(url);
        assertThat(dto.getServiceCode()).isEqualTo(serviceCode);
        assertThat(dto.getServiceName()).isEqualTo(serviceName);
        assertThat(dto.getOperationType()).isEqualTo(operationType);
        assertThat(dto.getActive()).isEqualTo(active);
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        ServiceLocatorDto dto = new ServiceLocatorDto();

        dto.setIds(List.of("a", "b"));
        dto.setUrl("http://test.com");
        dto.setServiceCode("SC");
        dto.setServiceName("TestService");
        dto.setOperationType("WRITE");
        dto.setActive(false);

        assertThat(dto.getIds()).containsExactly("a", "b");
        assertThat(dto.getUrl()).isEqualTo("http://test.com");
        assertThat(dto.getServiceCode()).isEqualTo("SC");
        assertThat(dto.getServiceName()).isEqualTo("TestService");
        assertThat(dto.getOperationType()).isEqualTo("WRITE");
        assertThat(dto.getActive()).isFalse();
    }

    @Test
    void testBuilder() {
        ServiceLocatorDto dto = ServiceLocatorDto.builder()
                .ids(List.of("x"))
                .url("http://builder.com")
                .serviceName("BuilderService")
                .operationType("UPDATE")
                .active(true)
                .build();

        assertThat(dto.getIds()).containsExactly("x");
        assertThat(dto.getUrl()).isEqualTo("http://builder.com");
        assertThat(dto.getServiceName()).isEqualTo("BuilderService");
        assertThat(dto.getOperationType()).isEqualTo("UPDATE");
        assertThat(dto.getActive()).isTrue();
    }
}
