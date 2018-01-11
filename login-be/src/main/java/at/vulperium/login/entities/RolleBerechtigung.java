package at.vulperium.login.entities;

import javax.persistence.*;

/**
 * Created by 02ub0400 on 06.07.2017.
 */
@Entity
@Table(name = "ROLLE_BERECHTIGUNG",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ROLLE_FK", "BERECHTIGUNG_FK"}),
        }
)
@NamedQueries({
        @NamedQuery(name = RolleBerechtigung.QRY_FIND_BERECHTIGUNG_BY_ROLLE_IDS,
                query = "SELECT p FROM RolleBerechtigung p where p.rolle.id IN (:" + RolleBerechtigung.QRY_PARAM_ROLLEIDS + ")"
        ),
        @NamedQuery(name = RolleBerechtigung.QRY_FIND_BERECHTIGUNG_BY_ROLLEN_BEZEICHNUNGEN,
                query = "SELECT p FROM RolleBerechtigung p where p.rolle.bezeichnung IN (:" + RolleBerechtigung.QRY_PARAM_ROLLEN_BEZEICHNUNGEN + ")"
        ),
        @NamedQuery(name = RolleBerechtigung.QRY_FIND_ALL_ROLLE_BERECHTIGUNG,
                query = "SELECT p FROM RolleBerechtigung p"
        )
})
public class RolleBerechtigung {

    public static final String QRY_FIND_BERECHTIGUNG_BY_ROLLE_IDS = "RolleBerechtigung.findByRolleIdSet";
    public static final String QRY_PARAM_ROLLEIDS = "rolleIdSet";
    public static final String QRY_FIND_BERECHTIGUNG_BY_ROLLEN_BEZEICHNUNGEN = "RolleBerechtigung.findByRolleNamenSet";
    public static final String QRY_PARAM_ROLLEN_BEZEICHNUNGEN = "namenSet";
    public static final String QRY_FIND_ALL_ROLLE_BERECHTIGUNG = "RolleBerechtigung.findAllRolleBerechtigung";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLLE_BERECHTIGUNG_SQ")
    @SequenceGenerator(name = "ROLLE_BERECHTIGUNG_SQ", sequenceName = "ROLLE_BERECHTIGUNG_SQ", allocationSize = 1, initialValue = 2000000)
    @Column(name = "ROLLE_BERECHTIGUNG_PK", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLLE_FK", nullable = false, updatable = false)
    private Rolle rolle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "BERECHTIGUNG_FK", nullable = false, updatable = false)
    private Berechtigung berechtigung;

    public Berechtigung getBerechtigung() {
        return berechtigung;
    }

    public void setBerechtigung(Berechtigung berechtigung) {
        this.berechtigung = berechtigung;
    }

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
}
