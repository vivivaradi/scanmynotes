package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import com.google.api.services.vision.v1.model.Image
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.domain.models.Note
import javax.inject.Inject

class NetworkDataSource @Inject constructor(
    private val firebaseApi: FirebaseApi,
    private val visionApi: VisionApi
){

    suspend fun getComplexList(): Result<List<ListItem>> {
        Log.d("DEBUG", "Datasource reached")
        val notesResult = firebaseApi.fetchNotes()
        val categoriesResult = firebaseApi.fetchCategories()
        return when {
            categoriesResult is Result.Success<List<Category>> && notesResult is Result.Success<List<Note>> -> {
                Log.d("DEBUG", "Successful API call")
                Result.success(buildList(categoriesResult.data, notesResult.data))
            }
            categoriesResult is Result.Failure -> Result.failure(categoriesResult.message)
            notesResult is Result.Failure -> Result.failure(notesResult.message)
            else -> Result.failure("Unexpected error: Results of network calls do not match.")
        }
    }

    suspend fun getNotes(): Result<List<Note>> {
        return firebaseApi.fetchNotes()
    }

    suspend fun getCategories(): Result<List<Category>> {
        return firebaseApi.fetchCategories()
    }

    fun getAuth() = firebaseApi.auth

    fun getCurrentUser() = firebaseApi.getCurrentUser()

    suspend fun detectText(image: Image): String? {
        return visionApi.detectText(image)
    }

    suspend fun getSingleNote(id: String): Result<Note> {
        return firebaseApi.getSingleNote(id)
    }

    suspend fun getSingleCategory(id: String): Result<Category> {
        return firebaseApi.getSingleCategory(id)
    }

    suspend fun saveNote(note: Note): Result<String> {
        return firebaseApi.saveNote(note)
    }

    suspend fun saveCategory(category: Category): Result<String> {
        return firebaseApi.saveCategory(category)
    }

    suspend fun deleteNote(id: String): Result<String> {
        return firebaseApi.deleteNote(id)
    }

    suspend fun deleteCategory(id: String): Result<String> {
        return firebaseApi.deleteCategory(id)
    }

    private fun buildList(categories: List<Category>, notes: List<Note>): List<ListItem> {
        val noteList = ArrayList<ListItem>()
        for (category in categories) {
            if (category.parentId != null) {
                val parent = categories.find { elem ->
                    elem.id == category.parentId
                }
                if (parent != null) {
                    parent.listItems.add(category)
                } else {
                    category.parentId = null
                    noteList.add(category)
                }
            } else {
                noteList.add(category)
            }
        }
        for (note in notes) {
            if (note.parentId != null) {
                val parent = categories.find { elem ->
                    elem.id == note.parentId
                }
                if (parent != null) {
                    parent.listItems.add(note)
                } else {
                    // note.parentId = null
                    noteList.add(note)
                }
            } else {
                noteList.add(note)
            }
        }
        Log.d("DEBUG", noteList.toString())
        return noteList
    }
}