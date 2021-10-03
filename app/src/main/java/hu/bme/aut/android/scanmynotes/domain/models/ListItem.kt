package hu.bme.aut.android.scanmynotes.domain.models

import hu.bme.aut.android.scanmynotes.util.Behaviour

open class ListItem(val id: String = "",
                    val title: String = "",
                    val behaviour: Behaviour = Behaviour.UNDEFINED,
                    val parent: ListItem? = null)