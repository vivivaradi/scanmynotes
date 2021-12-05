package hu.bme.aut.android.scanmynotes.domain.interactors

import android.graphics.Bitmap
import android.util.Log
import co.zsmb.rainbowcake.withIOContext
import com.google.api.services.vision.v1.model.Image
import hu.bme.aut.android.scanmynotes.data.network.NetworkDataSource
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.Note
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class Interactor @Inject constructor(
    private val networkDataSource: NetworkDataSource
){
    suspend fun digitalize(image: Bitmap): String? = withIOContext {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val inputImage = Image()
        inputImage.encodeContent(byteArray)
        networkDataSource.detectText(inputImage)
    }

    suspend fun getComplexList() = withIOContext {
        networkDataSource.getComplexList()
    }

    suspend fun getNotes() = withIOContext {
        networkDataSource.getNotes()
    }

    suspend fun getCategories() = withIOContext {
        networkDataSource.getCategories()
    }

    suspend fun saveNote(note: Note) = withIOContext {
        networkDataSource.saveNote(note)
    }

    suspend fun saveCategory(category: Category) = withIOContext {
        networkDataSource.saveCategory(category)
    }

    suspend fun getSingleNote(id: String): Result<Note> = withIOContext {
        networkDataSource.getSingleNote(id)
    }

    suspend fun getSingleCategory(id: String): Result<Category> = withIOContext {
        networkDataSource.getSingleCategory(id)
    }

    suspend fun deleteNote(id: String) = withIOContext {
        networkDataSource.deleteNote(id)
    }

    suspend fun deleteCategory(id: String) = withIOContext {
        networkDataSource.deleteCategory(id)
    }
}