package com.company.phone_directory.repository;


import com.company.phone_directory.model.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact, String> {
}