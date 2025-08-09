package com.csy.springbootauthbe.patient.mapper;

import com.csy.springbootauthbe.patient.dto.PatientDTO;
import com.csy.springbootauthbe.patient.entity.Patient;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDTO toDto(Patient patient);

    Patient toEntity(PatientDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PatientDTO dto, @MappingTarget Patient entity);
}
