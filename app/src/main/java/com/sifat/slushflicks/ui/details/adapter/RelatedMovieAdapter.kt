package com.sifat.slushflicks.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.sifat.slushflicks.R
import com.sifat.slushflicks.databinding.ItemRelatedMovieBinding
import com.sifat.slushflicks.ui.details.adapter.diffutils.MovieDiffUtils
import com.sifat.slushflicks.ui.details.adapter.viewholder.MovieViewHolder
import com.sifat.slushflicks.ui.home.adapter.model.ShowListModel

class RelatedMovieAdapter : ListAdapter<ShowListModel, MovieViewHolder>(MovieDiffUtils()) {

    lateinit var onMovieClickedListener: MovieViewHolder.OnMovieClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemRelatedMovieBinding>(
                inflater,
                R.layout.item_related_movie,
                parent,
                false
            )
        return MovieViewHolder(binding, onMovieClickedListener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}