package hu.bme.aut.android.scanmynotes.domain.models

import hu.bme.aut.android.scanmynotes.util.Behaviour

class Category(id: String = "",
               title: String = "",
               behaviour: Behaviour = Behaviour.DROPDOWN,
               parent: ListItem? = null,
               var listItems: List<ListItem> = ArrayList<ListItem>()
): ListItem(id, title, behaviour)