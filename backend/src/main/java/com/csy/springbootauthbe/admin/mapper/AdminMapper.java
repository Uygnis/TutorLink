package com.csy.springbootauthbe.admin.mapper;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.entity.Admin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminDTO toDTO(Admin admin);

    Admin toEntity(AdminDTO adminDTO);
}
