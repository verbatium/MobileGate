package ee.valja7.gate.modem.commands;

import ee.valja7.gate.modem.SerialModem;
import org.apache.log4j.Logger;

public class McATCPIN extends McPlusCommand {
    private static final Logger LOG = Logger.getLogger(McATCPIN.class);

    public McATCPIN(SerialModem modem) {
        super(modem, "+CPIN");
    }

    public boolean unlock(String pin) {
        run("=" + pin);
        return status == CommandState.OK;
    }

    public boolean unlock(String puk, String newPin) {
        run("=" + puk + "," + newPin);
        return status == CommandState.OK;
    }

    public SimLock SimStatus() {
        String response = run("?");
        switch (response) {
            case "+CPIN: READY\r\n":
                return SimLock.READY;
            case "+CPIN: SIM PIN\r\n":
                return SimLock.SIM_PIN;
            case "+CPIN: SIM PIN2\r\n":
                return SimLock.SIM_PIN2;
            case "+CPIN: SIM PUK\r\n":
                return SimLock.SIM_PUK;
            case "+CPIN: SIM PUK2\r\n":
                return SimLock.SIM_PUK2;
        }
        LOG.info("unknown response:" + response);
        return SimLock.UNKNOWN;
    }
}
