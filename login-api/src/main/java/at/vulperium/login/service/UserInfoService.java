package at.vulperium.login.service;


import at.vulperium.login.utils.UserInfo;

/**
 * Created by 02ub0400 on 18.09.2017.
 */
public interface UserInfoService {

    UserInfo holeUserInfo(Long userId);

    UserInfo holeUserInfo(String username);
}
