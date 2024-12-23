package model;

import java.util.Arrays;

public class User {
    private int id;
    private String name;
    private String phone;
    private String password;
    private User[] contacts;

    // constructor
    public User(int id, String name, String phone, String password, Object[] contacts) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public User() {
    }

    public User(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User(String phone) {
        this.phone = phone;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object[] getContacts() {
        return contacts;
    }

    public void setContacts(User[] contacts) {
        this.contacts = contacts;
    }

    // toString method
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", phone=" + phone + ", password=" + password + ", contacts="
                + Arrays.toString(contacts) + "]";
    }

    // equals method
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return false;
    }

    // hashCode method
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + Arrays.hashCode(contacts);
        return result;
    }
}
