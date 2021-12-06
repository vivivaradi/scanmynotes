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
): RainbowCakeViewModel<NoteDetailsViewState>(Loading) {

    lateinit var currentNote: Note
    var selectedParent: Category? = null
    lateinit var categoriesList: List<Category>

    object NoTextFoundEvent: OneShotEvent
    class TextReady(val text: String): OneShotEvent

    class ErrorEvent(val message: String): OneShotEvent
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
            noteResult is Result.Failure -> Error(noteResult.message)
            categoriesResult is Result.Failure -> Error(categoriesResult.message)
            else -> Error(R.string.failure_unknown_error_text.toString())
        }
    }


    fun editNote() {
        viewState = Editing(currentNote)
    }

    fun saveNote(title: String, content: String) = execute {
        viewState = Loading
        val updatedNote = Note(currentNote.id, title, selectedParent?.id, content)
        val result = interactor.saveNote(updatedNote)
        viewState = when (result) {
            is Result.Success -> {
                currentNote = updatedNote
                Viewing(currentNote)
            }
            is Result.Failure -> Error(result.message)
        }
    }

    fun deleteNote() = execute {
        viewState = Loading
        val result = interactor.deleteNote(currentNote.id)
        when (result) {
            is Result.Success -> postEvent(NoteDeletedEvent)
            is Result.Failure -> postEvent(ErrorEvent(result.message))
        }

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

    fun getParentName(parentId: String?) : String? {
        val parent = categoriesList.find { category ->
            category.id == parentId
        }
        return parent?.title
    }
}