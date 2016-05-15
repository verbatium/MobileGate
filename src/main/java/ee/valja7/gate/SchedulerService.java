package ee.valja7.gate;

import org.apache.log4j.Logger;

import javax.inject.Singleton;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

@Singleton
class SchedulerService {
    private static final Logger LOG = Logger.getLogger(SchedulerService.class);
    private final ScheduledExecutorService mainScheduler = Executors.newSingleThreadScheduledExecutor();
    private final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    private ScheduledFuture<?> openHandle;
    private ScheduledFuture<?> closeHandle;


    private Date openTime;
    private Date closeTime;
    private Long openedTime;
    private Long interval;
    private Long repeatInterval;

    public SchedulerService() {
        //every 2 weeks starting from 2015-03-24T06:00 for 4 hours Call gate open routine evry 55 sec;
        interval = prefs.getLong("interval", TimeUnit.DAYS.toMillis(14));
        openTime = PhoneEvent.ParseDate(prefs.get("NextOpenTime", "2015-03-24T05:00:00.000+02:00"));
        openedTime = prefs.getLong("openedTime", TimeUnit.HOURS.toMillis(4));
        repeatInterval = prefs.getLong("repeat", TimeUnit.SECONDS.toMillis(55));
        closeTime = new Date(openTime.getTime() + openedTime);
    }

    public void cancel() {
        if (openHandle != null)
            openHandle.cancel(true);
        if (closeHandle != null)
            closeHandle.cancel(true);
        mainScheduler.shutdown();
    }

    public void save() {
        prefs.put("NextOpenTime", PhoneEvent.DateToString(openTime));
        prefs.putLong("interval", interval);
        prefs.putLong("openedTime", openedTime);
        prefs.putLong("repeat", repeatInterval);
    }

    public void reset() {
        prefs.remove("NextOpenTime");
        prefs.remove("interval");
        prefs.remove("openedTime");
        prefs.remove("repeat");
    }

    public void test() {
        prefs.put("NextOpenTime", "2015-03-24T06:00:00.000+02:00");
        prefs.putLong("interval", TimeUnit.MINUTES.toMillis(10));
        prefs.putLong("openedTime", TimeUnit.MINUTES.toMillis(5));
        prefs.putLong("repeat", TimeUnit.SECONDS.toMillis(30));
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

    void openGate(Runnable job) {
        final Runnable task = job != null ? job : () -> LOG.info("Open: " + PhoneEvent.DateToString(new Date()));
        nextTime();
        LOG.info("Next open Time:" + PhoneEvent.DateToString(openTime));
        LOG.info("Next close Time:" + PhoneEvent.DateToString(closeTime));
        long initialDelay = openTime.getTime() - System.currentTimeMillis(); //start at this date
        LOG.info("Start after: " + initialDelay / 1000 + " seconds");
        long closeAfter = closeTime.getTime() - System.currentTimeMillis();
        LOG.info("finish after: " + closeAfter / 1000 + " seconds");

        openHandle = mainScheduler.scheduleAtFixedRate(task, initialDelay, repeatInterval, TimeUnit.MILLISECONDS);
        closeHandle = mainScheduler.schedule(() -> {
            LOG.info("Closing: " + PhoneEvent.DateToString(new Date()));
            openHandle.cancel(true);
            openGate(task);
        }, closeAfter, TimeUnit.MILLISECONDS);
    }
}
