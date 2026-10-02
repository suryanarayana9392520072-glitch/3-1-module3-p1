package project.handson1.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class FilePickerHelper(
    activity: AppCompatActivity,
    private val onFileSelected: (Uri?) -> Unit
) {
    private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onFileSelected(if (result.resultCode == Activity.RESULT_OK) result.data?.data else null)
    }

    fun pickDocument() {
        launcher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "text/plain", "text/markdown", "text/csv", "application/json", "application/xml",
                "text/x-kotlin", "text/x-java-source", "text/x-python"
            ))
        })
    }
}
