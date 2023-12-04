package com.example.deezer.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.deezer.Musica.Result
import com.example.deezer.R

class MusicAdapter(private val itemsList: MutableList<Result>) : RecyclerView.Adapter<MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_row_item, parent, false)
        return MusicViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val musics = itemsList[position]
        holder.bind(musics)
    }

    override fun getItemCount() = itemsList.size

    fun updateList(newData: List<Result>) {
        itemsList.clear()
        itemsList.addAll(newData)
        notifyDataSetChanged()
    }

}