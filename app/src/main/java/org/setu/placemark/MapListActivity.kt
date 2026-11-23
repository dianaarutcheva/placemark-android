package org.setu.placemark

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.placemark.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.setu.placemark.models.PlacemarkModel
import android.widget.Button

class MapListActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppData.initialize(this)
        Configuration.getInstance().userAgentValue =
            "DianaArutchevaPlacemark/1.0 (+https://github.com/dianaarutcheva/placemark-android)"

        setContentView(R.layout.activity_map_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        mapView = findViewById(R.id.mapList)
        txtTitle = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace = findViewById(R.id.mapImageView)
        findViewById<Button>(R.id.backToMenuButton).setOnClickListener {
            finish()
        }
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.overlays.add(CopyrightOverlay(this))

        loadMarkers()
    }

    private fun loadMarkers() {
        val placemarks = AppData.placemarks.findAll()
        if (placemarks.isEmpty()) {
            txtTitle.text = "No saved placemarks"
            txtDescription.text = "Add a placemark from the main menu to see it here."
            imgPlace.setImageDrawable(null)
            return
        }
        placemarks.forEach { placemark ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(placemark.x, placemark.y)
                title = placemark.title
                snippet = placemark.description
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                setOnMarkerClickListener { _, _ ->
                    displayPlace(placemark)
                    true
                }
            }

            mapView.overlays.add(marker)
        }

        placemarks.firstOrNull()?.let { first ->
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(first.x, first.y))
        }

        mapView.invalidate()
    }

    private fun displayPlace(placemark: PlacemarkModel) {
        txtTitle.text = placemark.title
        txtDescription.text = placemark.description

        val imageUri = placemark.imageUri
        if (imageUri != null) {
            imgPlace.setImageURI(Uri.parse(imageUri))
        } else {
            imgPlace.setImageDrawable(null)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
    }
}