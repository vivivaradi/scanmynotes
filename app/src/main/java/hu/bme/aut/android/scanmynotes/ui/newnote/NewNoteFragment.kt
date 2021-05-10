package hu.bme.aut.android.scanmynotes.ui.newnote

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.util.validateTextContent
import kotlinx.android.synthetic.main.fragment_new_note.*
import kotlinx.android.synthetic.main.fragment_note_details.*

class NewNoteFragment: RainbowCakeFragment<NewNoteViewState, NewNoteViewModel>() {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_new_note

    val args : NewNoteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (!validateTextFields())
                    return true
                viewModel.saveNote(newNoteTitle.text.toString(), newNoteText.text.toString())
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
        }
    }

    override fun render(viewState: NewNoteViewState) {
        newNoteText.setText(args.noteText)
    }

    fun validateTextFields(): Boolean = editNoteTitle.validateTextContent() && editNoteContent.validateTextContent()
}