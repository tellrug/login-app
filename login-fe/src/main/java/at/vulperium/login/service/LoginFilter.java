package at.vulperium.login.service;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieser {@link Filter} ueberprueft ob die aktuell aufgerufene URL
 * geschuetzt werden muss oder ob sie auf der liste der {@link LoginFilter#dropUrls}
 * steht.
 * <p/>
 * Falls sie einer DropUrl entspricht, wird der Filter uebersprungen,
 * ansonsten wird an das {@link LoginService} deligiert.
 * <p/>
 * Die DropUrls werden in der web.xml Datei als Init-Param hinterlegt.
 * <p/>
 * Beispiel:
 * <code>
 * <filter>
 * <filter-name>LoginFilter</filter-name>
 * <filter-class>at.sozvers.zepta.core.felib.idm.LoginFilter</filter-class>
 * <init-param>
 * <param-name>dropurl.0</param-name>
 * <param-value>/VAADIN/</param-value>
 * </init-param>
 * <init-param>
 * <param-name>dropurl.1</param-name>
 * <param-value>/clusterTest</param-value>
 * </init-param>
 * </filter>
 * </code>
 */
public class LoginFilter implements Filter {

    /**
     * Folgende URLs brauchen KEIN login
     */
    private List<String> dropUrls = null;


    private volatile FilterConfig filterConfig;

    private volatile LoginService loginService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dropUrls = new ArrayList<>();
        int i = 0;
        String dropUrlParam;
        while ((dropUrlParam = filterConfig.getInitParameter("dropurl." + i)) != null) {
            dropUrls.add(dropUrlParam);
            i++;
        }

        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String url = httpServletRequest.getRequestURI();
        if (isLoginReferer (httpServletRequest) || isDroppedUrl(url)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            getLoginService().doFilter(servletRequest, servletResponse, filterChain);
        }
    }

    @Override
    public void destroy() {
        if (loginService != null) {
            loginService.destroy();
        }
    }


    private boolean isDroppedUrl(String url) {
        // does any stopUrl match url
        for (String stopUrl : dropUrls) {
            if (url.contains(stopUrl)) {
                return true;
            }
        }

        return false;
    }

    private boolean isLoginReferer(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader("referer") != null &&
                httpServletRequest.getHeader("referer").contains("vulperiumLogin/login")) {
            //Kein Filtering auf LoginSeite
            return true;
        }
        return false;
    }

    private LoginService getLoginService() throws ServletException {
        if (loginService == null) {
            synchronized (this) {
                if (loginService == null) {
                    loginService = BeanProvider.getContextualReference(LoginService.class);

                    loginService.init(filterConfig);

                    // we do not need the filterconfig afterwards
                    // let the container GC it
                    filterConfig = null;
                }
            }
        }

        return loginService;
    }
}
