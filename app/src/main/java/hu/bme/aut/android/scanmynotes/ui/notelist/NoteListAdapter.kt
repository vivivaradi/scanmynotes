package hu.bme.aut.android.scanmynotes.ui.notelist

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.ui.notelist.items.CategoryItem
import hu.bme.aut.android.scanmynotes.ui.notelist.items.NoteItem

class NoteListAdapter: GroupieAdapter() {

    fun showList(list: List<ListItem>) {
        clear()
        add(populateList(list))
    }

    private fun populateList(itemList: List<ListItem>): Section {
        val mainSection = Section()
        for(item in itemList) {
            when (item) {
                is Category -> {
                    val group = ExpandableGroup(CategoryItem(item))
                    populateCategory(item.listItems, group)
                    mainSection.add(group)
                }
                is Note -> mainSection.add(NoteItem(item))
            }
        }
        return mainSection
    }

    private fun populateCategory(itemList: List<ListItem>, section: ExpandableGroup) {
        for (item in itemList) {
            when (item) {
                is Category -> {
                    val group = ExpandableGroup(CategoryItem(item))
                    populateCategory(item.listItems, group)
                    section.add(group)
                }
                is Note -> section.add(NoteItem(item))
            }
        }
    }
}