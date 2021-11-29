package hu.bme.aut.android.scanmynotes.ui.newnote

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentNewNoteBinding
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.ui.newnote.NewNoteFragment.Flipper.LOADING
import hu.bme.aut.android.scanmynotes.ui.newnote.NewNoteFragment.Flipper.VIEWING
import hu.bme.aut.android.scanmynotes.util.validateTextContent

class NewNoteFragment: RainbowCakeFragment<NewNoteViewState, NewNoteViewModel>(), AdapterView.OnItemSelectedListener {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_new_note

    val args : NewNoteFragmentArgs by navArgs()
    private lateinit var binding: FragmentNewNoteBinding
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
        binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        val noteView = binding.newNoteView
        noteView.categorySelectorSpinner.spinner.adapter = adapter
        noteView.categorySelectorSpinner.spinner.onItemSelectedListener = this
        noteView.editNoteTitle.inputLayout.hint = getString(R.string.title_text_field_hint)
        noteView.editNoteContent.inputLayout.hint = getString(R.string.content_text_field_hint)
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCategories()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (!validateTextFields()) {
                    return true
                }
                viewModel.saveNote(binding.newNoteView.editNoteTitle.textField.text.toString(), binding.newNoteView.editNoteContent.textField.text.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is NewNoteViewModel.NewNoteSavedEvent -> {
                findNavController().navigate(NewNoteFragmentDirections.savedNewNoteAction(event.id))
            }
            is NewNoteViewModel.NoteSaveEventError -> {
                // TODO
            }
        }
    }

    override fun render(viewState: NewNoteViewState) {
        when (viewState) {
            is Initial -> Log.d("New Note", "Initial")
            is Loading -> binding.newNoteViewFlipper.displayedChild = LOADING
            is CategoriesLoaded -> {
                Log.d("New Note", "Success")
                adapter.clear()
                adapter.add(Category("", getString(R.string.spinner_none_item_title)))
                adapter.addAll(viewState.categories)
                binding.newNoteView.editNoteContent.textField.setText(args.noteText)
                binding.newNoteViewFlipper.displayedChild = VIEWING
            }
            is Failure -> Log.d("New Note", "Failure")
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

    fun validateTextFields(): Boolean = binding.newNoteView.editNoteTitle.textField.validateTextContent() && binding.newNoteView.editNoteContent.textField.validateTextContent()
}