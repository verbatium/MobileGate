package ee.valja7.gate.ui.admin;

import ee.valja7.gate.ui.View;

import java.util.Date;

public class Dashboard extends View {
    @Override
    public void execute() {
        put("now", new Date());
    }
}
