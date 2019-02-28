package cz.slevomat.dominika.htmldisplayer;

import org.junit.Test;

import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.TextManager;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTab() {
        String result = TextManager.INSTANCE.adjustText("\n\n\n\n\t\t\t\n\t\t\t\t\t", "hkh\t");
        assertEquals("", result);
    }
}