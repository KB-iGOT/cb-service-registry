package com.igot.service_locator.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ServiceLocatorDto {
  private List<String> ids;
  private String url;
  private String serviceCode;
  private String  serviceName;
  private String operationType;
  private Boolean active;

}
