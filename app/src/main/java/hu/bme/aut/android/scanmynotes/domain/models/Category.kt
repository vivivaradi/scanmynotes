package hu.bme.aut.android.scanmynotes.domain.models

class Category(id: String = "",
               title: String = "",
               parentId: String? = null,
               var listItems: ArrayList<ListItem> = ArrayList<ListItem>()
): ListItem(id, title, parentId) {

    override fun toString(): String {
        return title
    }
}