package at.vulperium.login.service;



import at.vulperium.login.LoginConstants;
import at.vulperium.login.utils.UserInfo;
import at.vulperium.login.utils.UserInfoProducer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by 02ub0400 on 04.07.2017.
 */
public abstract class BaseLoginService implements LoginService {

    public static final Logger logger = LoggerFactory.getLogger(BaseLoginService.class);

    protected UserInfoProducer userInfoProducer;

    /**
     * Hole den Benutzer aus dem System.
     */
    protected abstract UserInfo getUserInfo(String userId);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userInfoProducer = BeanProvider.getContextualReference(UserInfoProducer.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("Error! No Request!");
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String loggedInUserId = httpServletRequest.getRemoteUser();

        if (loggedInUserId == null || loggedInUserId.isEmpty()) {
            // redirect to login site and set backUrl cookie
            if (LoginConstants.LOGIN_REDIRECT_URL != null) {
                redirectToLogin(httpServletRequest, httpServletResponse);
                return;
            }
            else {
                throw new IllegalArgumentException("Unerlaubter Zugriff!");
            }
        }

        //User bereits eingeloggt
        httpServletRequest.getSession().setAttribute(LoginConstants.SESSION_ATTR_USR_ID, loggedInUserId);

        UserInfo userInfo = getUserInfo(httpServletRequest);

        try {
            userInfoProducer.setUserInfo(userInfo);
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally {
            //Wird benoetigt um aus einem Thread die UserInfos wieder zu entfernen. Nach jedem Request (moeglicherweise anderer Thread dann) wird eine
            // neue Filterung durchgefuehrt
            userInfoProducer.clearUserInfo();
        }
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    private void redirectToLogin(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        // get relative backurl - this means the login app has to run on the SAME server as the application
        // (or the http proxy has to be configured accordingly)
        String backUrl = getRequestUrl(servletRequest, false);

        String redirectTo = LoginConstants.LOGIN_REDIRECT_URL;

        if (!redirectTo.contains("backUrl")) {
            redirectTo += redirectTo.contains("?") ? "&" : "?";
            redirectTo += "backUrl=" + URLEncoder.encode(backUrl, StandardCharsets.UTF_8.name());
        }

        logger.info("redirecting to login url " + redirectTo);
        servletResponse.sendRedirect(redirectTo);
    }

    private String getRequestUrl(HttpServletRequest servletRequest, boolean includeServerName) {
        StringBuffer url;
        if (includeServerName) {
            url = servletRequest.getRequestURL();
        }
        else {
            url = new StringBuffer(servletRequest.getRequestURI());
        }
        String queryString = servletRequest.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            url.append('?').append(queryString);
        }

        return url.toString();
    }


    protected UserInfo getUserInfo(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(true);
        UserInfo userInfo = (UserInfo) session.getAttribute(LoginConstants.SESSION_ATTR_USR);

        String username = (String) session.getAttribute(LoginConstants.SESSION_ATTR_USR_ID);
        if (userInfo == null || userInfo.getId() == null || !userInfo.getUsername().equals(username)) {

            if (username != null) {
                userInfo = getUserInfo(username);

                if (userInfo == null) {
                    throw new RuntimeException("Technische Anmeldung von User " + username + " fehlgeschlagem!");
                }

                // speichere den aktuellen IDMBenutzer auch in der Session.
                session.setAttribute(LoginConstants.SESSION_ATTR_USR, userInfo);
                logger.info("userLogin userId={}", userInfo.getId());
            }
        }
        return userInfo;
    }
}
