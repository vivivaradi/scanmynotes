package hu.bme.aut.android.scanmynotes.ui.notelist.items

import android.view.View
import com.xwray.groupie.ExpandableGroup
import hu.bme.aut.android.scanmynotes.domain.models.Category
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.CategoryRowBinding

class CategoryItem(val category: Category): BindableItem<CategoryRowBinding>(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    private val onClickListener = View.OnClickListener {
        expandableGroup.onToggleExpanded()

    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    override fun getLayout(): Int {
        return R.layout.category_row
    }

    override fun initializeViewBinding(view: View): CategoryRowBinding {
        return CategoryRowBinding.bind(view)
    }

    override fun bind(viewBinding: CategoryRowBinding, position: Int) {
        viewBinding.tvCategoryTitle.text = category.title
        viewBinding.arrowIcon.setImageResource(R.drawable.outline_expand_more_24)
    }
}