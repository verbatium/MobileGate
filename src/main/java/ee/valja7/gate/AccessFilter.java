package ee.valja7.gate;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AccessFilter implements Filter {

    public static final String PRINCIPAL = "ee.valja7.gate.principal";
    private static final Logger LOG = Logger.getLogger(AccessFilter.class);
    private static final String HOME = "/admin/home";
    private static final String LOGIN = "/admin/login";
    private static final String LOGOUT = "/admin/logout";
    private static final Set<String> PUBLIC_URLS = ImmutableSet.of(
            HOME, LOGIN, LOGOUT
    );

    @Inject
    LoginService loginService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Launcher.injector.injectMembers(this);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        Principal principal = session != null ? (Principal) session.getAttribute("principal") : null;
        String uri = request.getRequestURI();
        if (principal == null) {
            if (PUBLIC_URLS.contains(uri)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            //principal = authenticateBasic(request);
            response.sendRedirect("/admin/home");
            return;
//            if (uri.startsWith("/admin/")) {
//
//            } else {
//
//            }
        }

        if (principal == null) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"Valja7.Gate\"");
            response.sendError(SC_UNAUTHORIZED);
            return;
        }
        MDC.put("username", "[" + principal.getUserName() + "] ");

        try {
            request.setAttribute(PRINCIPAL, principal);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove("username");
        }

    }

    private Principal authenticateBasic(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            return null;
        }

        String decodedAuthorization = decode(authorization.substring(6));
        String[] usernameAndPassword = decodedAuthorization.split(":");
        if (usernameAndPassword.length != 2) {
            LOG.warn("Invalid Authorization: " + authorization + " / " + decodedAuthorization);
            return null;
        }
//        UserEntity user = new UserEntity();
//        user.setUserName("Admin");
//        return user;
        return loginService.loginWithPassword(usernameAndPassword[0], usernameAndPassword[1]);
    }

    private String decode(String authorization) {
        try {
            return new String(Base64.decodeBase64(authorization.getBytes()), "utf8");
        } catch (Exception e) {
            LOG.warn("base64 decode failed", e);
            return "";
        }
    }

    @Override
    public void destroy() {

    }
}
