package ee.valja7.gate;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AccessFilter implements Filter {

    public static final String PRINCIPAL = "ee.valja7.gate.principal";
    private static final Logger LOG = Logger.getLogger(AccessFilter.class);
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
//            if (PUBLIC_URLS.contains(uri)) {
//                chain.doFilter(servletRequest, servletResponse);
//                return;
//            }
            if (uri.startsWith("/consumer")) {
                response.sendRedirect("/admin/Dashboard");
                return;
            } else {
                principal = authenticateBasic(request);
            }
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
