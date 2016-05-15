package ee.valja7.gate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PhoneEvent {
    private static String rx = "\\+CLIP: \"(?<phone>\\+?\\d+)\",(?<type>\\d+),,,(\"(?<enabled>@?)(?<name>\\+?[^\"]*)\")?,\\d?";
    private static Pattern pattern = Pattern.compile(rx);
    private final Date callStarted;
    private final String callerName;
    private final String phone;
    private final Boolean enabled;
    Date callEnded;

    PhoneEvent(Date date, String name, String phone, Boolean enabled) {

        this(date, name, phone, enabled, null);
    }

    private PhoneEvent(Date date, String name, String phone, Boolean enabled, Date callEnded) {
        this.callStarted = date;
        this.callerName = name;
        this.enabled = enabled;
        this.phone = phone;
        this.callEnded = callEnded;
    }

    static PhoneEvent Parse(String message) {
        Matcher m = pattern.matcher(message);
        if (m.find()) {
            String phone = m.group("phone");
            //int type = Integer.parseInt(m.group("type"));
            String name = m.group("name");
            String enabled = m.group("enabled");
            if (name == null)
                name = "Unknown Number";
            if (enabled == null)
                enabled = "";
            return new PhoneEvent(new Date(), name, phone, "@".equals(enabled));
        }
        throw new IllegalArgumentException("Cannot parse message: " + message);
    }

    static PhoneEvent ParseLog(String line) {
        String[] s = line.split(",");
        if (s.length == 4)
            return new PhoneEvent(ParseDate(s[0]), s[1], s[2], "true".equals(s[3]));
        if (s.length == 5)
            return new PhoneEvent(ParseDate(s[0]), s[1], s[2], "true".equals(s[3]), ParseDate(s[4]));
        return null;
    }

    static Date ParseDate(String d) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            return format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String DateToString(Date d) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        if (d != null)
            return format.format(d);
        return "";
    }

    public Date getCallStarted() {
        return callStarted;
    }

    public String getCallEnded() {
        if (callEnded != null)
            return callEnded.toString();
        return "";
    }

    public String getCallerName() {
        return callerName;
    }

    public String getPhone() {
        return phone;
    }

    public boolean getEnabled() {
        return enabled;
    }

    String getLogString() {
        return String.format("%1$s,%2$s,%3$s,%4$b,%5$s", DateToString(callStarted), callerName, phone, enabled, DateToString(callEnded));
    }


}
