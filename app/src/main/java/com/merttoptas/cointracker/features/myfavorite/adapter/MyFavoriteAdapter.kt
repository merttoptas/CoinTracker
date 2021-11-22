package com.merttoptas.cointracker.features.myfavorite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.databinding.LayoutMyFavoriteListItemBinding

class MyFavoriteAdapter(private val listener: OnClickListener) :
    ListAdapter<CoinResponse, MyFavoriteAdapter.MyFavoriteListViewHolder>(MyFavoriteDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteListViewHolder {
        return MyFavoriteListViewHolder(
            LayoutMyFavoriteListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyFavoriteListViewHolder, position: Int) {
        holder.bind(getItem(position), listener = listener)
    }

    class MyFavoriteDiffUtil : DiffUtil.ItemCallback<CoinResponse>() {
        override fun areItemsTheSame(oldItem: CoinResponse, newItem: CoinResponse): Boolean {
            return oldItem.coinId == newItem.coinId
        }

        override fun areContentsTheSame(
            oldItem: CoinResponse,
            newItem: CoinResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class MyFavoriteListViewHolder(val binding: LayoutMyFavoriteListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataHolder: CoinResponse, listener: OnClickListener) {
            binding.dataHolder = dataHolder
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}

interface OnClickListener {
    fun onClick(id: String)
}