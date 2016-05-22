package ee.valja7.gate.modem;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class PhoneEventTest {

    @Test
    public void TestStringToDateAndBack() {
        String s = "2015-03-23T10:36:15.045+02:00";
        Date d = PhoneEvent.ParseDate(s);
        String s2 = PhoneEvent.DateToString(d);
        Assert.assertEquals(s, s2);
    }

    @Test
    public void TestGetLogString() {
        String s = "2015-03-23T10:36:15.045+02:00";
        Date d = PhoneEvent.ParseDate(s);
        PhoneEvent e = new PhoneEvent(d, "Valera", "37256", true);
        String ls = e.getLogString();
        Assert.assertEquals("2015-03-23T10:36:15.045+02:00,Valera,37256,true,", ls);
    }

    @Test
    public void TestGetLogStringWithEndDate() {
        String s = "2015-03-23T10:36:15.045+02:00";
        Date d = PhoneEvent.ParseDate(s);
        PhoneEvent e = new PhoneEvent(d, "Valera", "37256", true);
        e.callEnded = d;
        String ls = e.getLogString();
        Assert.assertEquals("2015-03-23T10:36:15.045+02:00,Valera,37256,true,2015-03-23T10:36:15.045+02:00", ls);
    }

    @Test
    public void TestParseLogString() {
        String s2parse = "2015-03-23T10:36:15.045+02:00,Valera,37256,false,";
        PhoneEvent e = PhoneEvent.ParseLog(s2parse);
        Assert.assertEquals(false, e.getEnabled());
        Assert.assertEquals(s2parse, e.getLogString());
    }

    @Test
    public void TestParseLogStringWithEndDate() {
        String s2parse = "2015-03-23T10:36:15.045+02:00,Valera,37256,true,2015-03-23T10:36:15.045+02:00";
        PhoneEvent e = PhoneEvent.ParseLog(s2parse);
        Assert.assertEquals(s2parse, e.getLogString());
    }

}