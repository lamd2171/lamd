package com.example.bitconintauto.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitconintauto.R
import com.example.bitconintauto.model.Coordinate

class CoordinateAdapter(
    private var items: MutableList<CoordinateItem>,
    private val onDeleteClicked: (CoordinateItem) -> Unit,
    private val onItemClicked: (CoordinateItem) -> Unit // ✅ 추가된 콜백
) : RecyclerView.Adapter<CoordinateAdapter.ViewHolder>() {

    data class CoordinateItem(
        val type: String,
        val coordinate: Coordinate
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabel: TextView = view.findViewById(R.id.tvLabel)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.coordinate_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val coord = item.coordinate

        holder.tvLabel.text = coord.label.ifBlank { "(${coord.x}, ${coord.y})" }
        holder.tvDetails.text =
            "${item.type} | (${coord.x}, ${coord.y}) | expected: ${coord.expectedValue ?: "-"}"

        holder.btnDelete.setOnClickListener {
            onDeleteClicked(item)
        }

        // ✅ 클릭 시 수정 기능 콜백
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    fun updateList(newList: List<CoordinateItem>) {
        items = newList.toMutableList()
        notifyDataSetChanged()
    }
}
