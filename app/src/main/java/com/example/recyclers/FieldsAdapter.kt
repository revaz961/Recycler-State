package com.example.recyclers

import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclers.databinding.ChooserDateLayoutBinding
import com.example.recyclers.databinding.ChooserGenderLayoutBinding
import com.example.recyclers.databinding.InputLayoutBinding
import kotlin.random.Random

class FieldsAdapter(
    private val fieldsState: MutableMap<Int, String>, private val saveState: (Int, String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val INPUT_TYPE = 1
        private const val CHOOSER_DATE_TYPE = 2
        private const val CHOOSER_GENDER_TYPE = 3
    }

    private val fields = mutableListOf<Field>()

    fun setFields(items: MutableList<Field>) {
        fields.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            INPUT_TYPE -> {
                InputViewHolder(
                    InputLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            CHOOSER_DATE_TYPE -> {
                ChooserDateViewHolder(
                    ChooserDateLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ChooserGenderViewHolder(
                    ChooserGenderLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InputViewHolder -> holder.bind()
            is ChooserDateViewHolder -> holder.bind()
            is ChooserGenderViewHolder -> holder.bind()
        }
    }

    override fun getItemCount() = fields.size

    override fun getItemViewType(position: Int): Int {
        return when (fields[position].hint) {
            "Birthday" -> CHOOSER_DATE_TYPE
            "Gender" -> CHOOSER_GENDER_TYPE
            else -> INPUT_TYPE
        }
    }

    inner class InputViewHolder(private var binding: InputLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    saveState(fields[adapterPosition].fieldId, s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }


                override fun afterTextChanged(s: Editable?) {}
            })

            if (fields[adapterPosition].keyboard != null)
                binding.root.inputType = when (fields[adapterPosition].keyboard) {
                    "number" -> InputType.TYPE_CLASS_NUMBER
                    else -> InputType.TYPE_CLASS_TEXT
                }
            binding.root.setText(fieldsState[fields[adapterPosition].fieldId])
            binding.root.id = fields[adapterPosition].fieldId
            binding.root.hint = fields[adapterPosition].hint
        }
    }

    inner class ChooserDateViewHolder(private var binding: ChooserDateLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.root.id = fields[adapterPosition].fieldId
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.root.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                    saveState(fields[adapterPosition].fieldId, "$year/$monthOfYear/$dayOfMonth")
                }
            }
            if (fieldsState.containsKey(fields[adapterPosition].fieldId)) {
                var date = fieldsState[fields[adapterPosition].fieldId]?.split("/")
                binding.root.updateDate(date!![0].toInt(), date!![1].toInt(), date!![2].toInt())
            }
        }
    }

    inner class ChooserGenderViewHolder(private var binding: ChooserGenderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.root.forEach { button ->
                if (button is RadioButton) {
                    button.setOnClickListener {
                        saveState(fields[adapterPosition].fieldId, button.text.toString())
                    }
                }
            }
            binding.root.id = fields[adapterPosition].fieldId
            if (fieldsState.containsKey(fields[adapterPosition].fieldId)) {
                var checked = binding.root.children.find {
                    it is RadioButton && it.text == fieldsState[fields[adapterPosition].fieldId]
                } as RadioButton
                checked.isChecked = true
            }
        }
    }
}
