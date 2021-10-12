package hu.bme.aut.android.scanmynotes.ui.notelist.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.databinding.NoteRowBinding
import hu.bme.aut.android.scanmynotes.domain.models.Note

class NoteItem(val note: Note): BindableItem<NoteRowBinding>() {
    override fun bind(viewBinding:NoteRowBinding, position: Int) {
        viewBinding.tvNoteTitle.text = note.title
    }

    override fun getLayout(): Int {
        return R.layout.note_row
    }

    override fun initializeViewBinding(view: View): NoteRowBinding {
        return NoteRowBinding.bind(view)
    }
}