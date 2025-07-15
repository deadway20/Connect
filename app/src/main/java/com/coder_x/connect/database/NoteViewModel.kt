package com.coder_x.connect.database

import android.app.Application
import androidx.lifecycle.switchMap
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.getOrPut


enum class TaskFilter {
    FAVORITE,TODAY, ALL, COMPLETED, INCOMPLETE
}
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    private val _taskFilter = MutableLiveData<TaskFilter>().apply { value = TaskFilter.ALL }
    private val taskFilter: LiveData<TaskFilter> = _taskFilter
    private val _selectedDate = MutableLiveData<String>().apply {
        value = getCurrentDate()
    }
    val selectedDate: LiveData<String> = _selectedDate
    private val _colorMap = mutableMapOf<Long, LiveData<Int>>()
    val filteredTasks: LiveData<List<NoteEntity>>

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        noteRepository = NoteRepository(noteDao)

        filteredTasks = taskFilter.switchMap { filter ->
            when (filter) {
                TaskFilter.FAVORITE -> noteRepository.getFavoriteTasks()
                TaskFilter.ALL -> noteRepository.getAllTasks()
                TaskFilter.COMPLETED -> noteRepository.getCompletedTasks()
                TaskFilter.INCOMPLETE -> noteRepository.getActiveTasks()
                TaskFilter.TODAY -> selectedDate.switchMap { date ->
                    noteRepository.getActiveTasksByDate(date)
                }
            }
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setTaskFilter(filter: TaskFilter) {
        _taskFilter.value = filter
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return sdf.format(Date())
    }

    fun insert(note: NoteEntity) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    fun update(note: NoteEntity) = viewModelScope.launch {
        noteRepository.update(note)
    }

    fun delete(note: NoteEntity) = viewModelScope.launch {
        noteRepository.delete(note)
    }

    fun setTaskCompleted(noteId: Long) = viewModelScope.launch {
        noteRepository.setTaskCompleted(noteId)
    }
    fun getActiveTasksByDate(date: String): LiveData<List<NoteEntity>> {
        return noteRepository.getActiveTasksByDate(date)
    }

    fun setTaskUncompleted(noteId: Long) = viewModelScope.launch {
        noteRepository.setTaskUncompleted(noteId)
    }

    fun deleteCompletedNotes() = viewModelScope.launch {
        noteRepository.deleteCompletedNotes()
    }

    fun deleteNoteById(noteId: Long) = viewModelScope.launch {
        noteRepository.deleteNoteById(noteId)
    }

    fun deleteAllTasks() = viewModelScope.launch {
        noteRepository.deleteAllTasks()
    }

    fun deleteNotesByDate(date: String) = viewModelScope.launch {
        noteRepository.deleteNotesByDate(date)
    }

    fun getTasksCountByDate(date: String): LiveData<Int> {
        return noteRepository.getTasksCountByDate(date)
    }

    fun getActiveTasksCount(): LiveData<Int> {
        return noteRepository.getActiveTasksCount()
    }

    fun setFavoriteTask(noteId: Long, isFavorite: Boolean) = viewModelScope.launch {
        noteRepository.setFavoriteTask(noteId, isFavorite)
    }

    fun getFavoriteTasks(): LiveData<List<NoteEntity>> {
        return noteRepository.getFavoriteTasks()
    }



    fun getOrUpdateColor(itemId: Long, defaultColorGenerator: () -> Int): LiveData<Int> { // Parameter type changed to Long
        return _colorMap.getOrPut(itemId) {
            MediatorLiveData<Int>().apply {
                val dbLiveData = noteRepository.getColorLiveData(itemId) // Ensure repository method also accepts Long
                addSource(dbLiveData) { colorFromDb ->
                    if (colorFromDb != null && colorFromDb != 0) {
                        value = colorFromDb
                    } else {
                        viewModelScope.launch(Dispatchers.IO) {
                            val newColor = defaultColorGenerator()
                            noteRepository.setColorView(newColor, itemId) // Ensure repository method also accepts Long
                        }
                    }
                }
            }
        }
    }


}

