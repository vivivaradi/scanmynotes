package hu.bme.aut.android.scanmynotes.ui.notelist

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import com.vmadalin.easypermissions.EasyPermissions
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentNoteListBinding
import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.domain.models.Note
import hu.bme.aut.android.scanmynotes.ui.notelist.items.CategoryItem
import hu.bme.aut.android.scanmynotes.ui.notelist.items.NoteItem
import hu.bme.aut.android.scanmynotes.util.hasCameraPermission
import hu.bme.aut.android.scanmynotes.util.requestCameraPermission

class NoteListFragment : RainbowCakeFragment<NoteListViewState, NoteListViewModel>(), EasyPermissions.PermissionCallbacks {
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource(): Int {
        return R.layout.fragment_note_list
    }

    private lateinit var adapter: GroupieAdapter
    private lateinit var binding: FragmentNoteListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupieAdapter()
        binding.listNotes.adapter = adapter
        adapter.setOnItemClickListener(onItemClicked)

        binding.floatingButton.setOnClickListener {
            takePhoto()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.clear()
        viewModel.load()
    }

    override fun render(viewState: NoteListViewState) {
        when(viewState){
            is Initial -> Log.d(getString(R.string.debug_tag), "Initial")
            is Loading -> Log.d(getString(R.string.debug_tag), "Loading")
            is Success -> {
                adapter.add(populateList(viewState.noteList))
                Log.d(getString(R.string.debug_tag), "Notes Ready")
            }
            is Error -> {
                Log.d("ERROR", viewState.message)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Not the best solution, should restrict this into the API, but good enough for now
                viewModel.getAuth().signOut()
                findNavController().navigate(NoteListFragmentDirections.logoutAction())
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    private val onItemClicked = OnItemClickListener { item, _ ->
        Log.d("DEBUG", "Note clicked: ${item.id}")
        if (item is NoteItem) {
            val note = item.note
            findNavController().navigate(NoteListFragmentDirections.openNoteAction(note.id))
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
            requestCameraPermission(PERMISSION_CAMERA_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            data.also {
                val image = it?.extras?.get("data") as Bitmap
                viewModel.digitalizePhoto(image)
            }
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is NoteListViewModel.NewNoteReadyEvent -> {
                Log.d(getString(R.string.debug_tag), "New Note Ready")
                findNavController().navigate(NoteListFragmentDirections.newNoteAction(event.text))
            }
            is NoteListViewModel.NoTextFoundEvent -> {
                Toast.makeText(requireContext(), "Couldn't find any text on the image!", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
        val PERMISSION_CAMERA_REQUEST_CODE = 2
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

    private fun populateList(itemList: List<ListItem>): Section {
        val mainSection = Section()
        for(item in itemList) {
            when (item) {
                is Category -> {
                    val group = ExpandableGroup(CategoryItem(item))
                    populateCategory(item.listItems, group)
                    mainSection.add(group)
                }
                is Note -> mainSection.add(NoteItem(item))
            }
        }
        return mainSection
    }

    private fun populateCategory(itemList: List<ListItem>, section: ExpandableGroup) {
        for (item in itemList) {
            when (item) {
                is Category -> {
                    val group = ExpandableGroup(CategoryItem(item))
                    populateCategory(item.listItems, group)
                    section.add(group)
                }
                is Note -> section.add(NoteItem(item))
            }
        }
    }
}