package org.example.belsign.be;

public class Admin {
    private String adminFirstName;
    private String adminLastName;
    private int adminID;

    public Admin(int adminId, String firstName, String lastName) {
        this.adminID = adminId;
        this.adminFirstName = firstName;
        this.adminLastName = lastName;
    }

    public String getAdminFirstName() {
        return adminFirstName;
    }

    public void setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
    }

    public String getAdminLastName() {
        return adminLastName;
    }

    public void setAdminLastName(String adminLastName) {
        this.adminLastName = adminLastName;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }
}
