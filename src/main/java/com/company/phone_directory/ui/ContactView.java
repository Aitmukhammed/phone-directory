package com.company.phone_directory.ui;

import com.company.phone_directory.model.Contact;
import com.company.phone_directory.repository.ContactRepository;
import com.company.phone_directory.service.ReportService;
import com.company.phone_directory.utils.Validators;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

@Route("contacts")
public class ContactView extends VerticalLayout {

    private final ContactRepository contactRepository;
    private final Grid<Contact> grid;
    private final Dialog contactDialog;
    private final TextField nameField;
    private final TextField phoneField;
    private final TextField emailField;
    private Contact currentContact;
    private Anchor downloadLink;

    @Autowired
    public ContactView(ContactRepository contactRepository, ReportService reportService) {
        this.contactRepository = contactRepository;

        downloadLink = new Anchor();
        downloadLink.getElement().setAttribute("download", true);
        add(downloadLink);

        grid = new Grid<>(Contact.class);
        grid.setColumns("name", "phone", "email");
        grid.addComponentColumn(contact -> createEditButton(contact)).setHeader("Edit");
        grid.addComponentColumn(contact -> createDeleteButton(contact)).setHeader("Delete");

        contactDialog = new Dialog();
        nameField = new TextField("Name");
        phoneField = new TextField("Phone");
        emailField = new TextField("Email");
        Button saveButton = new Button("Save", e -> saveContact());
        contactDialog.add(new FormLayout(nameField, phoneField, emailField, saveButton));

        Button addContactButton = new Button("Add Contact", e -> openContactDialog(null));

        Button downloadPdfButton = new Button("Download PDFsss", e -> {
            try {
                byte[] pdfContent = reportService.generateReport();
                StreamResource resource = new StreamResource("contacts_report.pdf",
                        () -> new ByteArrayInputStream(pdfContent));

                downloadLink.setHref(resource);
                downloadLink.getElement().callJsFunction("click");

                Notification.show("PDF report is ready for download.");
            } catch (Exception ex) {
                Notification.show("Error generating PDF: " + ex.getMessage());
            }
        });

        add(addContactButton, downloadPdfButton, grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(contactRepository.findAll());
    }

    private void openContactDialog(Contact contact) {
        if (contact != null) {
            currentContact = contact;
            nameField.setValue(contact.getName());
            phoneField.setValue(contact.getPhone());
            emailField.setValue(contact.getEmail());
        } else {
            currentContact = new Contact();
            nameField.clear();
            phoneField.clear();
            emailField.clear();
        }
        contactDialog.open();
    }

    private void saveContact() {
        currentContact.setName(nameField.getValue());

        if(Validators.isValidPhoneNumber(phoneField.getValue())) {
            String formattedPhoneNumber = Validators.phoneFormatter(phoneField.getValue());
            currentContact.setPhone(formattedPhoneNumber);
        } else {
            Notification.show("Invalid phone number!", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if(Validators.emailIsValid(emailField.getValue())) {
            currentContact.setEmail(emailField.getValue());
        } else {
            Notification.show("Invalid email address!", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        contactRepository.save(currentContact);
        updateGrid();
        contactDialog.close();
        Notification.show("Contact saved successfully!");
    }

    private Button createEditButton(Contact contact) {
        return new Button("Edit", e -> openContactDialog(contact));
    }

    private Button createDeleteButton(Contact contact) {
        return new Button("Delete", e -> {
            contactRepository.delete(contact);
            updateGrid();
            Notification.show("Contact deleted successfully!");
        });
    }
}
