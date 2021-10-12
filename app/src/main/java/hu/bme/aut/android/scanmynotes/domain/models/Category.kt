package hu.bme.aut.android.scanmynotes.domain.models

import hu.bme.aut.android.scanmynotes.util.Behaviour

class Category(id: String = "",
               title: String = "",
               parentId: String? = null,
               behaviour: Behaviour = Behaviour.DROPDOWN,
               var listItems: ArrayList<ListItem> = ArrayList<ListItem>()
): ListItem(id, title, parentId, behaviour)