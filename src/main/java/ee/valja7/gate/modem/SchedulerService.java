package ee.valja7.gate.modem;

import ee.valja7.gate.HibernateContext;
import ee.valja7.gate.persistence.Categories;
import ee.valja7.gate.persistence.PreferenceEntity;
import ee.valja7.gate.persistence.PreferenceTypes;
import ee.valja7.gate.persistence.PreferencesService;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class SchedulerService {
    private static final Logger LOG = Logger.getLogger(SchedulerService.class);
    final PreferencesService prefs;
    private final ScheduledExecutorService mainScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> openHandle;
    private ScheduledFuture<?> closeHandle;


    private Date openTime;
    private Date closeTime;
    private Long openedTime;
    private Long interval;
    private Long repeatInterval;
    private Map<String, PreferenceEntity> preferenceces;

    @Inject
    public SchedulerService(PreferencesService prefs) {
        Session session = HibernateContext.openSession();
        //every 2 weeks starting from 2015-03-24T06:00 for 4 hours Call gate open routine evry 55 sec;
        List<PreferenceEntity> prefList = prefs.getByCategory(Categories.Scheduler);
        preferenceces = prefList.stream()
                .collect(Collectors.toMap(PreferenceEntity::getName,
                        Function.identity()));

        this.interval = getLong("interval", TimeUnit.DAYS.toMillis(14));
        openTime = PhoneEvent.ParseDate(get("NextOpenTime", "2015-03-24T05:00:00.000+02:00"));
        this.openedTime = getLong("openedTime", TimeUnit.HOURS.toMillis(4));
        repeatInterval = getLong("repeat", TimeUnit.SECONDS.toMillis(55));
        closeTime = new Date(openTime.getTime() + this.openedTime);
        this.prefs = prefs;
        HibernateContext.closeSession();
        nextTime();
        save();
    }

    private Long getLong(String name, Long defaultValue) {
        return Long.valueOf(get(name, defaultValue.toString()));
    }

    private String get(String name, String defaultValue) {
        PreferenceEntity preferenceEntity = getPreferenceEntity(name, defaultValue);
        return preferenceEntity.getValue();
    }

    private PreferenceEntity getPreferenceEntity(String name, String defaultValue) {
        PreferenceEntity preferenceEntity;
        if (preferenceces.containsKey(name)) {
            preferenceEntity = preferenceces.get(name);
        } else {
            preferenceEntity = new PreferenceEntity(Categories.Scheduler, name, PreferenceTypes.Long, defaultValue);
            preferenceces.put(name, preferenceEntity);
        }
        return preferenceEntity;
    }

    public void cancel() {
        if (openHandle != null)
            openHandle.cancel(true);
        if (closeHandle != null)
            closeHandle.cancel(true);
        mainScheduler.shutdown();
    }

    public void save() {
        Session session = HibernateContext.openSession();
        put("NextOpenTime", PhoneEvent.DateToString(openTime));
        putLong("interval", interval);
        putLong("openedTime", openedTime);
        putLong("repeat", repeatInterval);

        preferenceces.values().stream().forEach(o -> session.saveOrUpdate(o));
        session.flush();

        HibernateContext.closeSession();
    }

    private void putLong(String name, Long value) {
        put(name, value.toString());
    }

    private void put(String name, String value) {
        PreferenceEntity preferenceEntity = getPreferenceEntity(name, value);
        preferenceEntity.setValue(value);
    }

    public void setOpenTime(Date d) {
        openTime = d;
    }

    private void nextTime() {
        long d = openTime.getTime();
        long now = System.currentTimeMillis();
        while ((d + openedTime) < now) {
            d += interval;
        }
        openTime = new Date(d);
        closeTime = new Date(openTime.getTime() + openedTime);
    }

    public void openGate(Runnable job) {
        final Runnable task = job != null ? job : () -> LOG.info("Open: " + PhoneEvent.DateToString(new Date()));
        nextTime();
        LOG.info("Next open Time:" + PhoneEvent.DateToString(openTime));
        LOG.info("Next close Time:" + PhoneEvent.DateToString(closeTime));
        long initialDelay = openTime.getTime() - System.currentTimeMillis(); //start at this date
        LOG.info("Start after: " + initialDelay / 1000 + " seconds");
        long closeAfter = closeTime.getTime() - System.currentTimeMillis();
        LOG.info("finish after: " + closeAfter / 1000 + " seconds");
        save();
        openHandle = mainScheduler.scheduleAtFixedRate(task, initialDelay, repeatInterval, TimeUnit.MILLISECONDS);
        closeHandle = mainScheduler.schedule(() -> {
            LOG.info("Closing: " + PhoneEvent.DateToString(new Date()));
            openHandle.cancel(true);
            openGate(task);
        }, closeAfter, TimeUnit.MILLISECONDS);
    }
}
