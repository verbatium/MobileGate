package ee.valja7.gate.modem.commands;

import ee.valja7.gate.modem.SerialModem;

public class McAT extends ModemCommand {
    public McAT(SerialModem modem) {
        super(modem);
        command = "AT";
    }
}
