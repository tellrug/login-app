package at.vulperium.login.entities;

import javax.persistence.*;

/**
 * Created by 02ub0400 on 06.07.2017.
 */
@Entity
@Table(name = "USER_ROLLE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"USER_FK", "ROLLE_FK"}),
        }
)
@NamedQueries({
        @NamedQuery(name = UserRolle.QRY_FIND_ROLLE_BY_USERID,
                query = "SELECT p FROM UserRolle p where p.user.id = :" + UserRolle.QRY_PARAM_USERID
        )
})
public class UserRolle {

    public static final String QRY_FIND_ROLLE_BY_USERID = "UserRolle.findByUserId";
    public static final String QRY_PARAM_USERID = "userid";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ROLLE_SQ")
    @SequenceGenerator(name = "USER_ROLLE_SQ", sequenceName = "USER_ROLLE_SQ", allocationSize = 1, initialValue = 2000000)
    @Column(name = "USER_ROLLE_PK", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_FK", nullable = false, updatable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLLE_FK", nullable = false, updatable = false)
    private Rolle rolle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
