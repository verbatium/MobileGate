package ee.valja7.gate.ui.admin;

import ee.valja7.gate.modem.PhoneBookEntry;
import ee.valja7.gate.modem.PhoneBookService;
import ee.valja7.gate.ui.View;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Phonebook extends View {
    private static final Logger LOG = Logger.getLogger(Phonebook.class);

    @Inject
    PhoneBookService phoneBookService;

    @Override
    public void execute() throws IOException {
        LOG.info("PhoneBook request");
        Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            LOG.info(paramName + "=" + "'" + get(paramName) + "'");
        }
        List<PhoneBookEntry> phoneBookEntries = Collections.list(phoneBookService.entries.elements());
        put("phonebooklenght", phoneBookService.entries.size());
        put("pbentries", phoneBookEntries);
    }
}
