package at.vulperium.login.service;


import at.vulperium.login.dto.UserBearbeitungsDTO;
import at.vulperium.login.dto.UserDatenDTO;

import java.util.List;

/**
 * Created by 02ub0400 on 06.07.2017.
 */
public interface UserBearbeitungsService {

    UserDatenDTO holeUserDatenDTO(Long userId);

    UserBearbeitungsDTO holeUserBearbeitungsDTO(Long userId);

    UserDatenDTO holeUserDatenDTO(String userId);

    UserBearbeitungsDTO holeUserBearbeitungsDTO(String username);

    boolean checkPW(Long userId, String passwort);

    boolean aktualisierePW(Long userId, String altesPW, String neuesPW);

    List<UserBearbeitungsDTO> holeAlleUserBearbeitungsDTOs();

    Long legeNeuenUserAn(UserBearbeitungsDTO userBearbeitungsDTO, String passwort);

    UserBearbeitungsDTO aktualisiereUser(UserBearbeitungsDTO userBearbeitungsDTO, String passwort);

    String erzeugePasswortHash(String passwort);
}
