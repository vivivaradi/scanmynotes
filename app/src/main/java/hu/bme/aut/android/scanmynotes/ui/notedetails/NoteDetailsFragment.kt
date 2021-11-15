package hu.bme.aut.android.scanmynotes.ui.notedetails

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import com.vmadalin.easypermissions.EasyPermissions
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentNoteDetailsBinding
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.ui.notelist.NoteListFragment
import hu.bme.aut.android.scanmynotes.util.hasCameraPermission
import hu.bme.aut.android.scanmynotes.util.requestCameraPermission
import hu.bme.aut.android.scanmynotes.util.validateTextContent

class NoteDetailsFragment : RainbowCakeFragment<NoteDetailsViewState, NoteDetailsViewModel>(), EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_note_details

    private lateinit var binding: FragmentNoteDetailsBinding
    private lateinit var adapter: ArrayAdapter<Category>

    val args: NoteDetailsFragmentArgs by navArgs()
    var isEditing = false

    object Flipper {
        val LOADING = 0
        val VIEWING = 1
        val EDITING = 2
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
        val PERMISSION_CAMERA_REQUEST_CODE = 2
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
        binding = FragmentNoteDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        binding.editNoteView.categorySelectorSpinner.adapter = adapter
        binding.editNoteView.categorySelectorSpinner.onItemSelectedListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
    }

    override fun onStart() {
        super.onStart()
        Log.d("DEBUG", "Loading current note with id: ${args.noteId}")
        viewModel.loadData(args.noteId)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val edit = menu.findItem(R.id.action_edit)
        edit.isVisible = !isEditing

        val save = menu.findItem(R.id.action_save)
        save.isVisible = isEditing

        val add = menu.findItem(R.id.action_add)
        add.isVisible = isEditing
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
                viewModel.saveNote(binding.editNoteView.editNoteTitle.text.toString(), binding.editNoteView.editNoteContent.text.toString())
                requireActivity().invalidateOptionsMenu()
                true
            }
            R.id.action_delete -> {
                viewModel.deleteNote()
                true
            }
            R.id.action_add -> {
                takePhoto()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun render(viewState: NoteDetailsViewState) {
        when(viewState) {
            is Loading -> {
                isEditing = false
                binding.detailsViewFlipper.displayedChild = Flipper.LOADING
            }
            is Viewing -> {
                Log.d("DEBUG", "Current note is ${viewState.note.title}")
                isEditing = false
                binding.detailsViewFlipper.displayedChild = Flipper.VIEWING
                binding.noteView.noteTitle.text = viewState.note.title
                binding.noteView.noteContent.text = viewState.note.content
            }
            is Editing -> {
                isEditing = true
                binding.detailsViewFlipper.displayedChild = Flipper.EDITING
                binding.editNoteView.editNoteTitle.setText(viewState.note.title)
                binding.editNoteView.editNoteContent.setText(viewState.note.content)

                adapter.clear()
                adapter.add(Category("", "None"))
                adapter.addAll(viewModel.categoriesList)
                val selectedPosition = when (viewModel.selectedParent) {
                    null -> 0
                    else -> adapter.getPosition(viewModel.selectedParent)
                }
                binding.editNoteView.categorySelectorSpinner.setSelection(selectedPosition)
            }
            is Failure -> Log.d("DEBUG", viewState.message)
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is NoteDetailsViewModel.NoteDeletedEvent -> {
                findNavController().navigate(NoteDetailsFragmentDirections.noteDeletedAction())
            }
            is NoteDetailsViewModel.TextReady -> {
                binding.editNoteView.editNoteContent.append(event.text)
            }
            is NoteDetailsViewModel.NoTextFoundEvent -> {
                Toast.makeText(requireContext(), "Couldn't find any text on the image!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun takePhoto() {
        if (hasCameraPermission()) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { photoIntent ->
                photoIntent.resolveActivity(requireContext().packageManager)?.also {
                    startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE)
                } } }
        else {
            Toast.makeText(requireContext(), "You need to grant camera access to the application, if you want to use this feature", Toast.LENGTH_LONG).show()
            requestCameraPermission(NoteListFragment.PERMISSION_CAMERA_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            data.also {
                val image = it?.extras?.get("data") as Bitmap
                viewModel.digitalizePhoto(image)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(requireContext(), "You need to manually grant camera permission, in order to use this app.", Toast.LENGTH_LONG).show()
        } else {
            requestCameraPermission(PERMISSION_CAMERA_REQUEST_CODE)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        takePhoto()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedItem = when (position) {
            0 -> null
            else -> parent.getItemAtPosition(position) as Category
        }
        Log.d("Spinner Selection", "object id: ${selectedItem?.id}, title: ${selectedItem?.title}")
        viewModel.selectParent(selectedItem)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.selectParent(null)
    }

    fun validateTextFields(): Boolean = binding.editNoteView.editNoteTitle.validateTextContent() && binding.editNoteView.editNoteContent.validateTextContent()

}