package com.example.bitconintauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitconintauto.R
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ui.CoordinateAdapter.CoordinateItem
import com.example.bitconintauto.util.CoordinateManager

class CoordinateListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoordinateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinate_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CoordinateAdapter(
            getAllCoordinates(),
            onDeleteClicked = { item -> removeCoordinate(item) },
            onItemClicked = { item -> showEditDialog(item) } // ✅ 클릭 수정 기능 연결
        )

        recyclerView.adapter = adapter
    }

    private fun getAllCoordinates(): MutableList<CoordinateItem> {
        val result = mutableListOf<CoordinateItem>()
        val allTypes = listOf("primary", "click", "copy", "paste", "final")
        allTypes.forEach { type ->
            val list = CoordinateManager.get(type)
            list.forEach { coord ->
                result.add(CoordinateItem(type, coord))
            }
        }
        return result
    }

    private fun removeCoordinate(item: CoordinateItem) {
        val existing = CoordinateManager.get(item.type).toMutableList()
        existing.remove(item.coordinate)
        CoordinateManager.register(item.type, existing)

        Toast.makeText(this, "삭제됨: ${item.coordinate.label}", Toast.LENGTH_SHORT).show()
        adapter.updateList(getAllCoordinates())
    }

    // ✅ 좌표 수정 다이얼로그
    private fun showEditDialog(item: CoordinateItem) {
        val coord = item.coordinate
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_coordinate, null)

        val etLabel = view.findViewById<EditText>(R.id.etLabel)
        val etExpected = view.findViewById<EditText>(R.id.etExpectedValue)
        val etWidth = view.findViewById<EditText>(R.id.etWidth)
        val etHeight = view.findViewById<EditText>(R.id.etHeight)

        etLabel.setText(coord.label)
        etExpected.setText(coord.expectedValue ?: "")
        etWidth.setText(coord.width.toString())
        etHeight.setText(coord.height.toString())

        AlertDialog.Builder(this)
            .setTitle("좌표 수정")
            .setView(view)
            .setPositiveButton("저장") { _, _ ->
                val updated = coord.copy(
                    label = etLabel.text.toString(),
                    expectedValue = etExpected.text.toString().ifBlank { null },
                    width = etWidth.text.toString().toIntOrNull() ?: coord.width,
                    height = etHeight.text.toString().toIntOrNull() ?: coord.height
                )

                val list = CoordinateManager.get(item.type).toMutableList()
                val index = list.indexOfFirst { it == coord }
                if (index >= 0) {
                    list[index] = updated
                    CoordinateManager.register(item.type, list)
                    adapter.updateList(getAllCoordinates())
                    Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
