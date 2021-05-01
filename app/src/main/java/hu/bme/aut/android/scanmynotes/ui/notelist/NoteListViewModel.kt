package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import co.zsmb.rainbowcake.withIOContext
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.Feature
import com.google.api.services.vision.v1.model.Image
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
        private val noteListPresenter: NoteListPresenter
) : RainbowCakeViewModel<NoteListViewState>(Initial) {
    fun load() = execute {
        viewState = Loading
        viewState = NotesReady(noteListPresenter.getNotes())
    }

    fun digitalizePhoto(image: Bitmap) = execute {
        viewState = Loading
        viewState = NewNoteReady(noteListPresenter.digitalize(image))
    }

}