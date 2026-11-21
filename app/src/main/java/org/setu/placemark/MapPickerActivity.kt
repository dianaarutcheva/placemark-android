package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.placemark.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapPickerActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var selectionMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue =
            "DianaArutchevaPlacemark/1.0 (+https://github.com/dianaarutcheva/placemark-android)"
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val confirmButton = findViewById<Button>(R.id.confirmLocationButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val start = GeoPoint(
            intent.getDoubleExtra("latitude", 52.245954),
            intent.getDoubleExtra("longitude", -7.139539)
        )

        val markerTitle = intent.getStringExtra("title").orEmpty()
            .ifBlank { "Selected location" }

        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean =
                selectLocation(point)

            override fun longPressHelper(point: GeoPoint): Boolean =
                selectLocation(point)
        }))

        for (placemark in AppData.placemarks.findAll()) {
            val savedMarker = Marker(map).apply {
                position = GeoPoint(placemark.x, placemark.y)
                title = placemark.title
                snippet = placemark.description
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(savedMarker)
        }

        selectionMarker = Marker(map).apply {
            position = start
            title = markerTitle
            icon = getDrawable(android.R.drawable.btn_star_big_on)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        map.overlays.add(selectionMarker)
        map.overlays.add(CopyrightOverlay(this))

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)

        confirmButton.setOnClickListener {
            val point = selectionMarker.position
            val result = Intent()
                .putExtra("latitude", point.latitude)
                .putExtra("longitude", point.longitude)

            setResult(RESULT_OK, result)
            finish()
        }
    }

    private fun selectLocation(point: GeoPoint): Boolean {
        selectionMarker.position = point
        map.invalidate()
        return true
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        map.onDetach()
        super.onDestroy()
    }
}