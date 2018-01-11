package at.vulperium.login.service;

/**
 * Created by 02ub0400 on 23.01.2017.
 */
public interface LoginShiroService {

    boolean login(String username, String password);

    void logout();

    String getUserInfo();



}
