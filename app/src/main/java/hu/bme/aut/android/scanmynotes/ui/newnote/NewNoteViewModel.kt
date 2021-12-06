package hu.bme.aut.android.scanmynotes.ui.newnote

import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.Note
import javax.inject.Inject

class NewNoteViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NewNoteViewState>(Loading) {

    class NewNoteSavedEvent(val id: String): OneShotEvent
    class NoteSaveEventError(val message: String): OneShotEvent

    private var selectedParent: Category? = null

    fun loadCategories() = execute {
        viewState = Loading
        val result = interactor.getCategories()
        viewState = when (result) {
            is Result.Success -> CategoriesLoaded(result.data)
            is Result.Failure -> Error(result.message)
        }
    }

    fun saveNote(title: String, text: String) = execute {
        viewState = Loading
        val result = interactor.saveNote(Note("", title, selectedParent?.id, text))
        when (result) {
            is Result.Success -> postEvent(NewNoteSavedEvent(result.data))
            is Result.Failure -> postEvent(NoteSaveEventError(result.message))
        }
    }

    fun selectParent(category: Category?) {
        selectedParent = category
    }
}