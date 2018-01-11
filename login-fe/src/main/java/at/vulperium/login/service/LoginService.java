package at.vulperium.login.service;

import javax.servlet.*;
import java.io.IOException;

/**
 * The LoginService interface.
 * Technically this is 1:1 like a Servlet Filter.
 */
public interface LoginService {

    void init(FilterConfig filterConfig) throws ServletException;

    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException;

    //void resetLoginAttributes(WrappedSession wrappedSession);

    void destroy();
}
