package hu.bme.aut.android.scanmynotes.ui.newnote

import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import javax.inject.Inject

class NewNoteViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NewNoteViewState>(Initial) {

    class NewNoteSavedEvent(val id: String): OneShotEvent
    class NoteSaveEventError(val message: String): OneShotEvent

    fun loadCategories() = execute {
        viewState = Loading
        val result = interactor.getCategories()
        viewState = when (result) {
            is Result.Success -> Success(result.data)
            is Result.Failure -> Failure(result.message)
        }
    }

    fun saveNote(title: String, text: String, parent: Category?) = execute {
        viewState = Loading
        val result = interactor.createNote(title, text, parent?.id)
        when (result) {
            is Result.Success -> postEvent(NewNoteSavedEvent(result.data))
            is Result.Failure -> postEvent(NoteSaveEventError(result.message))
        }
    }
}