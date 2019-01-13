package net.halawata.gone.fragment

import net.halawata.gone.entity.Article

interface ListFragmentDelegate {

    fun onClickArticle(article: Article)
}
