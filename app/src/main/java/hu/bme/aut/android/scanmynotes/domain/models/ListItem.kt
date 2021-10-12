package hu.bme.aut.android.scanmynotes.domain.models

import hu.bme.aut.android.scanmynotes.util.Behaviour

open class ListItem(
    var id: String = "",
    val title: String = "",
    var parentId: String? = null,
    val behaviour: Behaviour = Behaviour.UNDEFINED
    )