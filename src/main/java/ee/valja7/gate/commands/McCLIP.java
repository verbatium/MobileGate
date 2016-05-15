package ee.valja7.gate.commands;

import ee.valja7.gate.PhoneEventListener;
import ee.valja7.gate.SerialModem;

import java.util.regex.Pattern;

/**
 * Created by valeri on 7.03.15.
 */
public class McCLIP extends McPlusCommand {
    private final PhoneEventListener listener;

    public McCLIP(SerialModem modem, PhoneEventListener listener) {
        super(modem, "+CLIP");
        pattern = Pattern.compile("(\\+CLIP).*|(\\^CEND).*");
        this.listener = listener;
    }

    public void enable() {
        run("=1");
        if (status == CommandState.OK)
            modem.clipCommand = this;
        else
            modem.clipCommand = null;
    }

    public void disable() {
        run("=0");
        modem.clipCommand = null;
    }

    @Override
    protected void incomingCommand(String message) {
        listener.event(message);
    }
}
