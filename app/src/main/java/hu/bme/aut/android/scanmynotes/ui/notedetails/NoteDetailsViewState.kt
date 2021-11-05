package hu.bme.aut.android.scanmynotes.ui.notedetails

import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.Note

sealed class NoteDetailsViewState

object Initial: NoteDetailsViewState()
object Loading: NoteDetailsViewState()
data class Success(val data: List<Category>): NoteDetailsViewState()
data class Viewing(val note: Note): NoteDetailsViewState()
data class Editing(val note: Note): NoteDetailsViewState()
data class Failure(val message: String): NoteDetailsViewState()
