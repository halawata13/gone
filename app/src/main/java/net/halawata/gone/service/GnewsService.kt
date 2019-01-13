package net.halawata.gone.service

import net.halawata.gone.entity.GnewsArticle
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URL
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object GnewsService {

    private const val dateFormat = "EEE, dd MMM yyyy HH:mm:ss zzz"

    fun parse(content: String): ArrayList<GnewsArticle>? {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(content))

        var eventType = parser.eventType

        val articles = ArrayList<GnewsArticle>()
        var id: Long = 0

        try {
            while (eventType != XmlPullParser.END_DOCUMENT) {
                var title = ""
                var url = ""
                var host = ""
                var thumbsUrlString: String? = null
                var guid = ""
                var pubDate = ""

                // item タグ開始が来るまでスキップ
                if (eventType != XmlPullParser.START_TAG || parser.name != "item") {
                    eventType = parser.next()
                    continue
                }

                while (eventType != XmlPullParser.END_TAG || parser.name != "item") {
                    if (eventType == XmlPullParser.START_TAG && parser.name == "title") {
                        while (eventType != XmlPullParser.END_TAG || parser.name != "title") {
                            if (eventType == XmlPullParser.TEXT) {
                                title = parser.text
                            }

                            eventType = parser.next()
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG && parser.name == "link") {
                        while (eventType != XmlPullParser.END_TAG || parser.name != "link") {
                            if (eventType == XmlPullParser.TEXT) {
                                url = parser.text
                                host = URL(url).host
                            }

                            eventType = parser.next()
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG && parser.name == "guid") {
                        while (eventType != XmlPullParser.END_TAG || parser.name != "guid") {
                            if (eventType == XmlPullParser.TEXT) {
                                guid = parser.text
                            }

                            eventType = parser.next()
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG && parser.name == "pubDate") {
                        while (eventType != XmlPullParser.END_TAG || parser.name != "pubDate") {
                            if (eventType == XmlPullParser.TEXT) {
                                pubDate = formatDate(parser.text) ?: ""
                            }

                            eventType = parser.next()
                        }
                    }

                    if (eventType == XmlPullParser.START_TAG && parser.name == "media:content") {
                        for (i in 0 until parser.attributeCount) {
                            if (parser.getAttributeName(i) == "url") {
                                thumbsUrlString = parser.getAttributeValue(i)
                                break
                            }
                        }
                    }

                    eventType = parser.next()
                }

                val article = GnewsArticle(
                        id = id++,
                        title = title,
                        url = url,
                        host = host,
                        thumbsUrlString = thumbsUrlString,
                        pubDate = pubDate,
                        guid = guid
                )

                articles.add(article)

                eventType = parser.next()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        articles.sortByDescending { article -> article.pubDate }

        return articles
    }

    private fun formatDate(dateString: String): String? {
        return try {
            val inFormat = SimpleDateFormat(dateFormat, Locale.US)
            val outFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)

            outFormat.format(inFormat.parse(dateString))

        } catch (ex: ParseException) {
            ex.printStackTrace()
            null
        }
    }
}
