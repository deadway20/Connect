package com.coder_x.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coder_x.connect.databinding.FragmentToDoBinding


class ToDoFragment : Fragment() {

    private lateinit var binding: FragmentToDoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setupRecyclerView()
//        setupFab()
    }

//    private fun setupRecyclerView() {
//        val todoItems = listOf(
//            TodoItem("change edittext outbox color", "04-23"),
//            TodoItem("fix font size in all topbar", "04-23"),
//            TodoItem("fix view direction in english la...", "04-23"),
//            TodoItem("set profile bottombar ¡co to e...", "04-23"),
//            TodoItem("make graph in calendar Fragme...", "04-23")
//        )
//
//        todoAdapter = TodoAdapter(todoItems, this)
//        binding.todoRecyclerView.apply {
//            adapter = todoAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
//        }
//    }
//
//    private fun setupFab() {
//        binding.fabAddTask.setOnClickListener {
//            showAddTaskDialog()
//        }
//    }
//
//    override fun onTodoClicked(position: Int) {
//        // Handle item click (e.g., edit task)
//        val clickedItem = todoAdapter.currentList[position]
//        Toast.makeText(requireContext(), "Clicked: ${clickedItem.task}", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onTodoChecked(position: Int, isChecked: Boolean) {
//        // Update task completion status
//        val updatedList = todoAdapter.currentList.toMutableList()
//        updatedList[position] = updatedList[position].copy(isCompleted = isChecked)
//        todoAdapter.updateItems(updatedList)
//    }
//
//    private fun showAddTaskDialog() {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Add New Task")
//            .setView(R.layout.dialog_add_task)
//            .setPositiveButton("Add") { dialog, _ ->
//                // Handle adding new task
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
}