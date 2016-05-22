package ee.valja7.gate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ee.valja7.gate.modem.PhoneBookService;
import ee.valja7.gate.modem.PhoneEventListener;
import ee.valja7.gate.modem.SchedulerService;
import ee.valja7.gate.modem.SerialModem;
import ee.valja7.gate.modem.commands.SimLock;
import ee.valja7.gate.persistence.Categories;
import ee.valja7.gate.persistence.PreferenceEntity;
import ee.valja7.gate.persistence.PreferenceTypes;
import ee.valja7.gate.persistence.PreferencesService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.Session;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Launcher {
    private static final Logger LOG = Logger.getLogger(Launcher.class);
    public static Injector injector;
    protected static String portName;
    protected static String httpPort;
    protected static SerialModem modem;

    @Inject
    PhoneBookService phoneBookService;
    @Inject
    PhoneEventListener eventListener;
    @Inject
    SchedulerService schedulerService;
    @Inject
    PreferencesService preferencesService;
    private Map<String, PreferenceEntity> preferences;

    public Launcher() {
    }

    public static void main(String[] args) {
        org.eclipse.jetty.util.log.Log.setLog(null);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);

        Launcher launcher = new Launcher();
        DevelopmentGuiceModule guiceModule = new DevelopmentGuiceModule();

        injector = Guice.createInjector(guiceModule);
        injector.injectMembers(launcher);

        Logger.getRootLogger().setLevel(Level.INFO);

        launcher.run();
    }

    private static WebAppContext createHandler() {
        WebAppContext webAppContext = new WebAppContext("webapp", "/");
        webAppContext.setMaxFormContentSize(20971520);
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "true");
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*\\.jar");
        return webAppContext;
    }

    public SchedulerService getSchedulerService() {
        return schedulerService;
    }

    private void run() {
        loadPreferences();
        CompletableFuture.runAsync(this::init_modem)
                .thenRun(this::setSchedulerFunction);
        Server server = new Server(Integer.valueOf(httpPort));
        server.setHandler(createHandler());

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void setSchedulerFunction() {
        schedulerService.openGate(this::openGate);
    }

    private void openGate() {
        modem.writeString("+CLIP: \"+372000000\",145,,,\"@Manual Open\",0");
    }


    private void loadPreferences() {
        HibernateContext.openSession();
        List<PreferenceEntity> prefList = preferencesService.getByCategory(Categories.Modem);
        preferences = prefList.stream()
                .collect(Collectors.toMap(PreferenceEntity::getName,
                        Function.identity()));
        portName = get("port", "/dev/ttyUSB2");
        httpPort = get("httpPort", "8080");
        HibernateContext.closeSession();
    }

    public SerialModem getModem() {
        return modem;
    }

    private void init_modem() {

        modem = new SerialModem(portName);
        if (!modem.isReady()) {
            LOG.error("Modem is not ready");
            return;
        }

        LOG.info("Modem ready");
        modem.SetTerminalError(1);
        if (!this.enterPin("0000")) {
            LOG.error("Wrong Pin");
            return;
        }

        LOG.info("pin ok");
        this.readPhoneBook();
        LOG.info("PhoneBook read");
        this.enableClip();
        save();
    }

    private void enableClip() {
        this.eventListener.initModem(modem);
        modem.clip(this.eventListener);
    }

    private void readPhoneBook() {
        this.phoneBookService.read(modem);
    }

    private boolean enterPin(String pin) {
        SimLock sl = modem.pin();
        LOG.info("Lock:" + sl.toString());
        return sl == SimLock.READY || sl == SimLock.SIM_PIN && modem.unlock(pin);
    }

    private String get(String name, String defaultValue) {
        PreferenceEntity preferenceEntity = getPreferenceEntity(name, defaultValue);
        return preferenceEntity.getValue();
    }

    private PreferenceEntity getPreferenceEntity(String name, String defaultValue) {
        PreferenceEntity preferenceEntity;
        if (preferences.containsKey(name)) {
            preferenceEntity = preferences.get(name);
        } else {
            preferenceEntity = new PreferenceEntity(Categories.Modem, name, PreferenceTypes.Long, defaultValue);
            preferences.put(name, preferenceEntity);
        }
        return preferenceEntity;
    }

    public void save() {
        Session session = HibernateContext.openSession();
        put("port", portName);
        preferences.values().stream().forEach(o -> session.saveOrUpdate(o));
        session.flush();

        HibernateContext.closeSession();
    }

    private void put(String name, String value) {
        PreferenceEntity preferenceEntity = getPreferenceEntity(name, value);
        preferenceEntity.setValue(value);
    }

    public String getPortName() {
        return portName;
    }
}