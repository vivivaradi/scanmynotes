package hu.bme.aut.android.scanmynotes.ui.notelist

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.zsmb.rainbowcake.withIOContext
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.Feature
import com.google.api.services.vision.v1.model.Image
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import hu.bme.aut.android.scanmynotes.ui.notelist.models.UiNotePreview
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class NoteListPresenter @Inject constructor(
        private val interactor: Interactor
){

    suspend fun getNotes(): List<UiNotePreview> = withIOContext {
        val notes = interactor.getNotes()
        notes.map { it.toUiNotePreview() }
    }

    suspend fun digitalize(image: Bitmap): String = withIOContext {
        interactor.digitalize(image)
    }
}

private fun DomainNote.toUiNotePreview(): UiNotePreview {
    return UiNotePreview(
            noteId = id,
            noteTitle = title
    )
}