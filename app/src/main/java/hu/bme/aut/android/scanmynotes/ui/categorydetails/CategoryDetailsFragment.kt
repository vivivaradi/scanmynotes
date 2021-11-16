package hu.bme.aut.android.scanmynotes.ui.categorydetails

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentCategoryDetailsBinding
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.ui.categorydetails.CategoryDetailsFragment.Flipper.EDITING
import hu.bme.aut.android.scanmynotes.ui.categorydetails.CategoryDetailsFragment.Flipper.LOADING
import hu.bme.aut.android.scanmynotes.ui.categorydetails.CategoryDetailsFragment.Flipper.VIEWING
import hu.bme.aut.android.scanmynotes.util.validateTextContent
import java.text.FieldPosition


class CategoryDetailsFragment : RainbowCakeFragment<CategoryDetailsViewState, CategoryDetailsViewModel>(), AdapterView.OnItemSelectedListener {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_category_details

    private lateinit var binding: FragmentCategoryDetailsBinding
    private lateinit var adapter: ArrayAdapter<Category>

    val args: CategoryDetailsFragmentArgs by navArgs()
    var isEditing = false

    object Flipper {
        val LOADING = 0
        val VIEWING = 1
        val EDITING = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        binding.editCategoryView.categorySelectorSpinner.adapter = adapter
        binding.editCategoryView.categorySelectorSpinner.onItemSelectedListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_category_details, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val edit = menu.findItem(R.id.action_edit)
        edit.isVisible = !isEditing

        val save = menu.findItem(R.id.action_save)
        save.isVisible = isEditing
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData(args.categoryId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (!validateTextField())
                    return true
                val newTitle = binding.editCategoryView.editCategoryTitle.text.toString()
                viewModel.saveCategory(newTitle)
                requireActivity().invalidateOptionsMenu()
                true
            }
            R.id.action_edit -> {
                viewModel.editCategory()
                requireActivity().invalidateOptionsMenu()
                true
            }
            R.id.action_delete -> {
                viewModel.deleteCategory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun render(viewState: CategoryDetailsViewState) {
        when (viewState) {
            is Loading -> {
                isEditing = false
                binding.detailsViewFlipper.displayedChild = LOADING
            }
            is Viewing -> {
                isEditing = false
                binding.detailsViewFlipper.displayedChild = VIEWING
                binding.categoryView.categoryTitle.text = viewState.category.title
            }
            is Editing -> {
                isEditing = true
                binding.detailsViewFlipper.displayedChild = EDITING
                binding.editCategoryView.editCategoryTitle.setText(viewState.category.title)

                adapter.clear()
                adapter.add(Category("", getString(R.string.spinner_none_item_title)))
                adapter.addAll(viewModel.categoriesList)
                val selectedPosition = when (viewModel.selectedParent) {
                    null -> 0
                    else -> adapter.getPosition(viewModel.selectedParent)
                }
                binding.editCategoryView.categorySelectorSpinner.setSelection(selectedPosition)
            }
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is CategoryDetailsViewModel.CategoryDeleted -> {
                findNavController().navigate(CategoryDetailsFragmentDirections.categoryDeletedAction())
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = when (position) {
            0 -> null
            else -> parent?.getItemAtPosition(position) as Category
        }
        viewModel.selectParent(selectedItem)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.selectParent(null)
    }

    fun validateTextField(): Boolean = binding.editCategoryView.editCategoryTitle.validateTextContent()
}