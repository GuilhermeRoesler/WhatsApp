package model;

public class ContactVerificationRequest {
    private String currentUserPhone;
    private String contactPhone;

    public ContactVerificationRequest(String currentUserPhone, String contactPhone) {
        this.currentUserPhone = currentUserPhone;
        this.contactPhone = contactPhone;
    }

    // Getters and setters
    public String getCurrentUserPhone() {
        return currentUserPhone;
    }

    public String getContactPhone() {
        return contactPhone;
    }
}
