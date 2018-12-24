package net.halawata.gone.entity

data class GnewsArticle (

        override val id: Long,
        override val title: String,
        override val url: String,
        override val pubDate: String,
        override val guid: String,
        override var isRead: Boolean = false
): Article
