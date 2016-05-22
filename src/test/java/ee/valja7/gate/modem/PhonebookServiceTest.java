package ee.valja7.gate.modem;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Hashtable;

public class PhonebookServiceTest {

    @org.junit.Test
    public void testParseParameters() throws Exception {
        PhoneBookService pb = new PhoneBookService();
        pb.parseParameters("+CPBR: (1-500),24,24\r\n");
        Assert.assertEquals(1, pb.minIdx);
        Assert.assertEquals(500, pb.maxIdx);
        Assert.assertEquals(24, pb.maxNameLength);
        Assert.assertEquals(24, pb.maxPhoneLength);
    }

    @Test
    public void testParseEntries() throws Exception {
        PhoneBookService pb = new PhoneBookService();
        pb.entries = new Hashtable<>();
        pb.parsePhoneBookEntries("+CPBR: 1,\"+37253407716\",145,\"@Valeri Kuzmin\"\r\n" +
                "+CPBR: 10,\"+3725247000\",145,\"Kliendiinfo\"\r\n");
        Assert.assertEquals(pb.entries.get(1).getPhone(), "+37253407716");
        Assert.assertEquals(pb.entries.get(10).getEnabled(), false);
        Assert.assertEquals(pb.entries.get(1).getEnabled(), true);
        Assert.assertEquals(pb.entries.get(1).GetCPBWString(false), "=1,\"+37253407716\",145,\"@Valeri Kuzmin\"\r\n");
        Assert.assertEquals(pb.entries.get(10).GetCPBWString(false), "=10,\"+3725247000\",145,\"Kliendiinfo\"\r\n");

        PhoneBookEntry pbe = new PhoneBookEntry(0, "+37200000", 145, null, "Disabled");
        Assert.assertEquals(pbe.GetCPBWString(false), "=,\"+37200000\",145,\"Disabled\"\r\n");
        pbe = new PhoneBookEntry(0, "+37200001", 145, "@", "Enabled");
        Assert.assertEquals(pbe.GetCPBWString(false), "=,\"+37200001\",145,\"@Enabled\"\r\n");
    }

    @Test
    public void testGetFirstFreeIntegerEven() throws Exception {
        Integer[] arr = {0, 1, 2};
        int retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals("All positions full", 3, retVal);

        arr = new Integer[]{0, 1, 3};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals("second Element", 2, retVal);

        arr = new Integer[]{0, 2, 3};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals("second Element", 1, retVal);

        arr = new Integer[]{1, 2, 3};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals("second Element", 0, retVal);
    }

    @Test
    public void testGetFirstFreeIntegerODD() throws Exception {
        Integer[] arr = {0, 1, 2, 3};
        int retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals(4, retVal);

        arr = new Integer[]{0, 1, 2, 4};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals(3, retVal);

        arr = new Integer[]{0, 1, 3, 4};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals(2, retVal);

        arr = new Integer[]{0, 2, 3, 4};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals(1, retVal);

        arr = new Integer[]{1, 2, 3, 4};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals(0, retVal);
    }

    @Test
    public void testGetFirstFreeIntegerUnsorted() throws Exception {
        Integer[] arr = {4, 1, 2, 0};
        int retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr));
        Assert.assertEquals(3, retVal);

    }

    @Test
    public void testGetFirstFreeIntegerStartedFromOne() throws Exception {
        Integer[] arr = {1, 2, 3, 4};
        int retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr), 1);
        Assert.assertEquals(5, retVal);

        arr = new Integer[]{1, 2, 4, 5};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr), 1);
        Assert.assertEquals(3, retVal);

        arr = new Integer[]{1, 3, 4, 5};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr), 1);
        Assert.assertEquals(2, retVal);

        arr = new Integer[]{2, 3, 4, 5};
        retVal = PhoneBookService.getFirstFreeInteger(Arrays.asList(arr), 1);
        Assert.assertEquals(1, retVal);
    }


}