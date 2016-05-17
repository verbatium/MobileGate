package ee.valja7.gate.ui.admin;

import ee.valja7.gate.ui.Action;

import javax.servlet.http.HttpSession;

public class Logout extends Action {
    @Override
    public String execute() {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "home";
    }
}
