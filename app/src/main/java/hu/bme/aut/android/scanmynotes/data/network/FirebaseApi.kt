package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import java.util.concurrent.TimeUnit

class FirebaseApi {

    private val db = Firebase.firestore
    val noteList = MutableLiveData<List<DomainNote>>()
    val auth = Firebase.auth

    suspend fun fetchNotes() {
        Log.d("DEBUG", "FirebaseApi reached")
        val notes = ArrayList<DomainNote>()
        val notesRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes")
        notesRef.get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        for (document in result) {
                            val note = document.toObject<DomainNote>()
                            note.id = document.id
                            notes.add(note)
                        }
                    }
                    noteList.postValue(notes)
                    Log.d("DEBUG", "Successful data fetch")
                }
                .addOnFailureListener { exception ->
                    exception.message?.let { Log.e("ERROR", it) }
                }
    }

    fun getNoteList(): LiveData<List<DomainNote>> = noteList

    fun getCurrentUser() = auth.currentUser

    suspend fun saveNewNote(note: DomainNote): String {
        val notesRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes")
        val data = hashMapOf(
            "title" to note.title,
            "content" to note.content
        )
        val task = notesRef.add(data)
        val docRef = Tasks.await(task, 10, TimeUnit.SECONDS)
        return docRef.id
    }

    suspend fun getUserNote(id: String) : DomainNote{
        Log.d("DEBUG", "API received id: $id")
        var note = DomainNote()
        val task = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document(id).get()
        val snapshot = Tasks.await(task, 10, TimeUnit.SECONDS)
        if (snapshot.exists()){
            snapshot.toObject<DomainNote>()?.let { result ->
                note = result
                note.id = snapshot.id
            }
        }
        return note
    }

    suspend fun saveNote(note: DomainNote) {
        val docRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document(note.id)
        val data = hashMapOf(
            "title" to note.title,
            "content" to note.content
        )
        docRef.set(data)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.e("ERROR", it) }
            }
    }

    suspend fun deleteNote(id: String) {
        val docRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document(id)
        docRef.delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener { exception ->
                    exception.message?.let { Log.e("ERROR", it) }
                }
    }

}