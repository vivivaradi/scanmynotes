package hu.bme.aut.android.scanmynotes.ui.newcategory

import hu.bme.aut.android.scanmynotes.domain.models.Category

sealed class NewCategoryViewState

object Loading: NewCategoryViewState()
data class CategoriesLoaded(val categories: List<Category>): NewCategoryViewState()
data class Error(val message: String): NewCategoryViewState()