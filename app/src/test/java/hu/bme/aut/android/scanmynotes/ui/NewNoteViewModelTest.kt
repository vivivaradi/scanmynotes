package hu.bme.aut.android.scanmynotes.ui

import co.zsmb.rainbowcake.test.assertObservedLast
import co.zsmb.rainbowcake.test.base.ViewModelTest
import co.zsmb.rainbowcake.test.observeStateAndEvents
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.ui.newnote.CategoriesLoaded
import hu.bme.aut.android.scanmynotes.ui.newnote.NewNoteViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class NewNoteViewModelTest: ViewModelTest() {

    @Mock
    lateinit var mockInteractor: Interactor

    lateinit var newNoteViewModel: NewNoteViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        newNoteViewModel = NewNoteViewModel(mockInteractor)
    }

    @Test
    fun loadViewModel() = runBlocking {
        `when`(mockInteractor.getCategories()).thenReturn(Result.success(expectedCategoryList))

        newNoteViewModel.observeStateAndEvents { stateObserver, eventsObserver ->
            newNoteViewModel.loadCategories()
            stateObserver.assertObservedLast(CategoriesLoaded(expectedCategoryList))
        }
    }

    companion object {
        val category1 = Category("1", "category1", null)
        val category2 = Category("2", "category2", "1")

        val expectedCategoryList = listOf(category1, category2)

        val expectedNewNote = Note("3", "newNote", "1", "newContent")
    }
}