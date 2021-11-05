package hu.bme.aut.android.scanmynotes.domain.models

class Note(id: String = "",
           title: String = "",
           parentId: String? = null,
           val content: String = ""
): ListItem(id, title, parentId)