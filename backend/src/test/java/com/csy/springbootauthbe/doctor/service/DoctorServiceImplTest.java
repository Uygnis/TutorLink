package com.csy.springbootauthbe.doctor.service;

import com.csy.springbootauthbe.doctor.dto.DoctorDTO;
import com.csy.springbootauthbe.doctor.entity.Doctor;
import com.csy.springbootauthbe.doctor.mapper.DoctorMapper;
import com.csy.springbootauthbe.doctor.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock DoctorRepository doctorRepository;
    @Mock DoctorMapper doctorMapper;

    @InjectMocks DoctorServiceImpl doctorService;

    // getAllDoctors
    @Test
    void getAllDoctors_mapsAllEntitiesToDtos() {
        Doctor e1 = new Doctor(); Doctor e2 = new Doctor();
        DoctorDTO d1 = new DoctorDTO(); DoctorDTO d2 = new DoctorDTO();

        when(doctorRepository.findAll()).thenReturn(List.of(e1, e2));
        when(doctorMapper.toDto(e1)).thenReturn(d1);
        when(doctorMapper.toDto(e2)).thenReturn(d2);

        List<DoctorDTO> result = doctorService.getAllDoctors();

        assertEquals(2, result.size());
        assertSame(d1, result.get(0));
        assertSame(d2, result.get(1));
    }

    // getDoctorById
    @Test
    void getDoctorById_found_returnsDto() throws Exception {
        Doctor e = new Doctor();
        DoctorDTO d = new DoctorDTO();
        when(doctorRepository.findById("1")).thenReturn(Optional.of(e));
        when(doctorMapper.toDto(e)).thenReturn(d);

        DoctorDTO result = doctorService.getDoctorById("1");

        assertSame(d, result);
    }

    @Test
    void getDoctorById_notFound_throws() {
        when(doctorRepository.findById("404")).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> doctorService.getDoctorById("404"));
        assertTrue(ex.getMessage().contains("Doctor not found"));
    }

    // createDoctor
    @Test
    void createDoctor_mapsSavesAndReturnsDto() {
        DoctorDTO input = new DoctorDTO();
        Doctor mapped = new Doctor();
        Doctor saved = new Doctor();
        DoctorDTO out = new DoctorDTO();

        when(doctorMapper.toEntity(input)).thenReturn(mapped);
        when(doctorRepository.save(mapped)).thenReturn(saved);
        when(doctorMapper.toDto(saved)).thenReturn(out);

        DoctorDTO result = doctorService.createDoctor(input);

        assertSame(out, result);
        verify(doctorMapper).toEntity(input);
        verify(doctorRepository).save(mapped);
        verify(doctorMapper).toDto(saved);
    }

    // updateDoctor
    @Test
    void updateDoctor_found_updatesAndReturnsDto() throws Exception {
        Doctor existing = new Doctor();
        DoctorDTO patch = new DoctorDTO();
        Doctor saved = new Doctor();
        DoctorDTO out = new DoctorDTO();

        when(doctorRepository.findById("1")).thenReturn(Optional.of(existing));
        // void mapping method
        doNothing().when(doctorMapper).updateEntityFromDto(patch, existing);
        when(doctorRepository.save(existing)).thenReturn(saved);
        when(doctorMapper.toDto(saved)).thenReturn(out);

        DoctorDTO result = doctorService.updateDoctor("1", patch);

        assertSame(out, result);
        verify(doctorMapper).updateEntityFromDto(patch, existing);
        verify(doctorRepository).save(existing);
    }

    @Test
    void updateDoctor_notFound_throws() {
        when(doctorRepository.findById("NOPE")).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> doctorService.updateDoctor("NOPE", new DoctorDTO()));
        assertTrue(ex.getMessage().contains("Doctor not found"));
        verify(doctorRepository, never()).save(any());
    }

    // deleteDoctor
    @Test
    void deleteDoctor_exists_deletes() throws Exception {
        when(doctorRepository.existsById("7")).thenReturn(true);

        doctorService.deleteDoctor("7");

        verify(doctorRepository).deleteById("7");
    }

    @Test
    void deleteDoctor_notExists_throws() {
        when(doctorRepository.existsById("7")).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> doctorService.deleteDoctor("7"));
        assertTrue(ex.getMessage().contains("Doctor not found"));
        verify(doctorRepository, never()).deleteById(any());
    }
}
