package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import hu.bme.aut.android.scanmynotes.domain.models.Note
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class FirebaseApi {

    private val db = Firebase.firestore
    val auth = Firebase.auth

    suspend fun fetchNotes(): Result<List<Note>> {
        Log.d("DEBUG", "FirebaseApi reached")

        val notesRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes")
        val notesSnapshot = try {
            notesRef.get().await()
        } catch (error: FirebaseFirestoreException) {
            return Result.failure(error.message.toString())
        }

        val notes = notesSnapshot.map { document ->
            val note = document.toObject<Note>()
            note.id = document.id
            note
        }

        return Result.success(notes)
    }

    suspend fun fetchCategories(): Result<List<Category>> {
        val categoriesRef = db.collection("users").document(auth.currentUser!!.uid).collection("categories")
        val categoriesSnapshot = try {
            categoriesRef.get().await()
        } catch (error: FirebaseFirestoreException) {
            return Result.failure(error.message.toString())
        }

        val categories = categoriesSnapshot.map { document ->
            val category = document.toObject<Category>()
            category.id = document.id
            category
        }

        return Result.success(categories)
    }

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