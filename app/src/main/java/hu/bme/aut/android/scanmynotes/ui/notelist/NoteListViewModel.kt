package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import android.util.Log
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
        private val interactor: Interactor
) : RainbowCakeViewModel<NoteListViewState>(Initial) {

    class NewNoteReadyEvent(val text: String): OneShotEvent
    object NoTextFoundEvent: OneShotEvent

    fun getUser() = interactor.getUser()

    fun getAuth() = interactor.getAuth()

    fun load() = execute {
        viewState = Loading
        Log.d("DEBUG", "Calling interactor for notes")
        val result = interactor.getNoteList()
        Log.d("DEBUG", "Received notes")
        viewState = when (result) {
            is Result.Success<List<ListItem>> -> Success(result.data)
            is Result.Failure<List<ListItem>> -> Error(result.message)
        }
    }

    fun digitalizePhoto(image: Bitmap) = execute {
        viewState = Loading
        val text = interactor.digitalize(image)
        if (text != null)
            postEvent(NewNoteReadyEvent(text))
        else
            postEvent(NoTextFoundEvent)
    }


}