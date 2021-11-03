package hu.bme.aut.android.scanmynotes.ui.newcategory

import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import hu.bme.aut.android.scanmynotes.domain.interactors.Interactor
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.domain.models.Category
import javax.inject.Inject

class NewCategoryViewModel @Inject constructor(
    private val interactor: Interactor
): RainbowCakeViewModel<NewCategoryViewState>(Initial) {

    class NewCategorySavedEvent(val id: String): OneShotEvent
    class CategorySaveEventError(val message: String): OneShotEvent

    fun loadCategories() = execute {
        viewState = Loading
        val result = interactor.getCategories()
        viewState = when (result) {
            is Result.Success -> Success(result.data)
            is Result.Failure -> Failure(result.message)
        }
    }

    fun saveCategory(name: String, parent: Category?) = execute {
        viewState = Loading
        val category = Category("", name, parent?.id)
        val result = interactor.saveCategory(category)
        when (result) {
            is Result.Success -> postEvent(NewCategorySavedEvent(result.data))
            is Result.Failure -> postEvent(CategorySaveEventError(result.message))
        }
    }
}