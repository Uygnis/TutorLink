package com.csy.springbootauthbe.tutor.mapper;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TutorMapper {

    @Mapping(source = "profileImageUrl", target = "profileImageUrl")
    @Mapping(target = "qualifications", expression = "java(filterQualifications(tutor.getQualifications()))")
    TutorDTO toDTO(Tutor tutor);

    default List<QualificationFile> filterQualifications(List<QualificationFile> qualifications) {
        return qualifications == null ? List.of() :
                qualifications.stream()
                        .filter(q -> !q.isDeleted())
                        .collect(Collectors.toList());
    }

    Tutor toEntity(TutorDTO tutorDTO);
}
