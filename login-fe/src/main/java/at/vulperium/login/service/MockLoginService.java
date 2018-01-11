package at.vulperium.login.service;


import at.vulperium.usermanager.UserInfo;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Created by 02ub0400 on 04.07.2017.
 */
@Dependent
public class
MockLoginService extends BaseLoginService {

    private @Inject UserInfoService userInfoService;

    @Override
    protected UserInfo getUserInfo(String username) {
        return userInfoService.holeUserInfo(username);
    }
}
