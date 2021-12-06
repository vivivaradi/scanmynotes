package hu.bme.aut.android.scanmynotes.ui.categorydetails

import hu.bme.aut.android.scanmynotes.domain.models.Category

sealed class CategoryDetailsViewState

object Loading: CategoryDetailsViewState()
data class Viewing(val category: Category): CategoryDetailsViewState()
data class Editing(val category: Category): CategoryDetailsViewState()
data class Failure(val message: String): CategoryDetailsViewState()