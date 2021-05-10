package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
        private val interactor: Interactor
) : RainbowCakeViewModel<NoteListViewState>(Initial) {

    val noteList = MediatorLiveData<List<DomainNote>>()

    class NewNoteReadyEvent(val text: String): OneShotEvent

    fun setupDataFlow() {
        interactor.setupDataFlow()
        noteList.addSource(interactor.getNoteList()) { list ->
            noteList.value = list
        }
    }

    fun stopDataFlow() {
        noteList.removeSource(interactor.getNoteList())
        interactor.stopDataFlow()
    }

    fun load() = execute {
        viewState = Loading
        Log.d("DEBUG", "Calling interactor for notes")
        interactor.fetchNotes()
        Log.d("DEBUG", "Received notes")
        viewState = NotesReady
    }

    fun digitalizePhoto(image: Bitmap) = execute {
        viewState = Loading
        postEvent(NewNoteReadyEvent(interactor.digitalize(image)))
    }

}