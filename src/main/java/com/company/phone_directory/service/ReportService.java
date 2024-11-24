package com.company.phone_directory.service;

import com.company.phone_directory.model.Contact;
import com.company.phone_directory.repository.ContactRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    private final ContactRepository contactRepository;

    @Autowired
    public ReportService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public byte[] generateReport() throws DocumentException {
        List<Contact> contacts = contactRepository.findAll();
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        document.add(new Paragraph("Phonebook Report"));
        for (Contact contact : contacts) {
            document.add(new Paragraph("Name: " + contact.getName()));
            document.add(new Paragraph("Phone: " + contact.getPhone()));
            document.add(new Paragraph("Email: " + contact.getEmail()));
            document.add(new Paragraph("-----------------------------"));
        }
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}