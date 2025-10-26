package com.csy.springbootauthbe.common.aws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwsResponse {
    String hash;
    String key;
}
