package ee.valja7.gate.ui;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by valeri on 16.01.15.
 */
public abstract class RequestHandler {
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public static boolean isNullOrEmpty(String param) {
        return param == null || param.trim().length() == 0;
    }

    protected abstract void doExecute() throws Exception;

    void setLanguage() {
        Cookie cookie = getCookie("language");
        if (cookie == null) {
            cookie = new Cookie("language", "et");
            response.addCookie(cookie);
        }
        String requestedLanguage = cookie.getValue();

        if (!Labels.getAllowedLanguages().contains(requestedLanguage)) {
            requestedLanguage = "et";
        }
        Labels.setLanguage(requestedLanguage);
    }

    protected Cookie getCookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    protected Integer getInteger(String name) throws NumberFormatException {
        String value = get(name);
        return isNullOrEmpty(value) ? null : Integer.valueOf(value);
    }

    protected String get(String name) {
        return request.getParameter(name);
    }
}
