package hu.bme.aut.android.scanmynotes.ui.notelist

import hu.bme.aut.android.scanmynotes.ui.notelist.models.UiNotePreview

sealed class NoteListViewState

object Initial: NoteListViewState()
object Loading: NoteListViewState()
data class NotesReady(val notes: List<UiNotePreview>) : NoteListViewState()
data class NewNoteReady(val detectedText: String) : NoteListViewState()