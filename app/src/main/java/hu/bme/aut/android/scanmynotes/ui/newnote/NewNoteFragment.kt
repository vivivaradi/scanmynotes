package hu.bme.aut.android.scanmynotes.ui.newnote

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import kotlinx.android.synthetic.main.fragment_new_note.*

class NewNoteFragment: RainbowCakeFragment<NewNoteViewState, NewNoteViewModel>() {
    override fun provideViewModel() = getViewModelFromFactory()

    val args : NewNoteFragmentArgs by navArgs()

    override fun render(viewState: NewNoteViewState) {
        newNoteText.setText(args.noteText)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveNoteButton.setOnClickListener {
            viewModel.saveNote(newNoteTitle.text.toString(), newNoteText.text.toString())
        }
    }
}