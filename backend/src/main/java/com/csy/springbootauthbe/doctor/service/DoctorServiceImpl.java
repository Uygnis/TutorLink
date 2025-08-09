package com.csy.springbootauthbe.doctor.service;

import com.csy.springbootauthbe.doctor.dto.DoctorDTO;
import com.csy.springbootauthbe.doctor.entity.Doctor;
import com.csy.springbootauthbe.doctor.mapper.DoctorMapper;
import com.csy.springbootauthbe.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO getDoctorById(String id) throws Exception {
        Doctor doctor = doctorRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new Exception("Doctor not found with id: " + id));
        return doctorMapper.toDto(doctor);
    }

    @Override
    public DoctorDTO createDoctor(DoctorDTO doctorDto) {
        Doctor doctor = doctorMapper.toEntity(doctorDto);
        Doctor saved = doctorRepository.save(doctor);
        return doctorMapper.toDto(saved);
    }

    @Override
    public DoctorDTO updateDoctor(String id, DoctorDTO doctorDto) throws Exception {
        Doctor doctor = doctorRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new Exception("Doctor not found with id: " + id));

        doctorMapper.updateEntityFromDto(doctorDto, doctor);

        Doctor updated = doctorRepository.save(doctor);
        return doctorMapper.toDto(updated);
    }

    @Override
    public void deleteDoctor(String id) throws Exception {
        if (!doctorRepository.existsById(String.valueOf(id))) {
            throw new Exception("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(String.valueOf(id));
    }
}
