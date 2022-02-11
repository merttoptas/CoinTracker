package com.merttoptas.cointracker.features.coinlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.LayoutCoinListItemBinding
import com.merttoptas.cointracker.databinding.LayoutCoinListLoadingBinding
import com.merttoptas.cointracker.domain.viewstate.coinlist.CoinItemViewItem
import com.merttoptas.cointracker.domain.viewstate.coinlist.CoinListViewItem
import com.merttoptas.cointracker.domain.viewstate.coinlist.CoinListViewType

class CoinListAdapter(private val listener: OnClickListener) :
    ListAdapter<CoinListViewItem, RecyclerView.ViewHolder>(CoinListDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.layout_coin_list_item -> {
                CoinListViewHolder(
                    LayoutCoinListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.layout_coin_list_loading -> {
                CoinListLoadingViewHolder(
                    LayoutCoinListLoadingBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            else -> TODO("The view that the viewType is $viewType has not implemented yet")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.layout_coin_list_item -> {
                (holder as? CoinListViewHolder)?.let { viewHolder ->
                    (getItem(position) as? CoinItemViewItem)?.let { data ->
                        viewHolder.bind(data, listener = listener)
                    }

                }
            }
        }
    }

    class CoinListDiffUtil : DiffUtil.ItemCallback<CoinListViewItem>() {
        override fun areItemsTheSame(
            oldItem: CoinListViewItem,
            newItem: CoinListViewItem
        ): Boolean {
            if (oldItem.viewType == CoinListViewType.COIN_LIST_LOADING && newItem.viewType == CoinListViewType.COIN_LIST_LOADING)
                return true
            else if (oldItem.viewType == CoinListViewType.COIN_LIST && newItem.viewType == CoinListViewType.COIN_LIST)
                return true
            return false
        }

        override fun areContentsTheSame(
            oldItem: CoinListViewItem,
            newItem: CoinListViewItem
        ): Boolean {
            return true
        }
    }

    inner class CoinListViewHolder(val binding: LayoutCoinListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataHolder: CoinItemViewItem, listener: OnClickListener) {
            binding.dataHolder = dataHolder
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    inner class CoinListLoadingViewHolder(val binding: LayoutCoinListLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).viewType) {
            CoinListViewType.COIN_LIST -> R.layout.layout_coin_list_item
            CoinListViewType.COIN_LIST_LOADING -> R.layout.layout_coin_list_loading
        }
    }

}

interface OnClickListener {
    fun onClick(id: String)
}