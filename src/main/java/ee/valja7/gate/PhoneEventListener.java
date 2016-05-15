package ee.valja7.gate;

import org.apache.log4j.Logger;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class PhoneEventListener {

    private static final Logger LOG = Logger.getLogger(PhoneEventListener.class);
    public List<PhoneEvent> eventList = new ArrayList<>();
    private SerialModem modem;
    private PhoneEvent lastEvent;
    private String lastString = "";

    public PhoneEventListener() {
        readLog();

    }

    private static String getLogFileName() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM");
        Date today = new Date();
        return dateFormatter.format(today) + ".log";
    }

    private void readLog() {
        Path lastMonthLog = Paths.get(getLogFileName());
        if (Files.exists(lastMonthLog)) {
            List<String> s;
            try {
                s = Files.readAllLines(lastMonthLog, Charset.defaultCharset());
                for (String line : s) {
                    PhoneEvent e = PhoneEvent.ParseLog(line);
                    eventList.add(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void WriteLog(String msg) {
        Path lastMonthLog = Paths.get(getLogFileName());
        try {
            if (Files.exists(lastMonthLog))
                Files.write(lastMonthLog, msg.getBytes(), StandardOpenOption.APPEND);
            else
                Files.write(lastMonthLog, msg.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    void initModem(SerialModem modem) {
        this.modem = modem;
    }

    public void event(String message) {

        if (!lastString.equals(message) && message.startsWith("+CLIP")) {
            try {
                PhoneEvent e = PhoneEvent.Parse(message);
                eventList.add(e);
                lastEvent = e;
                LOG.info("event: < CALL STARTED");
                WriteLog(e.getLogString());
            } catch (IllegalArgumentException ex) {
                LOG.error("event not parsed: " + ex.getMessage(), ex);
            }


        }
        if (message.startsWith("^CEND")) {
            if (lastEvent != null) {
                lastEvent.callEnded = new Date();
                WriteLog(PhoneEvent.DateToString(lastEvent.callEnded) + "\n");
            }
            LOG.info("event: < CALL ENDED");
        }
        lastString = message;
    }
}
