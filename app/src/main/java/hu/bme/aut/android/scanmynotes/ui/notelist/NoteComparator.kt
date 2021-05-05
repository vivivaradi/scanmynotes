package hu.bme.aut.android.scanmynotes.ui.notelist

import androidx.recyclerview.widget.DiffUtil
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote

object NoteComparator : DiffUtil.ItemCallback<DomainNote>() {


    override fun areItemsTheSame(oldItem: DomainNote, newItem: DomainNote): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DomainNote, newItem: DomainNote): Boolean {
        return oldItem == newItem
    }
}