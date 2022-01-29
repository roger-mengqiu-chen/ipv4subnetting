package com.mengqiu.ipv4subnetting.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class NetsRequest {
    @JsonProperty
    String hostIp;
    @JsonProperty
    String mask;
    @JsonProperty
    Map<String, Integer> subnets;
}
