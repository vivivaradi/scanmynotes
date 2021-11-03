package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.graphics.Bitmap
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.data.models.Result
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NoteDetailsViewState>(Initial) {

    var currentNote: Note? = null

    object NoTextFoundEvent: OneShotEvent
    class TextReady(val text: String): OneShotEvent

    class NoteNotFoundEvent(val noteId: String): OneShotEvent
    object NoteDeletedEvent: OneShotEvent

    fun loadCurrentNote(id: String) = execute {
        viewState = Loading
        val result = interactor.getSingleNote(id)
        viewState = when (result) {
            is Result.Success<Note> -> {
                currentNote = result.data
                Viewing(result.data)
            }
            is Result.Failure<Note> -> Error(result.message)
        }
    }

    fun editNote() {
        currentNote?.let {
            viewState = Editing(it)
        }
    }

    fun saveNote(title: String, content: String) = execute {
        val updatedNote = Note(currentNote!!.id, title, content)
        viewState = Loading
        interactor.saveNote(updatedNote)
        currentNote = updatedNote
        viewState = Viewing(currentNote!!)
    }

    fun deleteNote() = execute {
 //       viewState = Loading
 //       interactor.deleteNote(currentNote!!.id)
 //       postEvent(NoteDeletedEvent)
    }

    fun digitalizePhoto(image: Bitmap) = execute {
 //       viewState = Loading
 //       val text = interactor.digitalize(image)
 //       if (text != null)
 //           postEvent(TextReady(text))
 //       else
 //           postEvent(NoTextFoundEvent)
 //       viewState = Editing(DomainNote(currentNote!!.id, currentNote!!.title, currentNote!!.content + text))
    }

}