package hu.bme.aut.android.scanmynotes.ui.notedetails

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NoteDetailsViewState>(Initial) {

    lateinit var currentNote: DomainNote

    fun loadCurrentNote(id: String) = execute {
        viewState = Loading
        currentNote = interactor.getSingleNote(id)
        viewState = Viewing(currentNote)
    }

    fun editNote() {
        viewState = Editing(currentNote)
    }

    fun saveNote(title: String, content: String) = execute {
        val updatedNote = DomainNote(currentNote.id, title, content)
        viewState = Loading
        interactor.saveNote(updatedNote)
        currentNote = updatedNote
        viewState = Viewing(currentNote)
    }

    fun deleteNote() = execute {
        viewState = Loading
        interactor.deleteNote(currentNote.id)
        viewState = NoteDeleted
    }

}