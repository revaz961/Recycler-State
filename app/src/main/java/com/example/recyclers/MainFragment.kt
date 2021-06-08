package com.example.recyclers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclers.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null
    private var adapter: FieldsGroupAdapter? = null
    private val mainViewModel: MainViewModel by viewModels()
    private val users = mutableListOf<User>()
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private val phonePattern =
        "[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})?"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = FragmentMainBinding.inflate(inflater, container, false)
            init()
        }
        return binding!!.root
    }

    private fun init() {
        binding!!.btnRegister.setOnClickListener {
            register()
        }
        mainViewModel.init()
        binding!!.rvFieldsGroup.layoutManager = LinearLayoutManager(requireActivity())
        adapter = FieldsGroupAdapter()
        binding!!.rvFieldsGroup.adapter = adapter
        observes()
    }

    private fun observes() {
        mainViewModel.mainLiveData.observe(viewLifecycleOwner, {
            adapter!!.setData(it)
        })
    }

    private fun register() {
        val fields = mainViewModel.getFields()
        val fieldsState = adapter!!.getFieldState()
        if (validate(fieldsState, fields)) {
            val user = User()
            fields.forEach {
                when (it.hint) {
                    "UserName" -> {
                        if (view is EditText)
                            user.userName = fieldsState[it.fieldId]
                    }
                    "Email" -> {
                        if (view is EditText)
                            user.email = fieldsState[it.fieldId]!!
                    }
                    "phone" -> {
                        if (view is EditText)
                            user.email = fieldsState[it.fieldId]!!
                    }
                    "Full Name" -> {
                        if (view is EditText)
                            user.fullName = fieldsState[it.fieldId]!!
                    }
                    "Jemali" -> {
                        if (view is EditText)
                            user.name = fieldsState[it.fieldId]
                    }
                    "Birthday" -> {
                        if (view is DatePicker)
                            user.birthDay = fieldsState[it.fieldId]
                    }
                    "Gender" -> {
                        if (view is RadioGroup)
                            user.gender = fieldsState[it.fieldId]
                    }
                }
            }
            users.add(user)
        }
    }


    private fun validate(fieldsState: Map<Int, String>, fields: List<Field>): Boolean {
        var valid = true
        var message = ""
        fields.forEach { field ->
            val value = fieldsState[field.fieldId]
            when (field.hint) {
                "Full Name" -> {
                    if (value!!.isEmpty()) {
                        valid = false
                        message += ", ${field.hint}"
                    }
                }
                "Email" -> {
                    if (value!!.isEmpty() || !value.matches(emailPattern.toRegex())
                    ) {
                        valid = false
                        message += ", ${field.hint}"
                    }
                }
                "phone" -> {
                    if (value!!.isEmpty() || !value.matches(phonePattern.toRegex())
                    ) {
                        valid = false
                        message += ", ${field.hint}"
                    }
                }
            }
        }
        if(message.isNotEmpty())
            showMessage(message)
        return valid
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding!!.root, "invalid input $message", Snackbar.LENGTH_LONG).show()
    }
}