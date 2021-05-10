package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import kotlinx.android.synthetic.main.fragment_note_details.*

class NoteDetailsFragment : RainbowCakeFragment<NoteDetailsViewState, NoteDetailsViewModel>() {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_note_details

    val args: NoteDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DEBUG", "Reached note detail fragment")
        editNoteButton.setOnClickListener {
            viewModel.editNote()
        }

        viewModel.setupDataFlow()
        Log.d("DEBUG", "SetupDataFlow should have run")
        viewModel.noteList.observe(viewLifecycleOwner) {
        }
        Log.d("DEBUG", "Observing noteList")

        saveNoteButton.setOnClickListener {
            viewModel.saveNote(editNoteTitle.text.toString(), editNoteContent.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("DEBUG", "Loading current note with id: ${args.noteId}")
        viewModel.loadCurrentNote(args.noteId)
    }

    override fun render(viewState: NoteDetailsViewState) {
        when(viewState) {
            is Viewing -> {
                Log.d("DEBUG", "Current note is ${viewState.note.title}")
                editNoteTitle.isEnabled = false
                editNoteContent.isEnabled = false
                editNoteButton.visibility = View.VISIBLE
                saveNoteButton.visibility = View.GONE
                editNoteTitle.setText(viewState.note.title)
                editNoteContent.setText(viewState.note.content)
            }
            is Editing -> {
                editNoteTitle.isEnabled = true
                editNoteContent.isEnabled = true
                editNoteButton.visibility = View.GONE
                saveNoteButton.visibility = View.VISIBLE
                editNoteTitle.setText(viewState.note.title)
                editNoteContent.setText(viewState.note.content)
            }
            is NoteDeleted ->
                findNavController().navigate(NoteDetailsFragmentDirections.noteDeletedAction())
        }
    }

}