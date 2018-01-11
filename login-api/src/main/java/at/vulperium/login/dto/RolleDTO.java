package at.vulperium.login.dto;


import at.vulperium.login.enums.BerechtigungEnum;
import at.vulperium.login.enums.RolleEnum;

import java.util.Set;

/**
 * Dieses DTO beinhaltet Mapping zwischen einer Rolle und zugehoerigen Berechtigungen.
 * Wird in erster Linie nur bei der Bearbeitung von Usern benoetigt.
 */
public class RolleDTO {

    private Long id;
    private RolleEnum rolle;

    private Set<BerechtigungEnum> berechtigungen;

    public RolleDTO(Long id, RolleEnum rolle, Set<BerechtigungEnum> berechtigungen) {
        this.id = id;
        this.rolle = rolle;
        this.berechtigungen = berechtigungen;
    }

    public Set<BerechtigungEnum> getBerechtigungen() {
        return berechtigungen;
    }

    public RolleEnum getRolle() {
        return rolle;
    }

    public Long getId() {
        return id;
    }
}
