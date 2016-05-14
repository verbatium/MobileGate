package ee.valja7.gate.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by valeri on 14.05.16.
 */
public class CmeErrorTest {
    @Test
    public void getValue() throws Exception {
        assertEquals(CmeError.DialStringTooLong, CmeError.GetByCode(CmeError.DialStringTooLong.getCode()));
    }

}