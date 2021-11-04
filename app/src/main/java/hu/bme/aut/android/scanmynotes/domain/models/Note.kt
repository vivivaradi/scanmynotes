package hu.bme.aut.android.scanmynotes.domain.models

import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.util.Behaviour

class Note(id: String = "",
           title: String = "",
           parentId: String? = null,
           val content: String = ""
): ListItem(id, title, parentId)