package hu.bme.aut.android.scanmynotes.ui.notedetails

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
import kotlinx.android.synthetic.main.fragment_note_details.*

class NoteDetailsFragment : RainbowCakeFragment<NoteDetailsViewState, NoteDetailsViewModel>() {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_note_details

    val args: NoteDetailsFragmentArgs by navArgs()
    var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DEBUG", "Reached note detail fragment")

        viewModel.setupDataFlow()
        Log.d("DEBUG", "SetupDataFlow should have run")
        viewModel.noteList.observe(viewLifecycleOwner) {
        }
        Log.d("DEBUG", "Observing noteList")

    }

    override fun onStart() {
        super.onStart()
        Log.d("DEBUG", "Loading current note with id: ${args.noteId}")
        viewModel.loadCurrentNote(args.noteId)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val edit = menu.findItem(R.id.action_edit)
        edit.isVisible = !isEditing

        val save = menu.findItem(R.id.action_save)
        save.isVisible = isEditing
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                viewModel.editNote()
                requireActivity().invalidateOptionsMenu()
                true
            }
            R.id.action_save -> {
                if (!validateTextFields())
                    return true
                viewModel.saveNote(editNoteTitle.text.toString(), editNoteContent.text.toString())
                requireActivity().invalidateOptionsMenu()
                true
            }
            R.id.action_delete -> {
                viewModel.deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun render(viewState: NoteDetailsViewState) {
        when(viewState) {
            is Viewing -> {
                Log.d("DEBUG", "Current note is ${viewState.note.title}")
                isEditing = false
                editNoteTitle.isEnabled = false
                editNoteContent.isEnabled = false
                editNoteTitle.setText(viewState.note.title)
                editNoteContent.setText(viewState.note.content)
            }
            is Editing -> {
                isEditing = true
                editNoteTitle.isEnabled = true
                editNoteContent.isEnabled = true
                editNoteTitle.setText(viewState.note.title)
                editNoteContent.setText(viewState.note.content)
            }
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is NoteDetailsViewModel.NoteDeletedEvent -> {
                findNavController().navigate(NoteDetailsFragmentDirections.noteDeletedAction())
            }
        }

    }

    fun validateTextFields(): Boolean = editNoteTitle.validateTextContent() && editNoteContent.validateTextContent()

}