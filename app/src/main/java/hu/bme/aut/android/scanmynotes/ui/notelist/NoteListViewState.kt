package hu.bme.aut.android.scanmynotes.ui.notelist

import hu.bme.aut.android.scanmynotes.domain.models.Category
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote
import hu.bme.aut.android.scanmynotes.domain.models.ListItem
import hu.bme.aut.android.scanmynotes.domain.models.Note

sealed class NoteListViewState

object Initial: NoteListViewState()
object Loading: NoteListViewState()
data class Success(val noteList: List<ListItem>): NoteListViewState()
data class Error(val message: String): NoteListViewState()