package ee.valja7.gate.commands;

import ee.valja7.gate.SerialModem;

public class McAT extends ModemCommand {
    public McAT(SerialModem modem) {
        super(modem);
        command = "AT";
    }
}
