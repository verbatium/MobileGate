package ee.valja7.gate.ui.admin;

import ee.valja7.gate.modem.PhoneEventListener;
import ee.valja7.gate.ui.View;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;

public class CallHistory extends View {
    private static final Logger LOG = Logger.getLogger(CallHistory.class);
    @Inject
    PhoneEventListener listener;

    @Override
    public void execute() throws IOException {
        LOG.info("History request");
        put("historylenght", listener.eventList.size());
        put("historyItems", listener.eventList);
        put("now", new Date());
    }
}
