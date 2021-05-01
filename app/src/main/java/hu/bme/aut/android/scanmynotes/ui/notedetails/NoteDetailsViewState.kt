package hu.bme.aut.android.scanmynotes.ui.notedetails

import hu.bme.aut.android.scanmynotes.domain.models.DomainNote

sealed class NoteDetailsViewState

object Initial: NoteDetailsViewState()

object Loading: NoteDetailsViewState()

data class Viewing(val note: DomainNote): NoteDetailsViewState()

data class Editing(val note: DomainNote): NoteDetailsViewState()

object NoteDeleted: NoteDetailsViewState()