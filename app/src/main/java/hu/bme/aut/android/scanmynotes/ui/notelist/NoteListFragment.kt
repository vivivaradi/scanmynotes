package hu.bme.aut.android.scanmynotes.ui.notelist

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.ui.notelist.models.UiNotePreview
import kotlinx.android.synthetic.main.fragment_note_list.*
import java.util.*

class NoteListFragment : RainbowCakeFragment<NoteListViewState, NoteListViewModel>(), NoteListAdapter.Listener {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource(): Int {
        return R.layout.fragment_note_list
    }

    private lateinit var adapter: NoteListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NoteListAdapter()
        adapter.listener = this
        listNotes.adapter = adapter

        floatingButton.setOnClickListener {
            takePhoto()
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.load()
    }

    override fun render(viewState: NoteListViewState) {
        when(viewState){
            is Loading -> Log.d("INFO", "Loading")
            is NotesReady -> adapter.submitList(viewState.notes)
            is NewNoteReady -> findNavController().navigate(NoteListFragmentDirections.newNoteAction(viewState.detectedText))
        }
    }

    override fun onNoteClicked(note: UiNotePreview) {
        findNavController().navigate(NoteListFragmentDirections.openNoteAction(note.noteId))
    }

    fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { photoIntent ->
            photoIntent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE)
            } } }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            data.also {
                val image = it?.extras?.get("data") as Bitmap
                viewModel.digitalizePhoto(image)
            }
        }
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

}