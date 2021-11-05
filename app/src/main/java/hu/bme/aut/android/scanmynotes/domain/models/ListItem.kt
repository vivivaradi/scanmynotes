package hu.bme.aut.android.scanmynotes.domain.models

open class ListItem(
    var id: String = "",
    val title: String = "",
    var parentId: String? = null
    )