package at.vulperium.login.utils;


import at.vulperium.login.dto.UserBearbeitungsDTO;
import at.vulperium.login.enums.BerechtigungEnum;

/**
 * Created by 02ub0400 on 12.07.2017.
 */
public class UserInfoUtil {

    public static boolean hatUserBerechtigung(UserBearbeitungsDTO userBearbeitungsDTO, BerechtigungEnum berechtigung) {
        return userBearbeitungsDTO != null && userBearbeitungsDTO.getBerechtigungen() != null && userBearbeitungsDTO.getBerechtigungen().contains(berechtigung);
    }
}
