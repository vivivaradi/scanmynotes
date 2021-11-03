package hu.bme.aut.android.scanmynotes.ui.newcategory

import hu.bme.aut.android.scanmynotes.domain.models.Category

sealed class NewCategoryViewState
object Initial: NewCategoryViewState()
object Loading: NewCategoryViewState()
data class Success(val categories: List<Category>): NewCategoryViewState()
data class Failure(val message: String): NewCategoryViewState()