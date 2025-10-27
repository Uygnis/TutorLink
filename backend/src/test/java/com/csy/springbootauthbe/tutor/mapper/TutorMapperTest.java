package com.csy.springbootauthbe.tutor.mapper;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class TutorMapperTest {

    private final TutorMapper mapper = Mappers.getMapper(TutorMapper.class);

    @Test
    void toDto_and_back_roundTrip() {
        Tutor entity = new Tutor();
        entity.setId("T1");
        entity.setUserId("U1");

        TutorDTO dto = mapper.toDTO(entity);
        assertEquals("T1", dto.getId());
        assertEquals("U1", dto.getUserId());

        Tutor back = mapper.toEntity(dto);
        assertEquals("T1", back.getId());
        assertEquals("U1", back.getUserId());
    }
}
