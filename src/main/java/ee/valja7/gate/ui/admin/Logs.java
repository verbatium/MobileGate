package ee.valja7.gate.ui.admin;

import ee.valja7.gate.persistence.LogEntity;
import ee.valja7.gate.persistence.LogService;
import ee.valja7.gate.ui.View;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class Logs extends View {

    @Inject
    LogService logService;

    @Override
    public void execute() throws IOException {
        List<LogEntity> entries = logService.getAll();
        put("entries", entries);
    }
}
