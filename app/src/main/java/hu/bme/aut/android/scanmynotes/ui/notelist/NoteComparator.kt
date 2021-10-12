package hu.bme.aut.android.scanmynotes.ui.notelist

import androidx.recyclerview.widget.DiffUtil
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import hu.bme.aut.android.scanmynotes.domain.models.ListItem

object NoteComparator : DiffUtil.ItemCallback<ListItem>() {


    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.title == newItem.title && oldItem.behaviour == newItem.behaviour
    }
}