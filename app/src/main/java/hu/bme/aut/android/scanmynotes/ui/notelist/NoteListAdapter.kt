package hu.bme.aut.android.scanmynotes.ui.notelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import hu.bme.aut.android.scanmynotes.R
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import kotlinx.android.synthetic.main.note_row.view.*

class NoteListAdapter : ListAdapter<DomainNote, NoteListAdapter.NoteListViewHolder>(NoteComparator) {

    var listener: Listener? = null


    inner class NoteListViewHolder(noteView: View) : RecyclerView.ViewHolder(noteView) {
        val tvNoteTitle: TextView = noteView.tvNoteTitle
        val linearLayout: LinearLayout = noteView.linearLayout

        var note: DomainNote? = null

        init {
            linearLayout.setOnClickListener {
                note?.let { listener?.onNoteClicked(it) }
            }
        }
    }

    interface Listener {
        fun onNoteClicked(note: DomainNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_row, parent, false)
        return NoteListViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val note = getItem(position)

        holder.note = note
        holder.tvNoteTitle.text = note.title
    }


}