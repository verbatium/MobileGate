package ee.valja7.gate.modem;

import ee.valja7.gate.modem.commands.*;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SerialModem {
    private static final Logger LOG = Logger.getLogger(SerialModem.class);
    public ModemCommand lastCommand;
    public ModemCommand clipCommand;
    private SerialPort serialPort;
    private String message = "";
    private boolean modemReady;
    public SerialModem(String port) {
        serialPort = new SerialPort(port);
        try {
            LOG.info("port open :" + serialPort.openPort());//Open port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener

            writeString("ATZ");
            ModemCommand mc = new McAT(this);
            LOG.debug(mc.run());
            modemReady = mc.status == CommandState.OK;
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    private static CompletableFuture writeBytesToPort(SerialPort serialPort, byte[] b) {
        return CompletableFuture.runAsync(() -> {
            try {
                serialPort.writeBytes(b);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        });
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    private void processMessage(String message) {
        if (lastCommand != null)
            lastCommand.response(message);
        if (clipCommand != null)
            clipCommand.response(message);
    }

    private void closePort() {
        if (serialPort != null)
            try {

                serialPort.removeEventListener();
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
    }

    public void writeString(String str) {
        String encoding = CharsetDetector.detect(str);
        LOG.debug("[" + encoding + "]: > " + str);
        byte[] b = (str + "\r\n").getBytes(Charset.forName(encoding));
        try {
            writeBytesToPort(serialPort, b).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void finalize() throws Throwable {
        closePort();
        super.finalize();
    }

    public SimLock pin() {
        McATCPIN mc = new McATCPIN(this);
        return mc.SimStatus();
    }

    public boolean unlock(String pin) {
        McATCPIN mc = new McATCPIN(this);
        return mc.unlock(pin);
    }

    public boolean isReady() {
        return this.modemReady;
    }

    public void SetTerminalError(int i) {
        McPlusCommand mc = new McPlusCommand(this, "+CMEE");
        mc.run("=" + i); //+CPBR: (1-500),24,24
    }

    public void clip(PhoneEventListener listener) {
        McCLIP clipCmd = new McCLIP(this, listener);
        clipCmd.enable();
    }

    private class SerialPortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {//If data is available
                try {
                    byte buffer[] = serialPort.readBytes();
                    String s;
                    String encoding;
                    int start = 0;
                    for (int i = 0; i < buffer.length; i++) {
                        byte b = buffer[i];
                        if ((b == '\r' || b == '\n')) {
                            if (start < i - 1) {
                                encoding = CharsetDetector.detect(buffer, start, i - start);
                                s = message + new String(buffer, start, i - start, encoding);
                                LOG.debug("[" + encoding + "]: < " + s.replace("\"", "\"\""));
                                processMessage(s);
                                message = "";
                            }
                            start = i + 1;
                        }
                    }
                    if (start < buffer.length) {

                        encoding = CharsetDetector.detect(buffer, start, buffer.length - start);
                        s = new String(buffer, start, buffer.length - start, encoding);
                        message = s;
                    }

                } catch (SerialPortException ex) {
                    LOG.error(ex + "serialEvent");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (event.isCTS()) {
                //If CTS line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    LOG.debug("CTS - ON");
                } else {
                    LOG.debug("CTS - OFF");
                }
            } else if (event.isDSR()) {
                ///If DSR line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    LOG.debug("DSR - ON");
                } else {
                    LOG.debug("DSR - OFF");
                }
            }
        }
    }

}
