package net.halawata.gone

import net.halawata.gone.entity.Article

interface ListFragmentDelegate {

    fun onClickArticle(article: Article)
}
