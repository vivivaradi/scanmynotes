package hu.bme.aut.android.scanmynotes.ui.notelist

import hu.bme.aut.android.scanmynotes.domain.models.ListItem

sealed class NoteListViewState

object Loading: NoteListViewState()
data class ListLoaded(val noteList: List<ListItem>): NoteListViewState()
data class Error(val message: String): NoteListViewState()