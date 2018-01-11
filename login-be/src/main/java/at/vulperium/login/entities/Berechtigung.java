package at.vulperium.login.entities;

import javax.persistence.*;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
@Entity
@Table(name = "BERECHTIGUNG",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "BEZEICHNUNG")
        })
@NamedQueries({
        @NamedQuery(name = Berechtigung.QRY_FIND_ALL_BERECHTIGUNG,
                query = "SELECT p FROM Berechtigung p"
        )
})
public class Berechtigung {

    public static final String QRY_FIND_ALL_BERECHTIGUNG = "Berechtigung.findAllBerechtigung";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BERECHTIGUNG_SQ")
    @SequenceGenerator(name = "BERECHTIGUNG_SQ", sequenceName = "BERECHTIGUNG_SQ", allocationSize = 1, initialValue = 2000000)
    @Column(name = "BERECHTIGUNG_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "BEZEICHNUNG", length = 100, nullable = false)
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
