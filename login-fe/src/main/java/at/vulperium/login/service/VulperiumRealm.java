package at.vulperium.login.service;


import at.vulperium.login.dto.UserBearbeitungsDTO;
import at.vulperium.login.enums.BerechtigungEnum;
import at.vulperium.login.enums.RolleEnum;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
public class VulperiumRealm extends AuthorizingRealm {

    private UserBearbeitungsService userBearbeitungsService = BeanProvider.getContextualReference(UserBearbeitungsService.class);
    private LoginShiroServiceImpl loginShiroServiceImpl = BeanProvider.getContextualReference(LoginShiroServiceImpl.class);


    public VulperiumRealm() {
        setCredentialsMatcher(new SimpleCredentialsMatcher());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = (String) getAvailablePrincipal(principals);
        UserBearbeitungsDTO userBearbeitungsDTO = userBearbeitungsService.holeUserBearbeitungsDTO(username);

        if (userBearbeitungsDTO == null) {
            throw new AuthorizationException("Fuer den User " + username + " konnten keine Rollen/Berechtigungen ermittelt werden!");
        }

        Set<String> rollen = userBearbeitungsDTO.getRollen().stream().map(RolleEnum::getBezeichnung).collect(Collectors.toSet());
        Set<String> berechtigungen = userBearbeitungsDTO.getBerechtigungen().stream().map(BerechtigungEnum::getBezeichnung).collect(Collectors.toSet());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(rollen);
        info.setStringPermissions(berechtigungen);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        //Holen der Informationen aus Security-DB
        SimpleAuthenticationInfo simpleAuthenticationInfo = loginShiroServiceImpl.getSimpleAuthenticationInfo(username, getName());
        if (simpleAuthenticationInfo == null) {
            throw new UnknownAccountException("No account found for user [" + username + "]");
        }

        return simpleAuthenticationInfo;
    }
}
