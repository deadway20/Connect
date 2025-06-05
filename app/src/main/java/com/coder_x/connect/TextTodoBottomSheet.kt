package com.coder_x.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.coder_x.connect.databinding.BottomsheetTextTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TextTodoBottomSheet : BottomSheetDialogFragment() {

    // [START Binding Section]
    private var _binding: BottomsheetTextTodoBinding? = null
    private val binding get() = _binding!!
    // [END Binding Section]

    // [START Callbacks Section]
    var onTodoAdded: ((title: String) -> Unit)? = null
    var onTodoUpdated: ((todoId: String, title: String) -> Unit)? = null

    private var currentTodoId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetTextTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAddButton()
    }

    fun setTodoForEditing(todoId: String, title: String) {
        currentTodoId = todoId
        if (_binding != null) {
            binding.textTitleInput.setText(title)
        }
    }

    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            val title = binding.textTitleInput.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), R.string.title_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentTodoId != null) {
                onTodoUpdated?.invoke(currentTodoId!!, title)
            } else {
                onTodoAdded?.invoke(title)
            }
            dismiss()
        }
    }

    // [START Lifecycle Section]
    override fun getTheme(): Int = R.style.Theme_Connect_BottomSheet_Material3

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // [END Lifecycle Section]
}