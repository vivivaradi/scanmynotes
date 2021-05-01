package hu.bme.aut.android.scanmynotes.domain.interactors

import android.graphics.Bitmap
import co.zsmb.rainbowcake.withIOContext
import com.google.api.services.vision.v1.model.Image
import hu.bme.aut.android.scanmynotes.data.network.NetworkDataSource
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class Interactor @Inject constructor(
    private val networkDataSource: NetworkDataSource
){
    var noteList = ArrayList<DomainNote>()

    suspend fun getNotes(): List<DomainNote> {
        noteList = networkDataSource.getUserNotes()
        return noteList
    }

    suspend fun digitalize(image: Bitmap): String {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val inputImage = Image()
        inputImage.encodeContent(byteArray)
        return networkDataSource.detectText(inputImage)
    }

    suspend fun createNote(title: String, text: String) = withIOContext{
        networkDataSource.createUserNote(DomainNote("", title, text))
    }

    suspend fun saveNote(note: DomainNote) = withIOContext {
        networkDataSource.saveNote(note)
    }

    suspend fun getSingleNote(id: String): DomainNote = withIOContext {
        networkDataSource.getSingleNote(id)
    }

    suspend fun deleteNote(id: String) = withIOContext {
        networkDataSource.deleteNote(id)
    }
}