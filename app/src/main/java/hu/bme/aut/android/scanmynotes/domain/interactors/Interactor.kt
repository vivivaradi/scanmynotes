package hu.bme.aut.android.scanmynotes.domain.interactors

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import co.zsmb.rainbowcake.withIOContext
import com.google.api.services.vision.v1.model.Image
import hu.bme.aut.android.scanmynotes.data.network.NetworkDataSource
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class Interactor @Inject constructor(
    private val networkDataSource: NetworkDataSource
){
    val noteList = MediatorLiveData<List<DomainNote>>()

    fun setupDataFlow() {
        noteList.addSource(networkDataSource.getNoteList()) { list ->
            noteList.postValue(list)
        }
    }

    fun stopDataFlow() {
        noteList.removeSource(networkDataSource.getNoteList())
    }

    fun getNoteList(): LiveData<List<DomainNote>> {
        return noteList
    }

    suspend fun fetchNotes() = withIOContext {
        Log.d("DEBUG", "Interactor reached")
        networkDataSource.fetchNotes()
    }

    suspend fun digitalize(image: Bitmap): String = withIOContext {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val inputImage = Image()
        inputImage.encodeContent(byteArray)
        networkDataSource.detectText(inputImage)
    }

    suspend fun createNote(title: String, text: String) = withIOContext{
        networkDataSource.createUserNote(DomainNote("", title, text))
    }

    suspend fun saveNote(note: DomainNote) = withIOContext {
        networkDataSource.saveNote(note)
    }

    fun getSingleNote(id: String): DomainNote? {

        Log.d("DEBUG", "Getting single note with id: $id")
        Log.d("DEBUG", "Notelist size: ${noteList.value!!.size}")
        return noteList.value!!.find { note ->
            Log.d("DEBUG", "Applying predicate to list, id: ${note.id}")
            note.id == id
        }
//        networkDataSource.getSingleNote(id)
    }

    suspend fun deleteNote(id: String) = withIOContext {
        networkDataSource.deleteNote(id)
    }

    suspend fun observeNotes() : LiveData<List<DomainNote>> = withIOContext {
        networkDataSource.observeNotes()
    }
}