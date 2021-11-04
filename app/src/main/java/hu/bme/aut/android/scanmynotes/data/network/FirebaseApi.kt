package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
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
            notesRef.orderBy("title").get().await()
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
            categoriesRef.orderBy("title").get().await()
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

    suspend fun getUserNote(id: String) : Result<Note> {
        Log.d("DEBUG", "API received id: $id")
        var note = Note()
        val noteRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document(id)
        val noteSnapshot = try {
            noteRef.get().await()
        } catch (error: FirebaseFirestoreException) {
            return Result.failure(error.message.toString())
        }
        if (noteSnapshot.exists()){
            noteSnapshot.toObject<Note>()?.let { result ->
                note = result
                note.id = noteSnapshot.id
            }
        }

        return Result.success(note)
    }

    suspend fun saveNote(note: Note): Result<String> {
        val docRef: DocumentReference

        val data = hashMapOf(
            "title" to note.title,
            "content" to note.content,
            "parentId" to note.parentId
        )
        if (note.id.isEmpty()) {
            docRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document()
            try {
                docRef.set(data).await()
            } catch (error: FirebaseFirestoreException) {
                return Result.failure(error.message.toString())
            }
        } else {
            docRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document(note.id)
            try {
                docRef.set(data).await()
            } catch (error: FirebaseFirestoreException) {
                return Result.failure(error.message.toString())
            }
        }

        return Result.success(docRef.id)
    }

    suspend fun saveCategory(category: Category): Result<String> {
        val docRef: DocumentReference

        val data = hashMapOf(
            "title" to category.title,
            "parentId" to category.parentId
        )

        if (category.id.isEmpty()) {
            docRef = db.collection("users").document(auth.currentUser!!.uid).collection("categories").document()
            try {
                docRef.set(data).await()
            } catch (error: FirebaseFirestoreException) {
                return Result.failure(error.message.toString())
            }
        } else {
            docRef = db.collection("users").document(auth.currentUser!!.uid).collection("categories").document(category.id)
            try {
                docRef.set(data).await()
            } catch (error: FirebaseFirestoreException) {
                return Result.failure(error.message.toString())
            }
        }

        return Result.success(docRef.id)
    }

    suspend fun deleteNote(id: String): Result<String> {
        val docRef = db.collection("users").document(auth.currentUser!!.uid).collection("notes").document(id)
        try {
            docRef.delete().await()
        } catch (error: FirebaseFirestoreException) {
            return Result.failure(error.message.toString())
        }
        return Result.success("Successfully deleted note.")
    }

    suspend fun deleteCategory(id: String): Result<String> {
        val docRef = db.collection("users").document(auth.currentUser!!.uid).collection("categories").document(id)
        try {
            docRef.delete().await()
        } catch (error: FirebaseFirestoreException) {
            return Result.failure(error.message.toString())
        }
        return Result.success("Successfully deleted category.")
    }
}