package ee.valja7.gate.commands;

import ee.valja7.gate.SerialModem;
import org.apache.log4j.Logger;

import java.util.regex.Pattern;

public abstract class ModemCommand {
    private static final Logger LOG = Logger.getLogger(ModemCommand.class);
    public CommandState status = CommandState.NEW;
    public CmeError cmeError;
    String command = "AT";
    String filer = "";
    Pattern pattern;
    SerialModem modem;
    private StringBuilder response = new StringBuilder();

    ModemCommand(SerialModem modem) {
        this.modem = modem;
    }

    public String run() {
        return run("");
    }

    public synchronized String run(String param) {
        response.setLength(0);
        if (modem != null) {
            status = CommandState.WAIT;
            cmeError = null;
            this.modem.lastCommand = this;
            modem.writeString(command + param);
        }
        LOG.info("DEBUG: wait for command response");
        while (status == CommandState.WAIT) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOG.info("DEBUG: command ended: " + status.toString());
        this.modem.lastCommand = null;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public boolean isCommandExists() {
        run(command.equals("AT") ? "" : "=?");
        return "OK".equals(status.toString());
    }

    public synchronized void response(String message) {
        if (message.equals("OK")) {
            status = CommandState.OK;
            notifyAll();
        } else if (message.equals("ERROR")) {
            status = CommandState.ERROR;
            notifyAll();
        } else if (message.startsWith("+CME ERROR:")) {
            status = CommandState.CME_ERROR;
            LOG.info("ERR: '" + message + "'");
            String s = message.substring("+CME ERROR: ".length());
            //sometimes message come twice
            int i = s.indexOf('+');
            if (i >= 0)
                s = s.substring(0, i);
            LOG.info("ERR: '" + s + "'");
            int error = Integer.parseInt(s);
            CmeError c = CmeError.GetByCode(error);
            LOG.info("ERR: '" + c.getDescription() + "'");
            this.cmeError = c;
            notifyAll();
        } else if (pattern != null && pattern.matcher(message).matches()) {
            incomingCommand(message);
        }
    }

    protected void incomingCommand(String message) {
        response.append(message).append("\r\n");
    }
}
