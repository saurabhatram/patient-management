package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
private PatientRepository patientRepository;

public PatientService(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
}

public List<PatientResponseDTO> getPatients() {
    List<Patient> patients = patientRepository.findAll();
    //.map(patient -> PatientMapper.toDTO(patient)).toList();
    return patients.stream()
            .map(PatientMapper::toDTO).toList();
}
public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
    if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
        throw new EmailAlreadyExistsException("A patient of this email already exists" + patientRequestDTO.getEmail());
    }

    Patient newPatient = patientRepository.save(
            PatientMapper.toModel(patientRequestDTO));
    //we are mapping dto to entity through model and once it is saved in repo and then in db we are getting back the entity which
    //again we are mapping and sending it back as a dto
    return PatientMapper.toDTO(newPatient);
}

public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
    Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found with ID"+ id));
    if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
        throw new EmailAlreadyExistsException("A patient of this email already exists" + patientRequestDTO.getEmail());
    }
    patient.setName(patientRequestDTO.getName());
    patient.setAddress(patientRequestDTO.getAddress());
    patient.setEmail(patientRequestDTO.getEmail());
    patient.setDate_of_birth(LocalDate.parse(patientRequestDTO.getDate_of_birth()));

    Patient updatedPatient = patientRepository.save(patient);
    return PatientMapper.toDTO(updatedPatient);
}

public void deletePatient(UUID id) {
    patientRepository.deleteById(id);
}

}
