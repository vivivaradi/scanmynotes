package hu.bme.aut.android.scanmynotes.ui.newnote

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentNewNoteBinding
import hu.bme.aut.android.scanmynotes.util.validateTextContent

class NewNoteFragment: RainbowCakeFragment<NewNoteViewState, NewNoteViewModel>() {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_new_note

    val args : NewNoteFragmentArgs by navArgs()
    private lateinit var binding: FragmentNewNoteBinding

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (!validateTextFields()) {
                    Log.d("DEBUG", "Empty field found")
                    return true
                }
                Log.d("DEBUG", "Reached the saveNote part")
                viewModel.saveNote(binding.newNoteTitle.text.toString(), binding.newNoteText.text.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DEBUG", "Reached new note fragment")

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
        binding.newNoteText.setText(args.noteText)
    }

    fun validateTextFields(): Boolean = binding.newNoteTitle.validateTextContent() && binding.newNoteText.validateTextContent()
}