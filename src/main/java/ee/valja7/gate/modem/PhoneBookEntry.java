package ee.valja7.gate.modem;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;

public class PhoneBookEntry {
    private boolean enabled;
    private int type;
    private Integer id;
    private String phone;
    private String name;

    PhoneBookEntry(int id, String phone, int type, String enabled, String name) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.type = type;
        this.enabled = "@".equals(enabled);
    }

    private static String StringToUCSC(String value) {
        byte[] data = value.getBytes(Charset.forName("UTF16"));
        return new String(Hex.encodeHex(data)).toUpperCase();
    }

    public Integer getId() {
        return id;
    }


    public String getPhone() {
        return phone;
    }

    private void setPhone(String value) {
        phone = value;
    }

    public String getName() {
        return name;
    }

    void setName(String value) {
        name = value;
    }

    public boolean getEnabled() {
        return enabled;
    }

    private void setEnabled(boolean value) {
        enabled = value;
    }

    @Override
    public String toString() {
        return "PhoneBookEntry{" +
                "enabled=" + enabled +
                ", type=" + type +
                ", id=" + id +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    String GetCPBWString(boolean UCS2) {
        if ("".equals(phone))
            return String.format("=%s\r\n", id == 0 ? "" : id.toString());
        return String.format("=%s,\"%s\",145,\"%s%s\"\r\n", id == 0 ? "" : id.toString(), phone, enabled ? "@" : "", UCS2 ? "80" + StringToUCSC(name) : name);

    }

    public void setValue(String name, String value) {
        switch (name) {
            case "name":
                setName(value);
                break;
            case "phone":
                setPhone(value);
                break;
            case "enabled":
                setEnabled(value.equals("true"));
                break;
        }
    }
}
