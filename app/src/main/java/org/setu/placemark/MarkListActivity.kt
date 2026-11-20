package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.placemark.R
import org.setu.placemark.models.PlacemarkModel
import androidx.appcompat.app.AlertDialog
import timber.log.Timber

class MarkListActivity : AppCompatActivity() {

    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mark_list)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val recyclerView =
            findViewById<RecyclerView>(R.id.marksRecyclerView)

        adapter = PlacedMarkAdapter(
            marks = AppData.placemarks.findAll(),
            listener = object : OnMarkListener {
                override fun onMarkClick(mark: PlacemarkModel) {
                    editMark(mark)
                }

                override fun onMarkLongClick(mark: PlacemarkModel): Boolean {
                    AlertDialog.Builder(this@MarkListActivity)
                        .setTitle("Delete this mark?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete") { _, _ ->
                            Timber.d("Deleting placemark id=%s", mark.id)
                            AppData.placemarks.delete(mark.id)
                            adapter.updateMarks(AppData.placemarks.findAll())
                        }
                        .show()

                    return true
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.returnButton).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateMarks(AppData.placemarks.findAll())
        }
    }

    private fun editMark(mark: PlacemarkModel) {
        Timber.d("Editing placemark id=%s", mark.id)
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", mark.id)
        startActivity(intent)
    }
}