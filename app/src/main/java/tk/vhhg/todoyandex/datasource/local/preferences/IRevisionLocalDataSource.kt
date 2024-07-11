package tk.vhhg.todoyandex.datasource.local.preferences

interface IRevisionLocalDataSource {
    fun get(): Int
    fun set(value: Int?)
}