package tk.vhhg.todoyandex

import android.app.Application
import tk.vhhg.todoyandex.repo.TodoItemsFakeRepository

class App: Application(){
    val repo by lazy { TodoItemsFakeRepository() }
}