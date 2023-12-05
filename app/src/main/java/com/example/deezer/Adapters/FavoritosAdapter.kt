package com.example.deezer.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.deezer.Favoritos.Result
import com.example.deezer.R

class FavoritosAdapter(private val itemsList: MutableList<Result>) : RecyclerView.Adapter<FavoritosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_row_item, parent, false)
        return FavoritosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoritosViewHolder, position: Int) {
        val favoritos = itemsList[position]
        holder.bind(favoritos, holder.itemView.context)
    }

    override fun getItemCount() = itemsList.size

    fun updateList(newData: List<Result>) {
        itemsList.clear()
        itemsList.addAll(newData)
        notifyDataSetChanged()
    }

}