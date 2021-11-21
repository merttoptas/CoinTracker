package com.merttoptas.cointracker.features.coinlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.databinding.LayoutCoinListItemBinding

class CoinListAdapter(private val listener: OnClickListener) :
    ListAdapter<CoinResponse, CoinListAdapter.CoinListViewHolder>(CoinListDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinListViewHolder {
        return CoinListViewHolder(
            LayoutCoinListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CoinListViewHolder, position: Int) {
        holder.bind(getItem(position), listener = listener)
    }

    class CoinListDiffUtil : DiffUtil.ItemCallback<CoinResponse>() {
        override fun areItemsTheSame(oldItem: CoinResponse, newItem: CoinResponse): Boolean {
            return true
        }

        override fun areContentsTheSame(
            oldItem: CoinResponse,
            newItem: CoinResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class CoinListViewHolder(val binding: LayoutCoinListItemBinding) :
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