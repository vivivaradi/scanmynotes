package hu.bme.aut.android.scanmynotes.ui.categorydetails

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.scanmynotes.R
import java.lang.IllegalStateException

class DeleteWarningDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(getString(R.string.delete_confirm_dialog_title))
                .setMessage(getString(R.string.delete_confirm_dialog_message))
                .setPositiveButton(getString(R.string.warning_dialog_confirm_button_text), DialogInterface.OnClickListener { dialogInterface, id ->
                    parentFragmentManager.setFragmentResult(getString(R.string.delete_dialog_result_requestkey), bundleOf())
                })
                .setNegativeButton(getString(R.string.warning_dialog_cancel_button_text), DialogInterface.OnClickListener { _, _ ->
                    dismiss()
                })
            builder.create()
        }?: throw IllegalStateException(getString(R.string.exception_message_activity_cannot_be_null))
    }
}