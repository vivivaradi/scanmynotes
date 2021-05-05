package hu.bme.aut.android.scanmynotes.ui.notelist

import hu.bme.aut.android.scanmynotes.domain.models.DomainNote

sealed class NoteListViewState

object Initial: NoteListViewState()
object Loading: NoteListViewState()
data class NotesReady(val noteList: List<DomainNote>) : NoteListViewState()
data class NewNoteReady(val detectedText: String) : NoteListViewState()