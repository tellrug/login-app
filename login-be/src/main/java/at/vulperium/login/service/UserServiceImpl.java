package at.vulperium.login.service;

import at.vulperium.login.dto.UserBearbeitungsDTO;
import at.vulperium.login.dto.UserDatenDTO;
import at.vulperium.login.utils.UserInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Created by 02ub0400 on 18.09.2017.
 */
@ApplicationScoped
public class UserServiceImpl implements UserInfoService {

    private @Inject UserBearbeitungsService userBearbeitungsService;

    @Override
    public UserInfo holeUserInfo(Long userId) {
        UserBearbeitungsDTO userBearbeitungsDTO = userBearbeitungsService.holeUserBearbeitungsDTO(userId);
        if (userBearbeitungsDTO == null) {
            //User konnte nicht gefunden werden
            return null;
        }

        return ermittleUserInfo(userBearbeitungsDTO);
    }

    @Override
    public UserInfo holeUserInfo(String username) {
        UserBearbeitungsDTO userBearbeitungsDTO = userBearbeitungsService.holeUserBearbeitungsDTO(username);
        if (userBearbeitungsDTO == null) {
            //User konnte nicht gefunden werden
            return null;
        }

        return ermittleUserInfo(userBearbeitungsDTO);
    }

    private UserInfo ermittleUserInfo(UserBearbeitungsDTO userBearbeitungsDTO) {
        UserDatenDTO userDatenDTO = userBearbeitungsDTO.getUserDatenDTO();
        return new UserInfo(userDatenDTO.getId(), userDatenDTO.getName(), userDatenDTO.getUsername(), userDatenDTO.getEmail(),
                userDatenDTO.getUserStatus(), userBearbeitungsDTO.getRollen(), userBearbeitungsDTO.getBerechtigungen());
    }
}
