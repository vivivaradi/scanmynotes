package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.graphics.Bitmap
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NoteDetailsViewState>(Initial) {

    var currentNote: Note? = null
    var selectedParent: Category? = null
    lateinit var categoriesList: List<Category>

    object NoTextFoundEvent: OneShotEvent
    class TextReady(val text: String): OneShotEvent

    class NoteNotFoundEvent(val noteId: String): OneShotEvent
    object NoteDeletedEvent: OneShotEvent

    // TODO: lekezelni amikor nincs parent
    fun loadData(noteId: String) = execute {
        viewState = Loading
        val noteResult = interactor.getSingleNote(noteId)
        val categoriesResult = interactor.getCategories()
        viewState = when {
            noteResult is Result.Success && categoriesResult is Result.Success -> {
                currentNote = noteResult.data
                currentNote?.let { note ->
                    categoriesList = categoriesResult.data
                    selectedParent = categoriesList.find { elem ->
                        elem.id == note.parentId
                    }
                    Viewing(note)
                }
                Failure("CurrentNote is null")
            }
            noteResult is Result.Failure -> Failure(noteResult.message)
            categoriesResult is Result.Failure -> Failure(categoriesResult.message)
            else -> Failure("Unknown error.")
        }
    }

    fun editNote() {
        currentNote?.let { note ->
            viewState = Editing(note)
        }
    }

    fun saveNote(title: String, content: String) = execute {
        val updatedNote = Note(currentNote!!.id, title, currentNote!!.parentId, content)
        viewState = Loading
        interactor.saveNote(updatedNote)
        currentNote = updatedNote
        viewState = Viewing(currentNote!!)
    }

    fun deleteNote() = execute {
        viewState = Loading
        interactor.deleteNote(currentNote!!.id)
        postEvent(NoteDeletedEvent)
    }

    fun digitalizePhoto(image: Bitmap) = execute {
        viewState = Loading
        val text = interactor.digitalize(image)
        if (text != null)
            postEvent(TextReady(text))
        else
            postEvent(NoTextFoundEvent)
        viewState = Editing(Note(currentNote!!.id, currentNote!!.title, currentNote!!.content + text))
    }

}