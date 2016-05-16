package ee.valja7.gate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ee.valja7.gate.commands.SimLock;
import jssc.SerialPortList;
import org.apache.log4j.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.Session;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

public class Launcher {
    private static final Logger LOG = Logger.getLogger(Launcher.class);
    public static Injector injector;
    protected static String portName;
    final Session session = HibernateContext.openSession();
    @Inject
    PhoneBookService phoneBookService;
    @Inject
    PhoneEventListener eventListener;
    @Inject
    SchedulerService schedulerService;
    private SerialModem modem;

    public Launcher() {
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        try {
            RollingFileAppender fileAppender = new RollingFileAppender(new PatternLayout("%d{dd-MM-yyyy HH:mm:ss} %C %L %-5p:%m%n"), "file.log");
            LOG.addAppender(fileAppender);
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        LOG.info("TEST LOG ENTRY");
        if (args.length > 1) {
            portName = args[1];
        } else {
            String[] portNames = SerialPortList.getPortNames();
            if (portNames.length > 0) {
                System.out.println("Use command CONNECT {portName}.\n Available ports:\n");
                Arrays.stream(portNames).forEach(System.out::println);
            }
        }

        Launcher launcher = new Launcher();
        DevelopmentGuiceModule guiceModule = new DevelopmentGuiceModule();
        injector = Guice.createInjector(guiceModule);
        injector.injectMembers(launcher);
        launcher.run();
    }

    private static WebAppContext createHandler() {
        WebAppContext webAppContext = new WebAppContext("webapp", "/");
        webAppContext.setMaxFormContentSize(20971520);
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "true");
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*\\.jar");
        return webAppContext;
    }

    private void run() {
        this.init_modem();
        Runnable task = () -> Launcher.this.modem.writeString("+CLIP: \"+372000000\",145,,,\"@Manual Open\",0");
        this.schedulerService.openGate(task);
        Server server = new Server(8080);
        server.setHandler(createHandler());

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void init_modem() {
        this.modem = new SerialModem("/dev/ttyUSB2");
        if (!this.modem.isReady()) {
            System.exit(-1);
        }

        LOG.info("Modem ready");
        this.modem.SetTerminalError(1);
        if (!this.enterPin("0000")) {
            System.exit(-2);
        }

        LOG.info("pin ok");
        this.readPhoneBook();
        LOG.info("PhoneBook read");
        this.enableClip();
    }

    private void enableClip() {
        this.eventListener.initModem(this.modem);
        this.modem.clip(this.eventListener);
    }

    private void readPhoneBook() {
        this.phoneBookService.read(this.modem);
    }

    private boolean enterPin(String pin) {
        SimLock sl = this.modem.pin();
        LOG.info("Lock:" + sl.toString());
        return sl == SimLock.READY || sl == SimLock.SIM_PIN && this.modem.unlock(pin);
    }
}