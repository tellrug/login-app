package at.vulperium.login.dto;


import at.vulperium.login.enums.BerechtigungEnum;
import at.vulperium.login.enums.RolleEnum;

import java.io.Serializable;
import java.util.Set;

/**
 * Kapselt alle Informationen eines Users. Userinformationen, Rollen, ...
 */
public class UserBearbeitungsDTO implements Serializable {

    private static final long serialVersionUID = -7771040778236517558L;

    private UserDatenDTO userDatenDTO;
    private Set<RolleEnum> rollen;
    private Set<BerechtigungEnum> berechtigungen;


    public UserBearbeitungsDTO(UserDatenDTO userDatenDTO, Set<RolleEnum> rollen, Set<BerechtigungEnum> berechtigungen) {
        this.userDatenDTO = userDatenDTO;
        this.rollen = rollen;
        this.berechtigungen = berechtigungen;
    }

    public Set<BerechtigungEnum> getBerechtigungen() {
        return berechtigungen;
    }

    public Set<RolleEnum> getRollen() {
        return rollen;
    }

    public UserDatenDTO getUserDatenDTO() {
        return userDatenDTO;
    }
}
