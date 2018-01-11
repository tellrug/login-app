package at.vulperium.login.entities;

import javax.persistence.*;

/**
 * Created by 02ub0400 on 06.07.2017.
 */
@Entity
@Table(name = "USER",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "USERNAME")
        }
)
@NamedQueries({
        @NamedQuery(name = User.QRY_FIND_USER_BY_USERNAME,
                query = "SELECT p FROM User p WHERE p.username = :" + User.PARAM_USERNAME
        ),
        @NamedQuery(name = User.QRY_FIND_ALL_USERS,
                query = "SELECT p FROM User p"
        )
        ,
        @NamedQuery(name = User.QRY_CHECK_PW,
                query = "SELECT p FROM User p WHERE p.id = :" + User.PARAM_USERID + " AND p.passwort = :" + User.PARAM_PW
        )
})
public class User {

    public static final String QRY_FIND_USER_BY_USERNAME = "User.findUserByUserName";
    public static final String QRY_FIND_ALL_USERS = "User.findAllUsers";
    public static final String QRY_CHECK_PW = "User.checkPw";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PW = "pw";
    public static final String PARAM_USERID = "userId";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SQ")
    @SequenceGenerator(name = "USER_SQ", sequenceName = "USER_SQ", allocationSize = 1, initialValue = 2000000)
    @Column(name = "USER_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "USERNAME", length = 50, nullable = false)
    private String username;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "PASSWORT", length = 255, nullable = false)
    private String passwort;

    @Column(name = "EMAIL", length = 50, nullable = false)
    private String email;

    @Column(name = "AKTIV", length = 1, nullable = false)
    private Boolean aktiv;

    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
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

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
