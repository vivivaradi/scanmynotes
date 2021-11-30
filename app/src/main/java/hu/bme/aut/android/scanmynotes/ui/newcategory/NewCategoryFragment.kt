package hu.bme.aut.android.scanmynotes.ui.newcategory

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentNewCategoryBinding
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.ui.newcategory.NewCategoryFragment.Flipper.LOADING
import hu.bme.aut.android.scanmynotes.ui.newcategory.NewCategoryFragment.Flipper.VIEWING
import hu.bme.aut.android.scanmynotes.util.validateTextContent

class NewCategoryFragment: RainbowCakeFragment<NewCategoryViewState, NewCategoryViewModel>(), AdapterView.OnItemSelectedListener {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_new_category

    private lateinit var binding: FragmentNewCategoryBinding
    private lateinit var adapter: ArrayAdapter<Category>

    object Flipper {
        val LOADING = 0
        val VIEWING = 1
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
        binding = FragmentNewCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        val categoryView = binding.newCategoryView
        categoryView.categorySelectorSpinner.spinner.setAdapter(adapter)
        categoryView.categorySelectorSpinner.spinner.onItemSelectedListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new, menu)
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCategories()
    }

    override fun render(viewState: NewCategoryViewState) {
        when (viewState) {
            is Initial -> Log.d("New Category", "Initial")
            is Loading -> binding.newCategoryViewFlipper.displayedChild = LOADING
            is CategoriesLoaded -> {
                Log.d("New Category", "Success")
                adapter.clear()
                adapter.add(Category("", getString(R.string.spinner_none_item_title)))
                adapter.addAll(viewState.categories)
                binding.newCategoryViewFlipper.displayedChild = VIEWING
            }
            is Failure -> Log.d("New Category", "Failure")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val isValid = binding.newCategoryView.editCategoryTitle.textField.validateTextContent() ?: false
                if (!isValid) {
                    return true
                }
                viewModel.saveCategory(binding.newCategoryView.editCategoryTitle.textField.text.toString())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is NewCategoryViewModel.NewCategorySavedEvent -> findNavController().navigate(NewCategoryFragmentDirections.popAction())
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedItem = when (position) {
            0 -> null
            else -> parent.getItemAtPosition(position) as Category
        }
        viewModel.selectParent(selectedItem)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.selectParent(null)
    }
}