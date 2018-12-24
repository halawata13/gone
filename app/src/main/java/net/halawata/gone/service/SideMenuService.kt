package net.halawata.gone.service

import android.content.Context
import net.halawata.gone.R
import net.halawata.gone.entity.KeywordType
import net.halawata.gone.entity.SideMenuItem

class SideMenuService(private val context: Context) {

    fun getSideMenu(): SideMenu {
        val customItems = arrayListOf<SideMenuItem>()
        val presetItems = arrayListOf<SideMenuItem>()
        val helper = DatabaseHelper(context)
        val customKeywords = CustomKeywordsService(helper).getAll()
        val presetKeywords = PresetKeywordsService().getAll()
        var id = 0L

        val sections = if (customKeywords.size == 0) {
            arrayListOf(context.getString(R.string.section_title_topics))
        } else {
            arrayListOf(context.getString(R.string.section_title_keyword), context.getString(R.string.section_title_topics))
        }

        customKeywords.forEach { item ->
            customItems.add(SideMenuItem(
                    id = id,
                    keyword = item.title,
                    type = KeywordType.CUSTOM
            ))

            id++
        }

        presetKeywords.forEach { item ->
            presetItems.add(SideMenuItem(
                    id = id,
                    keyword = item.title,
                    type = KeywordType.PRESET
            ))

            id++
        }

        val items = if (customKeywords.size == 0) {
            arrayListOf(presetItems)
        } else {
            arrayListOf(customItems, presetItems)
        }

        return SideMenu(sections, items)
    }

    data class SideMenu(
            val sections: ArrayList<String>,
            val items: ArrayList<ArrayList<SideMenuItem>>
    )

    companion object {
        fun getUrlString(item: SideMenuItem): String {
            return when (item.type) {
                KeywordType.CUSTOM -> CustomKeywordsService.getUrlString(item.keyword)
                KeywordType.PRESET -> PresetKeywordsService.getUrlString(item.keyword)
            }
        }
    }
}
