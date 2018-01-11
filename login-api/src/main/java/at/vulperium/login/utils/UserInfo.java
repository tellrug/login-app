package at.vulperium.login.utils;

import at.vulperium.login.enums.BerechtigungEnum;
import at.vulperium.login.enums.RolleEnum;
import at.vulperium.login.enums.UserStatus;

import javax.enterprise.inject.Typed;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;

/**
 * Kapselt alle notwendigen Informationen eines Users.
 * Es koennen keine Daten bearbeitet werden
 */
@Typed()
public class UserInfo implements Serializable, Principal {

    private static final long serialVersionUID = 4829521950673976565L;

    private Long id;
    private String username;
    private String name;
    private String email;
    private UserStatus userStatus;

    private Set<RolleEnum> rollen;
    private Set<BerechtigungEnum> berechtigungen;

    public UserInfo() {
    }

    public UserInfo(Long id, String name, String username, String email, UserStatus userStatus, Set<RolleEnum> rollen, Set<BerechtigungEnum> berechtigungen) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.username = username;
        this.userStatus = userStatus;

        this.rollen = Collections.unmodifiableSet(rollen);
        this.berechtigungen = Collections.unmodifiableSet(berechtigungen);
    }

    public Set<BerechtigungEnum> getBerechtigungen() {
        return berechtigungen;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<RolleEnum> getRollen() {
        return rollen;
    }

    public String getUsername() {
        return username;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserInfo userinfo = (UserInfo) o;
        return id.equals(userinfo.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
