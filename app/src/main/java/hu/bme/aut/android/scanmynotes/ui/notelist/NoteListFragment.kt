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
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
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
            is Initial -> Log.d(getString(R.string.debug_tag), "Initial")
            is Loading -> Log.d(getString(R.string.debug_tag), "Loading")
            is NotesReady -> {
                adapter.submitList(viewState.noteList)
                Log.d(getString(R.string.debug_tag), "Notes Ready")

            }
            is NewNoteReady -> {
                Log.d(getString(R.string.debug_tag), "New Note Ready")
                findNavController().navigate(NoteListFragmentDirections.newNoteAction(viewState.detectedText))
            }
        }
    }

    override fun onNoteClicked(note: DomainNote) {
        findNavController().navigate(NoteListFragmentDirections.openNoteAction(note.id))
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