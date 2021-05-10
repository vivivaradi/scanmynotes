package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NoteDetailsViewState>(Initial) {

    var currentNote: DomainNote? = null

    val noteList = MediatorLiveData<List<DomainNote>>()

    class NoteNotFoundEvent(val noteId: String): OneShotEvent

    fun setupDataFlow() = execute {
        Log.d("DEBUG", "Run you bitch")
        noteList.addSource(interactor.noteList) { list ->
            noteList.value = list
            Log.d("DEBUG", "Observing interactor noteList")
        }
        Log.d("DEBUG", "Source added (maybe)")
    }

    fun loadCurrentNote(id: String) = execute {
        viewState = Loading

//        currentNote = noteList.value!!.find { note ->
//            Log.d("DEBUG", "Applying predicate to list, id: $id")
//            note.id == id
//        }
        currentNote = interactor.getSingleNote(id)
        Log.d("DEBUG", "Retrieved note with title ${currentNote!!.title}")
        currentNote?.let { note ->
            viewState = Viewing(note)
        }
        if (currentNote == null)
            postEvent(NoteNotFoundEvent(id))
    }

    fun editNote() {
        currentNote?.let {
            viewState = Editing(it)
        }
    }

    fun saveNote(title: String, content: String) = execute {
        val updatedNote = DomainNote(currentNote!!.id, title, content)
        viewState = Loading
        interactor.saveNote(updatedNote)
        currentNote = updatedNote
        viewState = Viewing(currentNote!!)
    }

    fun deleteNote() = execute {
        viewState = Loading
        interactor.deleteNote(currentNote!!.id)
        viewState = NoteDeleted
    }

}