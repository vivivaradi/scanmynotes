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
            builder.setTitle(getString(R.string.sort_dialog_title))
                .setSingleChoiceItems(list, selectedItem) { _, selected ->
                    val sorting = SortOptions.values()[selected]
                    val data = Pair(getString(R.string.sort_dialog_selected_option_key), sorting)
                    selectedItem = selected
                    parentFragmentManager.setFragmentResult(getString(R.string.sort_dialog_result_requestkey), bundleOf(data))
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exception_message_activity_cannot_be_null))
    }
}