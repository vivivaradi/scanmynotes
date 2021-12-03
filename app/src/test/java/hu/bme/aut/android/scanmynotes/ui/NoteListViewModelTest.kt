package hu.bme.aut.android.scanmynotes.ui

import android.graphics.Bitmap
import co.zsmb.rainbowcake.test.assertObserved
import co.zsmb.rainbowcake.test.assertObservedLast
import co.zsmb.rainbowcake.test.base.ViewModelTest
import co.zsmb.rainbowcake.test.observeStateAndEvents
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.ui.notelist.Error
import hu.bme.aut.android.scanmynotes.ui.notelist.ListLoaded
import hu.bme.aut.android.scanmynotes.ui.notelist.NoteListViewModel
import hu.bme.aut.android.scanmynotes.ui.notelist.SelectedNavItem
import hu.bme.aut.android.scanmynotes.ui.notelist.SortOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NoteListViewModelTest: ViewModelTest() {

    @Mock
    lateinit var mockInteractor: Interactor

    lateinit var noteListViewModel: NoteListViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        noteListViewModel = NoteListViewModel(mockInteractor)
    }

    @Test
    fun loadViewModel_withComplexList() = runBlocking {
        `when`(mockInteractor.getComplexList()).thenReturn(Result.success(expectedComplexList))
        `when`(mockInteractor.getNotes()).thenReturn(Result.success(expectedNoteList))

        noteListViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            noteListViewModel.load(SelectedNavItem.CATEGORIES)
            stateObserver.assertObservedLast(ListLoaded(expectedComplexList))
        }
    }

    @Test
    fun loadViewModel_withNoteList() = runBlocking {
        `when`(mockInteractor.getComplexList()).thenReturn(Result.success(expectedComplexList))
        `when`(mockInteractor.getNotes()).thenReturn(Result.success(expectedNoteList))

        noteListViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            noteListViewModel.load(SelectedNavItem.NOTES)
            stateObserver.assertObservedLast(ListLoaded(expectedNoteList))
        }
    }

    @Test
    fun loadViewModel_withError() = runBlocking {
        `when`(mockInteractor.getComplexList()).thenReturn(Result.failure("complexError"))

        noteListViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            noteListViewModel.load(SelectedNavItem.CATEGORIES)
            stateObserver.assertObservedLast(Error("complexError"))
        }
    }

    @Test
    fun searchList() = runBlocking {
        `when`(mockInteractor.getComplexList()).thenReturn(Result.success(expectedComplexList))
        `when`(mockInteractor.getNotes()).thenReturn(Result.success(expectedNoteList))

        noteListViewModel.load(SelectedNavItem.NOTES)
        val actualList = noteListViewModel.filterList(searchString)
        Assert.assertEquals(expectedFilteredList, actualList)
    }

    @Test
    fun sortList_descending() = runBlocking {
        `when`(mockInteractor.getComplexList()).thenReturn(Result.success(expectedComplexList))
        `when`(mockInteractor.getNotes()).thenReturn(Result.success(expectedNoteList))

        noteListViewModel.load(SelectedNavItem.NOTES)
        val actualList = noteListViewModel.sortList(SortOptions.ALPHA_DESC)
        Assert.assertEquals(expectedSortedList, actualList)
    }

    companion object {
        val categoryInner = Category("2", "CategoryInner", "1")
        val noteInner = Note("3", "NoteInner", "1", "testcontent")
        val noteOuter = Note("4", "NoteOuter", null, "testcontent2")
        val categoryOuter = Category("1", "CategoryOuter", null, arrayListOf(categoryInner, noteInner))

        val searchString = "Ou"

        val expectedComplexList = listOf(categoryOuter, noteOuter)
        val expectedNoteList = listOf(noteInner, noteOuter)

        val expectedFilteredList = listOf(noteOuter)

        val expectedSortedList = listOf(noteOuter, noteInner)
    }
}