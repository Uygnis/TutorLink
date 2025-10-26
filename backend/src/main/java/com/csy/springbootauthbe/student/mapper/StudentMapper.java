package com.csy.springbootauthbe.student.mapper;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(source = "profileImageUrl", target = "profileImageUrl")
    StudentDTO toDTO(Student student);

    Student toEntity(StudentDTO studentDTO);
}