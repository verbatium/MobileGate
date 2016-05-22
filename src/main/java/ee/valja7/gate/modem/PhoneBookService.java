package ee.valja7.gate.modem;

import ee.valja7.gate.modem.commands.CmeError;
import ee.valja7.gate.modem.commands.CommandState;
import ee.valja7.gate.modem.commands.McPlusCommand;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class PhoneBookService {
    public Dictionary<Integer, PhoneBookEntry> entries;
    int minIdx;
    int maxIdx;
    int maxPhoneLength;
    int maxNameLength;
    private SerialModem modem;
    private boolean UCS2;


    PhoneBookService() {
    }

    static int getFirstFreeInteger(List<Integer> list, int firstElement) {
        Collections.sort(list);
        int j = firstElement;

        int k = list.size() + firstElement;
        int i = k - 1;

        while (j < k) {
            if (i + 1 > list.get(i - firstElement))
                j = i + 1;
            else
                k = i;
            i = (k - j) / 2 + j;
        }
        return k;
    }

    static int getFirstFreeInteger(List<Integer> arr) {
        return getFirstFreeInteger(arr, 0);
    }

    public void read(SerialModem modem) {
        UCS2 = true;
        if (modem != null)
            this.modem = modem;
        McPlusCommand sm = new McPlusCommand(this.modem, "+CPBS");
        sm.run("=\"SM\"");
        while (sm.status == CommandState.CME_ERROR && sm.cmeError == CmeError.SIMBusy) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sm.run("=\"SM\"");
        }

        sm.run("?");

        McPlusCommand mc = new McPlusCommand(this.modem, "+CPBR");
        String data = mc.run("=?"); //+CPBR: (1-500),24,24


        parseParameters(data);
        data = mc.run("=" + minIdx + "," + maxIdx);
        entries = new Hashtable<>();
        parsePhoneBookEntries(data);
    }

    void parseParameters(String data) {
        String rx = "\\+CPBR: \\((?<minIdx>\\d)-(?<maxIdx>\\d+)\\),(?<maxPhoneLength>\\d+),(?<maxNameLength>\\d+)";
        Pattern p = Pattern.compile(rx);
        Matcher m = p.matcher(data);

        boolean b = m.find();
        if (b) {
            minIdx = Integer.parseInt(m.group("minIdx"));
            maxIdx = Integer.parseInt(m.group("maxIdx"));
            maxPhoneLength = Integer.parseInt(m.group("maxPhoneLength"));
            maxNameLength = Integer.parseInt(m.group("maxNameLength"));
        }
    }

    void parsePhoneBookEntries(String data) {

        String rx = "\\+CPBR: (?<id>\\d+),\"(?<phone>\\+?\\d+)\",(?<type>\\d+),\"(?<enabled>@?)(?<name>\\+?[^\"]*)\"";
        Pattern p = Pattern.compile(rx, Pattern.MULTILINE);
        Matcher m = p.matcher(data);
        while (m.find()) {
            int id = Integer.parseInt(m.group("id"));
            String phone = m.group("phone");
            int type = Integer.parseInt(m.group("type"));
            String name;
            if (UCS2)
                name = m.group("name");
            else
                name = m.group("name");

            String enabled = m.group("enabled");
            PhoneBookEntry pbe = new PhoneBookEntry(id, phone, type, enabled, name);
            entries.put(id, pbe);

        }
    }

    public void write(PhoneBookEntry entry) {
        McPlusCommand mc = new McPlusCommand(this.modem, "+CPBW");
        String param = entry.GetCPBWString(false);
        mc.run(param);
        if ("".equals(entry.getPhone()))
            entries.remove(entry.getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        while (entries.elements().hasMoreElements()) {
            sb.append(entries.elements().nextElement().toString()).append('\r');
        }
        return sb.toString();
    }

    public int AddNewUser(String Phone, String Name, boolean Active) {
        List<Integer> list = Collections.list(entries.keys());
        int id = getFirstFreeInteger(list, 1);
        PhoneBookEntry pbEntry = new PhoneBookEntry(id, Phone, 145, Active ? "@" : "", Name);
        write(pbEntry);
        entries.put(id, pbEntry);
        return id;
    }
}

