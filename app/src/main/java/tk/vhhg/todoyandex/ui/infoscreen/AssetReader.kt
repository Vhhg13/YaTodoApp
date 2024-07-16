package tk.vhhg.todoyandex.ui.infoscreen

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class AssetReader(private val context: Context) {

    fun read(filename: String): JSONObject {
        val data = BufferedReader(InputStreamReader(context.assets.open(filename))).lines()
            .collect(Collectors.joining("\n"))
        return JSONObject(data)
    }
}
