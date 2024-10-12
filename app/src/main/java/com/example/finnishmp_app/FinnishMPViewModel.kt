package com.example.finnishmp_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finnishmp_app.db.PMDatabase
import com.example.finnishmp_app.db.ParliamentMember
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
/*
 Muche Berhanu 2219580
 This class is a ViewModel class that serves as a mediator between the UI and the data layer in the Finnish
 Member of Parliament (MP) application. It is responsible for managing the state of parliament members,
 facilitating data operations, and providing the UI with the necessary data to display.
 */
class FinnishMPViewModel : ViewModel() {
    private val dao = PMDatabase.getInstance().memberDao()
    private val _members = dao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Using StateFlow for latest member states
    private val _member = MutableStateFlow<ParliamentMember?>(null)
    val member: StateFlow<ParliamentMember?> get() = _member

    private val _nextMember = MutableStateFlow<ParliamentMember?>(null)
    val nextMember: StateFlow<ParliamentMember?> get() = _nextMember

    private val _previousMember = MutableStateFlow<ParliamentMember?>(null)
    val previousMember: StateFlow<ParliamentMember?> get() = _previousMember

    // Update the current member and emit it
    fun updateMember(updatedMember: ParliamentMember?) {
        viewModelScope.launch {
            if (updatedMember != null) {
                dao.update(updatedMember)
                _member.value = updatedMember
            }
        }
    }

    // Set the current member based on the hetekaId
    fun setMember(hetekaId: Int?) {
        viewModelScope.launch {
            val currentMembers = _members.first()
            val newMember = if (hetekaId == null) {
                currentMembers.firstOrNull()
            } else {
                currentMembers.firstOrNull { it.hetekaId == hetekaId }
            }

            _member.value = newMember

            // Update next and previous member states
            val index = currentMembers.indexOf(newMember)

            _nextMember.value = if (currentMembers.isNotEmpty()) {
                currentMembers[(index + 1) % currentMembers.size]
            } else null

            _previousMember.value = if (currentMembers.isNotEmpty()) {
                currentMembers[(index - 1 + currentMembers.size) % currentMembers.size]
            } else null
        }
    }
}
