package org.example.basewebsub.entity;

import org.example.basewebsub.entity.base.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
public class UsersEntity extends BaseEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "USERNAME")
    private String username;

    @Basic
    @Column(name = "FULL_NAME")
    private String fullname;

    @Basic
    @Column(name = "EMAIL")
    private String email;

    @Basic
    @Column(name = "PHONE")
    private String phone;

    @Basic
    @Column(name = "PASSWORD")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
