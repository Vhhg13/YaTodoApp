package tk.vhhg.todoyandex.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkerFactory
import dagger.BindsInstance
import dagger.Component
import tk.vhhg.todoyandex.ui.edittask.EditTaskFragmentComponent
import tk.vhhg.todoyandex.ui.todolist.TodoListFragmentComponent

@Component(modules = [AppModule::class])
@TodoAppScope
interface ApplicationComponent {
    @Component.Factory
    interface Factory{ fun create(@BindsInstance ctx: Context): ApplicationComponent }

    fun getTodoListFragmentComponent(): TodoListFragmentComponent
    fun getEditTaskFragmentComponent(): EditTaskFragmentComponent

    fun getWorkerFactory(): WorkerFactory

    @LastRevisionPreferences
    fun getPreferences(): SharedPreferences
}