package hu.bme.aut.android.scanmynotes.ui.categorydetails

import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.domain.models.Category
import javax.inject.Inject

class CategoryDetailsViewModel @Inject constructor(
    private val interactor: Interactor
    ): RainbowCakeViewModel<CategoryDetailsViewState>(Initial) {

    lateinit var currentCategory: Category
    var selectedParent: Category? = null
    lateinit var categoriesList: List<Category>

    object CategoryDeleted: OneShotEvent

    fun loadData(categoryId: String) = execute {
        viewState = Loading
        val categoryResult = interactor.getSingleCategory(categoryId)
        val categoriesResult = interactor.getCategories()
        viewState = when {
            categoryResult is Result.Success && categoriesResult is Result.Success  -> {
                currentCategory = categoryResult.data
                categoriesList = categoriesResult.data
                if (currentCategory.parentId != null) {
                    selectedParent = categoriesList.find { elem ->
                        elem.id == currentCategory.parentId
                    }
                }
                Viewing(currentCategory)
            }
            categoryResult is Result.Failure -> Failure(categoryResult.message)
            categoriesResult is Result.Failure -> Failure(categoriesResult.message)
            else -> Failure(R.string.failure_unknown_error_text.toString())
        }
    }

    fun editCategory() {
        viewState = Editing(currentCategory)
    }

    fun saveCategory(title: String) = execute {
        viewState = Loading
        val updatedCategory = Category(currentCategory.id, title, selectedParent?.id)
        val result = interactor.saveCategory(updatedCategory)
        viewState = when (result) {
            is Result.Success -> {
                currentCategory = updatedCategory
                Viewing(currentCategory)
            }
            is Result.Failure -> Failure(result.message)
        }
    }

    fun deleteCategory() = execute {
        viewState = Loading
        interactor.deleteCategory(currentCategory.id)
        postEvent(CategoryDeleted)
    }

    fun selectParent(category: Category?) {
        selectedParent = category
    }
}
