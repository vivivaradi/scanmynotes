package hu.bme.aut.android.scanmynotes.ui.newnote

import hu.bme.aut.android.scanmynotes.domain.models.Category

sealed class NewNoteViewState

object Loading: NewNoteViewState()
data class CategoriesLoaded(val categories: List<Category>): NewNoteViewState()
data class Failure(val message: String): NewNoteViewState()
