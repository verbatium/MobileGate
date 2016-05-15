package ee.valja7.gate.commands;

import ee.valja7.gate.SerialModem;

import java.util.regex.Pattern;

/**
 * Created by valeri on 14.01.15.
 */
public class McPlusCommand extends ModemCommand {
    public McPlusCommand(SerialModem modem, String command) {
        super(modem);
        this.filer = command;
        pattern = Pattern.compile(String.format("\\%s.*", command));

        this.command = "AT" + this.filer;
    }
}
