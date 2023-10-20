package org.example.HW19.service;

import org.example.HW19.entity.Duty;
import org.example.HW19.exceptions.DutyExistException;
import org.example.HW19.exceptions.DutyNotFoundException;
import org.example.HW19.repository.DutyRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DutyService {

    private final DutyRepository dutyRepository;

    public DutyService(DutyRepository dutyRepository) {
        this.dutyRepository = dutyRepository;
    }

    public Duty save(Duty duty) {
        Duty foundedDuty = dutyRepository.findByName(duty.getName());
        if (foundedDuty == null) {
            return dutyRepository.save(duty);
        } else throw new DutyExistException("a duty already exists with this name.");
    }

    public List<Duty> findAll() {
        return dutyRepository.findAll();
    }

    public Duty findById(long id) {
        return dutyRepository.findById(id).orElseThrow(() -> new DutyNotFoundException("no duty found with this ID."));
    }
}
