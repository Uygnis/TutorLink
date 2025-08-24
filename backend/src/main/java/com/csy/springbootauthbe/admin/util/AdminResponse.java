package com.csy.springbootauthbe.admin.util;

import java.util.List;

import com.csy.springbootauthbe.admin.entity.Permissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private List<Permissions> permissions;
}