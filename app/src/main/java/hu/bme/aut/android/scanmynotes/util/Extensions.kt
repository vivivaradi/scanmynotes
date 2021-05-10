package hu.bme.aut.android.scanmynotes.util

import android.Manifest
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.vmadalin.easypermissions.EasyPermissions

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
    if (text.isEmpty()) {
        error = "Field cannot be empty!"
        return false
    }
    return true
}

fun EditText.validateEmailContent(): Boolean {
    if (text.isEmpty() || !text.contains('@') || !text.contains('.')) {
        error = "Email has to be a valid email format!"
        return false
    }
    return true
}

fun EditText.validatePasswordContent(): Boolean {
    if (text.length < 6) {
        error = "Password has to be at least 6 characters long!"
        return false
    }
    return true
}