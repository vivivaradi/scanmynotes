package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.graphics.Bitmap
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NoteDetailsViewState>(Initial) {

    lateinit var currentNote: Note
    var selectedParent: Category? = null
    lateinit var categoriesList: List<Category>

    object NoTextFoundEvent: OneShotEvent
    class TextReady(val text: String): OneShotEvent

    class NoteNotFoundEvent(val noteId: String): OneShotEvent
    object NoteDeletedEvent: OneShotEvent

    fun loadData(noteId: String) = execute {
        viewState = Loading
        val noteResult = interactor.getSingleNote(noteId)
        val categoriesResult = interactor.getCategories()
        viewState = when {
            noteResult is Result.Success && categoriesResult is Result.Success -> {
                currentNote = noteResult.data
                categoriesList = categoriesResult.data
                if (currentNote.parentId != null) {
                    selectedParent = categoriesList.find { elem ->
                        elem.id == currentNote.parentId
                    }
                }
                Viewing(currentNote)
            }
            noteResult is Result.Failure -> Failure(noteResult.message)
            categoriesResult is Result.Failure -> Failure(categoriesResult.message)
            else -> Failure(R.string.failure_unknown_error_text.toString())
        }
    }


    fun editNote() {
        viewState = Editing(currentNote)
    }

    fun saveNote(title: String, content: String) = execute {
        viewState = Loading
        val updatedNote = Note(currentNote.id, title, selectedParent?.id, content)
        interactor.saveNote(updatedNote)
        currentNote = updatedNote
        viewState = Viewing(currentNote)
    }

    fun deleteNote() = execute {
        viewState = Loading
        interactor.deleteNote(currentNote.id)
        postEvent(NoteDeletedEvent)
    }

    fun digitalizePhoto(image: Bitmap) = execute {
        viewState = Loading
        val text = interactor.digitalize(image)
        if (text != null)
            postEvent(TextReady(text))
        else
            postEvent(NoTextFoundEvent)
        viewState = Editing(Note(currentNote.id, currentNote.title, currentNote.content + text))
    }

    fun selectParent(category: Category?) {
        selectedParent = category
    }
}