package at.vulperium.login.entities;

import javax.persistence.*;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
@Entity
@Table(name = "ROLLE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "BEZEICHNUNG")
        })
@NamedQueries({
        @NamedQuery(name = Rolle.QRY_FIND_ALL_ROLLE,
                query = "SELECT p FROM Rolle p"
        )
})
public class Rolle {

    public static final String QRY_FIND_ALL_ROLLE = "Rolle.findAllRolle";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLLE_SQ")
    @SequenceGenerator(name = "ROLLE_SQ", sequenceName = "ROLLE_SQ", allocationSize = 1, initialValue = 2000000)
    @Column(name = "ROLLE_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "BEZEICHNUNG", length = 50, nullable = false)
    private String bezeichnung;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
