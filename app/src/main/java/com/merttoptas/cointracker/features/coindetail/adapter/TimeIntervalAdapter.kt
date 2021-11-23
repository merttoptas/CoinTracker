package com.merttoptas.cointracker.features.coindetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merttoptas.cointracker.data.model.TimeInterval
import com.merttoptas.cointracker.databinding.LayoutTimeIntervalItemBinding

class TimeIntervalAdapter(private val listener: OnClickListener) :
    ListAdapter<TimeInterval, TimeIntervalAdapter.TimeIntervalViewHolder>(MyFavoriteDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeIntervalViewHolder {
        return TimeIntervalViewHolder(
            LayoutTimeIntervalItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TimeIntervalViewHolder, position: Int) {
        holder.bind(getItem(position), listener = listener)
    }

    class MyFavoriteDiffUtil : DiffUtil.ItemCallback<TimeInterval>() {
        override fun areItemsTheSame(oldItem: TimeInterval, newItem: TimeInterval): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: TimeInterval,
            newItem: TimeInterval
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class TimeIntervalViewHolder(val binding: LayoutTimeIntervalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataHolder: TimeInterval, listener: OnClickListener) {
            binding.dataHolder = dataHolder
            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}

interface OnClickListener {
    fun onChanged(timeInterval: TimeInterval)
}