package com.csy.springbootauthbe.doctor.service;

import com.csy.springbootauthbe.doctor.dto.DoctorDTO;

import java.util.List;

public interface DoctorService {

    List<DoctorDTO> getAllDoctors();

    DoctorDTO getDoctorById(String id) throws Exception;

    DoctorDTO createDoctor(DoctorDTO doctorDto);

    DoctorDTO updateDoctor(String id, DoctorDTO doctorDto) throws Exception;

    void deleteDoctor(String id) throws Exception;
}
