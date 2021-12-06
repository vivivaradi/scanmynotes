package hu.bme.aut.android.scanmynotes.ui

import co.zsmb.rainbowcake.test.assertObservedLast
import co.zsmb.rainbowcake.test.base.ViewModelTest
import co.zsmb.rainbowcake.test.observeStateAndEvents
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.ui.notedetails.NoteDetailsViewModel
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.ui.notedetails.Error
import hu.bme.aut.android.scanmynotes.ui.notedetails.Viewing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NoteDetailsViewModelTest: ViewModelTest() {

    @Mock
    lateinit var mockInteractor: Interactor

    lateinit var noteDetailsViewModel: NoteDetailsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        noteDetailsViewModel = NoteDetailsViewModel(mockInteractor)
    }

    @Test
    fun loadViewModel() = runBlocking {
        `when`(mockInteractor.getSingleNote("1")).thenReturn(Result.success(expectedNote))
        `when`(mockInteractor.getCategories()).thenReturn(Result.success(expectedCategoryList))

        noteDetailsViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            noteDetailsViewModel.loadData("1")
            stateObserver.assertObservedLast(Viewing(expectedNote))
        }
    }

    @Test
    fun loadViewModel_withError() = runBlocking {
        `when`(mockInteractor.getSingleNote("1")).thenReturn(Result.failure("noteError"))

        noteDetailsViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            noteDetailsViewModel.loadData("1")
            stateObserver.assertObservedLast(Error("noteError"))
        }
    }

    @Test
    fun saveNote() = runBlocking {
        `when`(mockInteractor.getSingleNote("1")).thenReturn(Result.success(expectedNote))
        `when`(mockInteractor.getCategories()).thenReturn(Result.success(expectedCategoryList))
        `when`(mockInteractor.saveNote(Note("1", "newTitle", null, "testcontentcontent"))).thenReturn(Result.success("1"))

        noteDetailsViewModel.loadData("1")
        noteDetailsViewModel.saveNote("newTitle", "testcontentcontent")
        Assert.assertEquals(expectedModifiedNote.title, noteDetailsViewModel.currentNote.title)
        Assert.assertEquals(expectedModifiedNote.content, noteDetailsViewModel.currentNote.content)

    }

    @Test
    fun deleteNote() = runBlocking {
        `when`(mockInteractor.getSingleNote("1")).thenReturn(Result.success(expectedNote))
        `when`(mockInteractor.getCategories()).thenReturn(Result.success(expectedCategoryList))
        `when`(mockInteractor.deleteNote("1")).thenReturn(Result.success("1"))

        noteDetailsViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            noteDetailsViewModel.loadData("1")
            noteDetailsViewModel.deleteNote()
            eventsObserver.assertObservedLast(NoteDetailsViewModel.NoteDeletedEvent)
        }
    }

    companion object {
        val expectedNote = Note("1", "Note1", null, "testcontent")
        val category1 = Category("2", "Category1", null)
        val category2 = Category("3", "Category2", "2")
        val expectedCategoryList = listOf(category1, category2)

        val expectedModifiedNote = Note("1", "newTitle", null, "testcontentcontent")
    }
}