package cz.slevomat.dominika.htmldisplayer

import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.DataType
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.DisplayHtml
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [28])
@RunWith(RobolectricTestRunner::class)
class Tests {

    @Test
    fun testDangerousCharsBeforeYoutubeTag() {
        val youtubeId = "VUSmECpGcjk"
        val youtubeId2 = "wLzAj4_C7a8"
        val html =
            """
                | <p>Text A
                |   <strong>Strong B</strong>
                |   [image id="11015290" href="https://www.slevomat.cz/magazin/1238"]
                |   [youtube id="$youtubeId"]
                | </p>
                | <p>
                |   <strong>Strong C</strong>
                | </p>
                | <p>
                |   <b>Bold D</b> Text E
                | </p>
                | <p>[[ ]]"] [youtube id="$youtubeId2"] ]]"]</p>
            | """.trimMargin()

        val displayHtml = DisplayHtml()

        val htmlString = displayHtml.htmlRecursion(
            displayHtml.parseHtml(html).body().childNodes(), DataType.UNKNOWN, displayHtml, ""
        )

        assert(htmlString.isBlank())

        assertEquals(6, displayHtml.dataItems.size)

        val firstTextItem = displayHtml.dataItems.first()
        assertEquals(DataType.TEXT, firstTextItem.dataType)
        assertEquals(1, firstTextItem.liLevel)

        val secondYoutubeItem = displayHtml.dataItems[1]
        assertEquals(DataType.YOUTUBE, secondYoutubeItem.dataType)
        assertEquals(youtubeId, secondYoutubeItem.url)
        assertEquals(1, secondYoutubeItem.liLevel)

        val thirdTextItem = displayHtml.dataItems[2]
        assertEquals(DataType.TEXT, thirdTextItem.dataType)
        assertEquals("   Strong C", thirdTextItem.textToDisplay?.toString())
        assertEquals(1, thirdTextItem.liLevel)

        val sixthYoutubeItem = displayHtml.dataItems.last()
        assertEquals(DataType.YOUTUBE, sixthYoutubeItem.dataType)
        assertEquals(youtubeId2, sixthYoutubeItem.url)
        assertEquals(1, sixthYoutubeItem.liLevel)
    }

}