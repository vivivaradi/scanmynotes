package hu.bme.aut.android.scanmynotes.ui.notelist

import androidx.recyclerview.widget.DiffUtil
import hu.bme.aut.android.scanmynotes.ui.notelist.models.UiNotePreview

object NoteComparator : DiffUtil.ItemCallback<UiNotePreview>() {
    override fun areItemsTheSame(oldItem: UiNotePreview, newItem: UiNotePreview): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: UiNotePreview, newItem: UiNotePreview): Boolean {
        return oldItem == newItem
    }
}