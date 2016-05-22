package ee.valja7.gate.ui.admin;

import com.google.inject.internal.util.ImmutableMap;
import ee.valja7.gate.modem.PhoneBookEntry;
import ee.valja7.gate.modem.PhoneBookService;
import ee.valja7.gate.ui.Action;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.Enumeration;


public class SavePhoneBookEntry extends Action {
    private static final Logger LOG = Logger.getLogger(SavePhoneBookEntry.class);

    @Inject
    private PhoneBookService phoneBookService;


    @Override
    public String execute() {
        LOG.info("Edit PhoneBook");

        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            LOG.info(paramName + "=" + "'" + get(paramName) + "'");
        }

        String pk = request.getParameter("pk");
        if (pk != null) {
            LOG.info("edit element with id :" + pk);
            String name = request.getParameter("name");
            String value = request.getParameter("value");
            int id = Integer.parseInt(pk);
            PhoneBookEntry pbe = phoneBookService.entries.get(id);
            pbe.setValue(name, value);
            phoneBookService.write(pbe);
        } else {
            LOG.info("Create New Element");
            String Phone = request.getParameter("phone");
            String Name = request.getParameter("name");
            String enabled = request.getParameter("new_enabled");
            Integer id = phoneBookService.AddNewUser(Phone, Name, enabled.equals("true"));
            json(ImmutableMap.of("id", id.toString(), "phone", Phone, "name", Name, "enabled", enabled));
        }

        success("saved");

        return null;
    }
}
