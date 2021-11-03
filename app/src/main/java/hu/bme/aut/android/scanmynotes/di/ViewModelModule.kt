package hu.bme.aut.android.scanmynotes.di

import androidx.lifecycle.ViewModel
import co.zsmb.rainbowcake.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hu.bme.aut.android.scanmynotes.ui.newcategory.NewCategoryViewModel
import hu.bme.aut.android.scanmynotes.ui.newnote.NewNoteViewModel
import hu.bme.aut.android.scanmynotes.ui.notedetails.NoteDetailsViewModel
import hu.bme.aut.android.scanmynotes.ui.notelist.NoteListViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NoteListViewModel::class)
    abstract fun bindNoteListViewModel(viewModel: NoteListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewNoteViewModel::class)
    abstract fun bindNewNoteViewModel(viewModel: NewNoteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewCategoryViewModel::class)
    abstract fun bindNewCategoryViewModel(viewModel: NewCategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoteDetailsViewModel::class)
    abstract fun bindNoteDetailsViewModel(viewModel: NoteDetailsViewModel): ViewModel
}
