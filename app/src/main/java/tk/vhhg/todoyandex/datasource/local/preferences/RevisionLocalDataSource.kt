package tk.vhhg.todoyandex.datasource.local.preferences

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import tk.vhhg.todoyandex.di.Constants
import tk.vhhg.todoyandex.di.LastRevisionPreferences
import javax.inject.Inject

class RevisionLocalDataSource @Inject constructor(
    @LastRevisionPreferences private val lastRevision: SharedPreferences
) : IRevisionLocalDataSource {

    private val key = Constants.SP_KEY_LAST_REVISION

    override fun get(): Int = lastRevision.getInt(key, 0)

    override fun set(value: Int?) {
        Log.d("set revision", value?.toString() ?: "null")
        lastRevision.edit { putInt(key, value ?: get()) }
    }
}