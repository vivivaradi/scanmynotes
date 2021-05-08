package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.api.services.vision.v1.model.Image
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import javax.inject.Inject

class NetworkDataSource @Inject constructor(
    private val firebaseApi: FirebaseApi,
    private val visionApi: VisionApi
){
    val currentUser = Firebase.auth.currentUser

    suspend fun fetchNotes() {
        Log.d("DEBUG", "Datasource reached")
        return firebaseApi.fetchNotes(currentUser!!.uid)
    }

    fun getNoteList(): LiveData<List<DomainNote>> {
        return firebaseApi.getNoteList()
    }

    suspend fun createUserNote(note: DomainNote){
        return firebaseApi.saveNewNote(currentUser!!.uid, note)
    }

    suspend fun detectText(image: Image): String {
        return visionApi.detectText(image)
    }

    suspend fun getSingleNote(id: String): DomainNote {
        return firebaseApi.getUserNote(id, currentUser!!.uid)
    }

    suspend fun saveNote(note: DomainNote) {
        return firebaseApi.saveNote(currentUser!!.uid, note)
    }

    suspend fun deleteNote(id: String) {
        return firebaseApi.deleteNote(currentUser!!.uid, id)
    }

    suspend fun observeNotes() : LiveData<List<DomainNote>>{
        return firebaseApi.observeNotes(currentUser!!.uid)
    }

}