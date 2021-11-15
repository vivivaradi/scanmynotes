package hu.bme.aut.android.scanmynotes.ui.notelist

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.findNavController
import co.zsmb.rainbowcake.base.OneShotEvent
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import com.vmadalin.easypermissions.EasyPermissions
import com.xwray.groupie.OnItemClickListener
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.FragmentNoteListBinding
import hu.bme.aut.android.scanmynotes.ui.notelist.NoteListFragment.Flipper.LOADING
import hu.bme.aut.android.scanmynotes.ui.notelist.NoteListFragment.Flipper.VIEWING
import hu.bme.aut.android.scanmynotes.ui.notelist.items.NoteItem
import hu.bme.aut.android.scanmynotes.util.hasCameraPermission
import hu.bme.aut.android.scanmynotes.util.requestCameraPermission
import java.lang.IllegalStateException

class NoteListFragment : RainbowCakeFragment<NoteListViewState, NoteListViewModel>(), EasyPermissions.PermissionCallbacks, SearchView.OnQueryTextListener{
    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource(): Int {
        return R.layout.fragment_note_list
    }

    private lateinit var adapter: NoteListAdapter
    private lateinit var binding: FragmentNoteListBinding
    private val sortingDialog = SortDialog(0)

    private var isFloatingMenuOpen = false
    private var lastSelectedNavItem: SelectedNavItem = SelectedNavItem.CATEGORIES
    private var lastSelectedSorting: SortOptions = SortOptions.ALPHA_ASC

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(activity, R.anim.to_bottom_anim) }

    object Flipper {
        val LOADING = 0
        val VIEWING = 1
    }

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
    ): View {
        binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteListView = binding.noteListView

        adapter = NoteListAdapter()
        noteListView.listNotes.adapter = adapter
        adapter.setOnItemClickListener(onItemClicked)

        noteListView.floatingButton.setOnClickListener {
            animateButtons()
        }

        noteListView.addNoteButton.setOnClickListener {
            isFloatingMenuOpen = !isFloatingMenuOpen
            takePhoto()
        }

        noteListView.addCategoryButton.setOnClickListener {
            isFloatingMenuOpen = !isFloatingMenuOpen
            findNavController().navigate(NoteListFragmentDirections.newCategoryAction())
        }

        noteListView.bottomNavigationView.setOnItemSelectedListener { item ->
            onBottomNavItemSelected(item)
        }

        noteListView.noteSearchView.setIconifiedByDefault(false)
        noteListView.noteSearchView.setOnQueryTextListener(this)

        noteListView.orderButton.setOnClickListener {
            parentFragmentManager.setFragmentResultListener(getString(R.string.sort_dialog_result_requestkey), viewLifecycleOwner, { requestKey, result ->
                val chosenOption = getString(R.string.sort_dialog_selected_option_key)
                val sorting = result[chosenOption] as SortOptions
                if (sorting != lastSelectedSorting) {
                    adapter.showList(viewModel.sortList(sorting))
                    lastSelectedSorting = sorting
                }
            })
            sortingDialog.show(parentFragmentManager, getString(R.string.sort_dialog_tag))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.load(lastSelectedNavItem)
    }

    override fun render(viewState: NoteListViewState) {
        when(viewState){
            is Initial -> Log.d(getString(R.string.debug_tag), "Initial")
            is Loading -> binding.noteListViewFlipper.displayedChild = LOADING
            is Success -> {
                setSearchVisibility()
                adapter.showList(viewState.noteList)
                binding.noteListViewFlipper.displayedChild = VIEWING
                Log.d(getString(R.string.debug_tag), "Notes Ready")
            }
            is Error -> {
                Log.d(getString(R.string.error_tag), viewState.message)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // TODO: Not the best solution, should restrict this into the API, but good enough for now
                viewModel.getAuth().signOut()
                findNavController().navigate(NoteListFragmentDirections.logoutAction())
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    private val onItemClicked = OnItemClickListener { item, _ ->
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
            Toast.makeText(requireContext(), getString(R.string.camera_no_permission_toast_text), Toast.LENGTH_LONG).show()
            requestCameraPermission(PERMISSION_CAMERA_REQUEST_CODE)
        }
    }


    private fun onBottomNavItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.categories_item -> {
                lastSelectedNavItem = SelectedNavItem.CATEGORIES
                setSearchVisibility()
                adapter.showList(viewModel.complexList)
                true
            }
            R.id.notes_item -> {
                lastSelectedNavItem = SelectedNavItem.NOTES
                setSearchVisibility()
                adapter.showList(viewModel.noteList)
                true
            }
            else -> false
        }
    }

    private fun setSearchVisibility() {
        when (lastSelectedNavItem) {
            SelectedNavItem.CATEGORIES -> {
                binding.noteListView.noteSearchView.isVisible = false
                binding.noteListView.orderButton.isVisible = false
            }
            SelectedNavItem.NOTES -> {
                binding.noteListView.noteSearchView.isVisible = true
                binding.noteListView.orderButton.isVisible = true
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            data.also {
                val image = it?.extras?.get(getString(R.string.text_detection_result_data_key)) as Bitmap
                viewModel.digitalizePhoto(image)
            }
        }
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is NoteListViewModel.NewNoteReadyEvent -> {
                findNavController().navigate(NoteListFragmentDirections.newNoteAction(event.text))
            }
            is NoteListViewModel.NoTextFoundEvent -> {
                Toast.makeText(requireContext(), getString(R.string.text_detection_no_text_found), Toast.LENGTH_LONG).show()
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
            Toast.makeText(requireContext(), getString(R.string.camera_permission_denied_toast_text), Toast.LENGTH_LONG).show()
        } else {
            requestCameraPermission(PERMISSION_CAMERA_REQUEST_CODE)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        takePhoto()
    }


    private fun animateButtons() {
        if (isFloatingMenuOpen) {
            closeMenu()
        } else {
            openMenu()
        }
        isFloatingMenuOpen = !isFloatingMenuOpen
    }

    private fun openMenu() {
        val noteListView = binding.noteListView
        noteListView.addCategoryButton.visibility = View.VISIBLE
        noteListView.addNoteButton.visibility = View.VISIBLE
        noteListView.addCategoryButton.startAnimation(fromBottom)
        noteListView.addNoteButton.startAnimation(fromBottom)
        noteListView.floatingButton.startAnimation(rotateOpen)
    }

    private fun closeMenu() {
        val noteListView = binding.noteListView
        noteListView.addNoteButton.visibility = View.GONE
        noteListView.addCategoryButton.visibility = View.GONE
        noteListView.addCategoryButton.startAnimation(toBottom)
        noteListView.addNoteButton.startAnimation(toBottom)
        noteListView.floatingButton.startAnimation(rotateClose)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            adapter.showList(viewModel.noteList)
        }
        else {
            adapter.showList(viewModel.filterList(newText))
        }
        return true
    }
}