package ee.valja7.gate.ui.admin;

import ee.valja7.gate.persistence.PreferenceEntity;
import ee.valja7.gate.persistence.PreferencesService;
import ee.valja7.gate.ui.View;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class Settings extends View {

    @Inject
    PreferencesService preferencesService;

    @Override
    public void execute() throws IOException {
        List<PreferenceEntity> settings = preferencesService.getAll();
        put("entries", settings);
    }
}
