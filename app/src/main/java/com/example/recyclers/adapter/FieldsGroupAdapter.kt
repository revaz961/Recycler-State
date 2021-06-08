package com.example.recyclers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclers.databinding.FieldsLayoutBinding
import com.example.recyclers.model.Field

class FieldsGroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var fieldsGroup = mutableListOf<MutableList<Field>>()
    private val fieldState = mutableMapOf<Int, MutableMap<Int, String>>()
    fun setData(items: MutableList<MutableList<Field>>) {
        fieldsGroup.addAll(items)
        notifyDataSetChanged()
    }

    fun getFieldState(): MutableMap<Int, String>{
        var state = mutableMapOf<Int,String>()
        fieldState.forEach{ entry ->
            entry.value.forEach{
                state[it.key] = it.value
            }
        }
        return state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            FieldsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind()
        }
    }

    override fun getItemCount() = fieldsGroup.size

    inner class ViewHolder(private var binding: FieldsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (!fieldState.containsKey(adapterPosition))
                fieldState[adapterPosition] = mutableMapOf()
            binding.rvFields.layoutManager = LinearLayoutManager(binding.root.context)
            var adapter = FieldsAdapter(fieldState[adapterPosition]!!) { key, value ->
                fieldState[adapterPosition]!![key] = value
            }
            binding.rvFields.adapter = adapter
            adapter.setFields(fieldsGroup[adapterPosition])
        }
    }
}