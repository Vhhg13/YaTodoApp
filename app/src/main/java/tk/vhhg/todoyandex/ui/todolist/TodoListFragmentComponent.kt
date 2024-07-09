package tk.vhhg.todoyandex.ui.todolist

import dagger.Subcomponent

@Subcomponent
interface TodoListFragmentComponent {
    fun inject(fragment: TodoListFragment)
}