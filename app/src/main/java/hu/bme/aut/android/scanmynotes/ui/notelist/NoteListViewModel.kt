package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import android.util.Log
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Note
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
        private val interactor: Interactor
) : RainbowCakeViewModel<NoteListViewState>(Initial) {

    class NewNoteReadyEvent(val text: String): OneShotEvent
    object NoTextFoundEvent: OneShotEvent

    lateinit var complexList: List<ListItem>
    lateinit var noteList: List<Note>

    fun getAuth() = interactor.getAuth()

    fun load(selectedNavItem: SelectedNavItem) = execute {
        viewState = Loading
        Log.d("DEBUG", "Calling interactor for notes")
        val complexResult = interactor.getComplexList()
        Log.d("DEBUG", "Received notes")
        viewState = when (complexResult) {
            is Result.Success -> {
                val notesResult = interactor.getNotes()
                when(notesResult) {
                    is Result.Success -> {
                        complexList = complexResult.data
                        noteList = notesResult.data
                        when(selectedNavItem) {
                            SelectedNavItem.CATEGORIES -> Success(complexList)
                            SelectedNavItem.NOTES -> Success(noteList)
                        }
                    }
                    is Result.Failure -> Error(notesResult.message)
                }
            }
            is Result.Failure -> Error(complexResult.message)
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

    fun filterList(filterText: String): List<Note> {
        return noteList.filter { note ->
            note.title.contains(filterText)
        }
    }
}