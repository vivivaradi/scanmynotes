package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.Note
import kotlinx.coroutines.tasks.await

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

    suspend fun getSingleNote(id: String) : Result<Note> {
        Log.d("DEBUG", "API received id: $id")
        var note: Note
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
                return Result.success(note)
            }
        }
        return Result.failure("Note doesn't exist.")
    }

    suspend fun getSingleCategory(id: String): Result<Category> {
        var category: Category
        val categoryRef = db.collection("users").document(auth.currentUser!!.uid).collection("categories").document(id)
        val categorySnapshot = try {
            categoryRef.get().await()
        } catch (error: FirebaseFirestoreException) {
            return Result.failure(error.message.toString())
        }
        if (categorySnapshot.exists()) {
            categorySnapshot.toObject<Category>()?.let { result ->
                category = result
                category.id = categorySnapshot.id
                return Result.success(category)
            }
        }
        return Result.failure("Category doesn't exist.")
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