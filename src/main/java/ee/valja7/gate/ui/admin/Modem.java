package ee.valja7.gate.ui.admin;

import ee.valja7.gate.Launcher;
import ee.valja7.gate.modem.SchedulerService;
import ee.valja7.gate.modem.SerialModem;
import ee.valja7.gate.ui.View;
import jssc.SerialPort;
import jssc.SerialPortList;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Modem extends View {

    @Inject
    Launcher launcher;
    @Override
    public void execute() throws IOException {
        String[] portNames = SerialPortList.getPortNames();
        put("ports", Arrays.asList(portNames));
        String portName = launcher.getPortName();
        put("portName", portName);
        SerialModem modem = launcher.getModem();
        SerialPort serialPort = modem.getSerialPort();
        put("portIsOpened", serialPort.isOpened());
        SchedulerService schedulerService = launcher.getSchedulerService();
        Date openTime = schedulerService.getOpenTime();
        put("openTime", openTime);
    }
}
