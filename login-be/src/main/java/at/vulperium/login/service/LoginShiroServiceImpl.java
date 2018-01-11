package at.vulperium.login.service;

import at.vulperium.persistence.ShiroDb;
import at.vulperium.usermanager.service.UserBearbeitungsService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.io.Serializable;

/**
 * Created by 02ub0400 on 23.01.2017.
 */
@ApplicationScoped
public class LoginShiroServiceImpl implements LoginShiroService, Serializable {

    public static final Logger logger = LoggerFactory.getLogger(LoginShiroServiceImpl.class);

    private static final long serialVersionUID = 2675186880974970666L;

    private @Inject UserBearbeitungsService userBearbeitungsService;
    private @Inject @ShiroDb EntityManager em;

    @Override
    public String getUserInfo() {
        Subject currentUser = SecurityUtils.getSubject();
        return getUserInfo(currentUser);
    }

    @Override
    public boolean login(String username, String password) {
        logger.info("Versuch von Anmeldung des Users: {}.", username);
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // ”Remember Me” built-in, just do this:
            //token.setRememberMe(true);

            // With most of Shiro, you'll always want to make sure you're working with the currently executing user,
            // referred to as the subject
            Subject currentUser = SecurityUtils.getSubject();

            // Authenticate
            currentUser.login(token);
        }
        catch (Throwable e) {
            //TODO Fehlerbehandlung
            return false;
        }
        return true;
    }

    @Override
    public void logout() {
        Subject currentUser = SecurityUtils.getSubject();

        if (currentUser != null) {
            logger.info("Logout von User {} wird durchgefuehrt!", getUserInfo(currentUser));
            currentUser.logout();
        }
        else {
            logger.warn("Logout nicht erfolgreich - Kein User angemeldet!");
        }
    }

    public SimpleAuthenticationInfo getSimpleAuthenticationInfo(String username, String realmName) {
        SimpleAuthenticationInfo simpleAuthenticationInfo = null;

        Query query = em.createNativeQuery("SELECT passwort FROM USER where username = ? ", String.class);
        query.setParameter(1, username);

        try {
            String passwort = (String) query.getSingleResult();
            simpleAuthenticationInfo = new SimpleAuthenticationInfo(username, passwort.toCharArray(), realmName);
        }
        catch (NoResultException e1) {
            logger.warn("Versuch von Anmeldung des Users {} fehlgeschlagen. Kein User mit diesem Username gefunden!", username);
        }
        catch (NonUniqueResultException e2) {
            logger.warn("Versuch von Anmeldung des Users {} fehlgeschlagen. Mehrere User mit dem gleichen Username vorhanden!", username);
        }

        return simpleAuthenticationInfo;
    }

    private String getUserInfo(Subject currentUser) {
        if (currentUser != null) {
            return currentUser.getPrincipal().toString();
        }
        return null;
    }
}
