package com.smart.smartcontactmanager.dao;

import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.Queries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueriesRepository extends JpaRepository<Queries,Integer> {
}
