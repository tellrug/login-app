package at.vulperium.login.dto;

import at.vulperium.login.enums.UserStatus;

import java.io.Serializable;

/**
 * Kapselt alle persoenlichen Informationen eines Users. Name, Mail, ...
 */
public class UserDatenDTO implements Serializable {

    private static final long serialVersionUID = 2407764753782652656L;

    private Long id;
    private String username;
    private String name;
    private String email;
    private UserStatus userStatus;

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
