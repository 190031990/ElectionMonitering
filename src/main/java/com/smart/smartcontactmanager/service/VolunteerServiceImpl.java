package com.smart.smartcontactmanager.service;

import com.smart.smartcontactmanager.dao.VolunteerRepository;
import com.smart.smartcontactmanager.entities.Volunteer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerServiceImpl implements VolunteerService{

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Override
    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }
}
