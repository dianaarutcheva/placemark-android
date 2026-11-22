package org.setu.placemark

import android.content.Context
import org.setu.placemark.models.PlacemarkMemStore
import java.io.File

object AppData {
    lateinit var placemarks: PlacemarkMemStore
        private set

    fun initialize(context: Context) {
        if (::placemarks.isInitialized) return

        val file = File(context.filesDir, "place_list.json")
        placemarks = PlacemarkMemStore(file)
    }
}