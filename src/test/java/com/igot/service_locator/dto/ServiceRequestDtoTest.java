package com.igot.service_locator.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceRequestDtoTest {

    @Test
    void testSettersAndGetters() {
        ServiceRequestDto dto = new ServiceRequestDto();
        dto.setHeaderMap(Map.of("h", "1"));
        dto.setUrlMap(Map.of("u", "2"));
        dto.setRequestMap(Map.of("r", "3"));
        assertThat(dto.getHeaderMap()).containsEntry("h", "1");
        assertThat(dto.getUrlMap()).containsEntry("u", "2");
        assertThat(dto.getRequestMap()).containsEntry("r", "3");
    }

    @Test
    void testEqualsHashCodeAndToStringForEqualObjects() {
        ServiceRequestDto d1 = new ServiceRequestDto();
        d1.setHeaderMap(Map.of("a", "1"));
        d1.setUrlMap(Map.of("b", "2"));
        d1.setRequestMap(Map.of("c", "3"));
        ServiceRequestDto d2 = new ServiceRequestDto();
        d2.setHeaderMap(Map.of("a", "1"));
        d2.setUrlMap(Map.of("b", "2"));
        d2.setRequestMap(Map.of("c", "3"));
        assertThat(d1)
                .isEqualTo(d2)
                .hasSameHashCodeAs(d2)
                .hasToString(d2.toString());
    }

    @Test
    void testEqualsSameReferenceNullAndDifferentClass() {
        ServiceRequestDto dto = new ServiceRequestDto();
        assertThat(dto)
                .isEqualTo(dto)
                .isNotEqualTo(null)
                .isNotEqualTo("string");
    }

    @Test
    void testEqualsDifferentFieldValues() {
        ServiceRequestDto base = new ServiceRequestDto();
        base.setHeaderMap(Map.of("h", "1"));
        base.setUrlMap(Map.of("u", "2"));
        base.setRequestMap(Map.of("r", "3"));
        ServiceRequestDto diff1 = new ServiceRequestDto();
        diff1.setHeaderMap(Map.of("x", "y"));
        diff1.setUrlMap(base.getUrlMap());
        diff1.setRequestMap(base.getRequestMap());
        ServiceRequestDto diff2 = new ServiceRequestDto();
        diff2.setHeaderMap(base.getHeaderMap());
        diff2.setUrlMap(Map.of("other", "val"));
        diff2.setRequestMap(base.getRequestMap());
        ServiceRequestDto diff3 = new ServiceRequestDto();
        diff3.setHeaderMap(base.getHeaderMap());
        diff3.setUrlMap(base.getUrlMap());
        diff3.setRequestMap(Map.of("new", "map"));
        assertThat(base)
                .isNotEqualTo(diff1)
                .isNotEqualTo(diff2)
                .isNotEqualTo(diff3);
    }

    @Test
    void testEqualsWithNullVsNonNullFields() {
        ServiceRequestDto d1 = new ServiceRequestDto();
        d1.setHeaderMap(null);
        d1.setUrlMap(Map.of("a", "1"));
        d1.setRequestMap(Map.of("b", "2"));
        ServiceRequestDto d2 = new ServiceRequestDto();
        d2.setHeaderMap(null);
        d2.setUrlMap(Map.of("a", "1"));
        d2.setRequestMap(Map.of("b", "2"));
        assertThat(d1).isEqualTo(d2);
        d2.setHeaderMap(Map.of("x", "y"));
        assertThat(d1).isNotEqualTo(d2);
    }

    @Test
    void testHashCodeDifferentValues() {
        ServiceRequestDto d1 = new ServiceRequestDto();
        d1.setHeaderMap(Map.of("a", "1"));
        d1.setUrlMap(Map.of("b", "2"));
        d1.setRequestMap(Map.of("c", "3"));
        ServiceRequestDto d2 = new ServiceRequestDto();
        d2.setHeaderMap(Map.of("x", "y"));
        d2.setUrlMap(Map.of("z", "9"));
        d2.setRequestMap(Map.of("m", "n"));
        assertThat(d1.hashCode()).isNotEqualTo(d2.hashCode());
    }

    @Test
    void testEqualsWithUrlMapNullVsNonNull() {
        ServiceRequestDto d1 = new ServiceRequestDto();
        d1.setHeaderMap(Map.of("h", "1"));
        d1.setUrlMap(null);
        d1.setRequestMap(Map.of("r", "3"));
        ServiceRequestDto d2 = new ServiceRequestDto();
        d2.setHeaderMap(Map.of("h", "1"));
        d2.setUrlMap(null);
        d2.setRequestMap(Map.of("r", "3"));
        assertThat(d1).isEqualTo(d2);
        d2.setUrlMap(Map.of("u", "val"));
        assertThat(d1).isNotEqualTo(d2);
    }

    @Test
    void testEqualsWithRequestMapNullVsNonNull() {
        ServiceRequestDto d1 = new ServiceRequestDto();
        d1.setHeaderMap(Map.of("h", "1"));
        d1.setUrlMap(Map.of("u", "2"));
        d1.setRequestMap(null);
        ServiceRequestDto d2 = new ServiceRequestDto();
        d2.setHeaderMap(Map.of("h", "1"));
        d2.setUrlMap(Map.of("u", "2"));
        d2.setRequestMap(null);
        assertThat(d1).isEqualTo(d2);
        d2.setRequestMap(Map.of("r", "3"));
        assertThat(d1).isNotEqualTo(d2);
    }

    @Test
    void testHashCodeWithNullFields() {
        ServiceRequestDto d1 = new ServiceRequestDto();
        d1.setHeaderMap(null);
        d1.setUrlMap(null);
        d1.setRequestMap(null);
        ServiceRequestDto d2 = new ServiceRequestDto();
        d2.setHeaderMap(null);
        d2.setUrlMap(null);
        d2.setRequestMap(null);
        assertThat(d1)
                .isEqualTo(d2)
                .hasSameHashCodeAs(d2);
    }


}
