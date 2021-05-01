package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import kotlinx.android.synthetic.main.fragment_note_details.*

class NoteDetailsFragment : RainbowCakeFragment<NoteDetailsViewState, NoteDetailsViewModel>() {
    override fun provideViewModel() = getViewModelFromFactory()

    val args: NoteDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editNoteButton.setOnClickListener {
            viewModel.editNote()
        }

        saveNoteButton.setOnClickListener {
            viewModel.saveNote(editNoteTitle.text.toString(), editNoteContent.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCurrentNote(args.noteId)
    }

    override fun render(viewState: NoteDetailsViewState) {
        when(viewState) {
            is Viewing -> {
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