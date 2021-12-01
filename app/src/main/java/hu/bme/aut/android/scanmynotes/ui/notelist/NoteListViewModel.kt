package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Note
import java.util.*
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
        private val interactor: Interactor
) : RainbowCakeViewModel<NoteListViewState>(Initial) {

    class NewNoteReadyEvent(val text: String): OneShotEvent
    object NoTextFoundEvent: OneShotEvent

    lateinit var complexList: List<ListItem>
    lateinit var noteList: MutableList<Note>

    fun getAuth() = interactor.getAuth()

    // TODO: get category list
    fun load(selectedNavItem: SelectedNavItem) = execute {
        viewState = Loading
        val complexResult = interactor.getComplexList()
        viewState = when (complexResult) {
            is Result.Success -> {
                val notesResult = interactor.getNotes()
                when(notesResult) {
                    is Result.Success -> {
                        complexList = complexResult.data
                        noteList = notesResult.data.toMutableList()
                        when(selectedNavItem) {
                            SelectedNavItem.CATEGORIES -> ListLoaded(complexList)
                            SelectedNavItem.NOTES -> ListLoaded(noteList)
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
            note.title.contains(filterText, ignoreCase = true)
        }
    }

    fun sortList(sortOptions: SortOptions): List<Note> {
        return when (sortOptions) {
            SortOptions.ALPHA_ASC -> {
                noteList.sortBy { note ->
                    note.title
                }
                noteList
            }
            SortOptions.ALPHA_DESC -> {
                noteList.sortByDescending { note ->
                    note.title
                }
                noteList
            }
        }
    }
}