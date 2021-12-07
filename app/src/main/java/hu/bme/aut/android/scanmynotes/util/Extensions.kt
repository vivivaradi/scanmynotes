package hu.bme.aut.android.scanmynotes.util

import android.Manifest
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vmadalin.easypermissions.EasyPermissions
import hu.bme.aut.android.scanmynotes.data.models.Result
import hu.bme.aut.android.scanmynotes.ui.notelist.NoteListViewState

fun Fragment.hasCameraPermission() =
    com.vmadalin.easypermissions.EasyPermissions.hasPermissions(
        requireContext(),
        Manifest.permission.CAMERA
    )

fun Fragment.requestCameraPermission(permissionCode: Int) {
    EasyPermissions.requestPermissions(
        requireActivity(),
        "This application needs camera permission to work.",
        permissionCode,
        Manifest.permission.CAMERA
    )
}

fun EditText.validateTextContent(): Boolean {
    if (text.toString().isEmpty()) {
        error = "Field cannot be empty!"
        return false
    }
    return true
}

fun EditText.validateEmailContent(): Boolean {
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
        error = "Email has to be a valid email format!"
        return false
    }
    return true
}

fun EditText.validatePasswordContent(): Boolean {
    if (text.toString().length < 6) {
        error = "Password has to be at least 6 characters long!"
        return false
    }
    return true
}

fun TextView.validateTextContent(): Boolean {
    if (text.toString().isEmpty()) {
        error = "Field cannot be empty!"
        return false
    }
    return true
}