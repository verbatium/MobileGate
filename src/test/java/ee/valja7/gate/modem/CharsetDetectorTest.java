package ee.valja7.gate.modem;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

public class CharsetDetectorTest {

    @Test
    public void testDetectRussian() throws Exception {

        byte[] b = "russйцукен".getBytes(Charset.forName("ISO-8859-5"));
        String e = CharsetDetector.detect(b);
        Assert.assertEquals("ISO-8859-5", e);
    }

    @Test
    public void testDetectEstonian() throws Exception {

        byte[] b = "estöäüõžš".getBytes(Charset.forName("ISO-8859-13"));
        String e = CharsetDetector.detect(b);
        Assert.assertEquals("ISO-8859-13", e);
    }

    @Test
    public void testDetectRussianUTF8() throws Exception {

        String s = "russйцукен";
        String e = CharsetDetector.detect(s);
        Assert.assertEquals("ISO-8859-5", e);
    }

    @Test
    public void testDetectEstonianUTF8() throws Exception {

        String s = "estöäüõžš";
        String e = CharsetDetector.detect(s);
        Assert.assertEquals("ISO-8859-13", e);
    }

    @Test
    public void testIsBaltic() throws Exception {
        Assert.assertTrue(CharsetDetector.isBaltic("ö"));
        Assert.assertTrue(CharsetDetector.isBaltic("ž"));
        Assert.assertTrue(CharsetDetector.isBaltic("š"));
        Assert.assertFalse(CharsetDetector.isBaltic("a"));
        Assert.assertFalse(CharsetDetector.isBaltic("й"));
    }

    @Test
    public void testIsCyrillic() throws Exception {
        Assert.assertFalse(CharsetDetector.isCyrillic("ö"));
        Assert.assertFalse(CharsetDetector.isCyrillic("ž"));
        Assert.assertFalse(CharsetDetector.isCyrillic("š"));
        Assert.assertFalse(CharsetDetector.isCyrillic("a"));
        Assert.assertTrue(CharsetDetector.isCyrillic("й"));
    }

    @Test
    public void testByteIsCyrillic() throws Exception {
        Assert.assertFalse(CharsetDetector.isCyrillic("ö".getBytes(Charset.forName("ISO-8859-5"))[0]));
        Assert.assertFalse(CharsetDetector.isCyrillic("ž".getBytes(Charset.forName("ISO-8859-5"))[0]));
        Assert.assertFalse(CharsetDetector.isCyrillic("š".getBytes(Charset.forName("ISO-8859-5"))[0]));
        Assert.assertFalse(CharsetDetector.isCyrillic("a".getBytes(Charset.forName("ISO-8859-5"))[0]));
        Assert.assertTrue(CharsetDetector.isCyrillic("й".getBytes(Charset.forName("ISO-8859-5"))[0]));
    }
}