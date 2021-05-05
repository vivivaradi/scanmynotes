package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
        private val interactor: Interactor
) : RainbowCakeViewModel<NoteListViewState>(Initial) {


    fun load() = execute {
        viewState = Loading
        viewState = NotesReady(interactor.getNotes())
    }

    fun digitalizePhoto(image: Bitmap) = execute {
        viewState = Loading
        viewState = NewNoteReady(interactor.digitalize(image))
    }

}