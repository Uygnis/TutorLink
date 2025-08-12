package com.csy.springbootauthbe.tutor.mapper;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TutorMapper {

    TutorDTO toDTO(Tutor tutor);

    Tutor toEntity(TutorDTO tutorDTO);
}
