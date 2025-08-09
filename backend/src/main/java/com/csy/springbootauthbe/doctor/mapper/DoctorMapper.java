package com.csy.springbootauthbe.doctor.mapper;

import com.csy.springbootauthbe.doctor.dto.DoctorDTO;
import com.csy.springbootauthbe.doctor.entity.Doctor;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorDTO toDto(Doctor doctor);

    Doctor toEntity(DoctorDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(DoctorDTO dto, @MappingTarget Doctor entity);
}
