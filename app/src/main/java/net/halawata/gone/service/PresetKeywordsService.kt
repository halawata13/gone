package net.halawata.gone.service

import net.halawata.gone.entity.KeywordItem
import net.halawata.gone.entity.KeywordType

class PresetKeywordsService {

    fun getAll(): ArrayList<KeywordItem> {
        val items = arrayListOf<KeywordItem>()

        Keyword.values().forEach { item ->
            items.add(KeywordItem(title = item.displayName, type = KeywordType.PRESET))
        }

        return items
    }

    companion object {
        fun getUrlString(displayName: String): String {
            return when (displayName) {
                Keyword.NATION.displayName        -> "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRE5mTTJRU0FtcGhLQUFQAQ?hl=ja&gl=JP&ceid=JP:ja"
                Keyword.WORLD.displayName         -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGx1YlY4U0FtcGhHZ0pLVUNnQVAB?hl=ja&gl=JP&ceid=JP:ja"
                Keyword.BUSINESS.displayName      -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGx6TVdZU0FtcGhHZ0pLVUNnQVAB?hl=ja&gl=JP&ceid=JP:ja"
                Keyword.POLITICS.displayName      -> "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNRFZ4ZERBU0FtcGhLQUFQAQ?hl=ja&gl=JP&ceid=JP:ja"
                Keyword.ENTERTAINMENT.displayName -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNREpxYW5RU0FtcGhHZ0pLVUNnQVAB?hl=ja&gl=JP&ceid=JP:ja"
                Keyword.SPORTS.displayName        -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtcGhHZ0pLVUNnQVAB?hl=ja&gl=JP&ceid=JP:ja"
                Keyword.SCITECH.displayName       -> "https://news.google.com/rss/topics/CAAqKAgKIiJDQkFTRXdvSkwyMHZNR1ptZHpWbUVnSnFZUm9DU2xBb0FBUAE?hl=ja&gl=JP&ceid=JP:ja"
                else -> throw Exception("Unknown display name.")
            }
        }
    }

    enum class Keyword(val displayName: String) {
        NATION("国内"),
        WORLD("国際"),
        BUSINESS("ビジネス"),
        POLITICS("政治"),
        ENTERTAINMENT("エンタメ"),
        SPORTS("スポーツ"),
        SCITECH("テクノロジー"),
    }
}
