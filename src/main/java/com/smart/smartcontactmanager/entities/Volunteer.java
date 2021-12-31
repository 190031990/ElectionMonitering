package com.smart.smartcontactmanager.entities;

import javax.persistence.*;

@Entity
@Table(name="VOLUNTEER")
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int vId;
    private String name;
    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String image;
    @Column(length=5000)
    private String description;


    public Volunteer() {
        super();
    }

    public int getvId() {
        return vId;
    }

    public void setvId(int vId) {
        this.vId = vId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Volunteer{" +
                "vId=" + vId +
                ", name='" + name + '\'' +
                ", secondName='" + secondName + '\'' +
                ", work='" + work + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}

