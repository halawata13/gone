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
                Keyword.NATION.displayName        -> "https://news.google.com/news/rss/headlines/section/topic/NATION.ja_jp/%E5%9B%BD%E5%86%85?ned=jp&hl=ja&gl=JP"
                Keyword.WORLD.displayName         -> "https://news.google.com/news/rss/headlines/section/topic/WORLD.ja_jp/%E5%9B%BD%E9%9A%9B?ned=jp&hl=ja&gl=JP"
                Keyword.BUSINESS.displayName      -> "https://news.google.com/news/rss/headlines/section/topic/BUSINESS.ja_jp/%E3%83%93%E3%82%B8%E3%83%8D%E3%82%B9?ned=jp&hl=ja&gl=JP"
                Keyword.POLITICS.displayName      -> "https://news.google.com/news/rss/headlines/section/topic/POLITICS.ja_jp/%E6%94%BF%E6%B2%BB?ned=jp&hl=ja&gl=JP"
                Keyword.ENTERTAINMENT.displayName -> "https://news.google.com/news/rss/headlines/section/topic/ENTERTAINMENT.ja_jp/%E3%82%A8%E3%83%B3%E3%82%BF%E3%83%A1?ned=jp&hl=ja&gl=JP"
                Keyword.SPORTS.displayName        -> "https://news.google.com/news/rss/headlines/section/topic/SPORTS.ja_jp/%E3%82%B9%E3%83%9D%E3%83%BC%E3%83%84?ned=jp&hl=ja&gl=JP"
                Keyword.SCITECH.displayName       -> "https://news.google.com/news/rss/headlines/section/topic/SCITECH.ja_jp/%E3%83%86%E3%82%AF%E3%83%8E%E3%83%AD%E3%82%B8%E3%83%BC?ned=jp&hl=ja&gl=JP"
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
