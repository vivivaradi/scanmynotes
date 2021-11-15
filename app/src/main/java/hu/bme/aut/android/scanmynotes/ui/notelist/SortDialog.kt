package hu.bme.aut.android.scanmynotes.ui.notelist

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.scanmynotes.R
import java.lang.IllegalStateException

class SortDialog(var selectedItem: Int): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val list = resources.getStringArray(R.array.sortingOptions)
            builder.setTitle("Sort by")
                .setSingleChoiceItems(list, selectedItem) { _, selected ->
                    val sorting = SortOptions.values()[selected]
                    val data = Pair("chosenOption", sorting)
                    selectedItem = selected
                    parentFragmentManager.setFragmentResult("SortOption", bundleOf(data))
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}