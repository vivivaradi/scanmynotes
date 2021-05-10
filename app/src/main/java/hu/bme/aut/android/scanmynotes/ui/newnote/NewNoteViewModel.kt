package hu.bme.aut.android.scanmynotes.ui.newnote

import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import javax.inject.Inject

class NewNoteViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NewNoteViewState>(Initial) {

    class NewNoteSavedEvent(val id: String): OneShotEvent

    fun saveNote(title: String, text: String) = execute {
        val newId = interactor.createNote(title, text)
        postEvent(NewNoteSavedEvent(newId))
    }
}