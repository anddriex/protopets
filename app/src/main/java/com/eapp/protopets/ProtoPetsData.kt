package com.eapp.protopets

data class PetInfo(val petId: String, val name: String) {
    override fun toString(): String {
        return name
    }
}

data class AppointmentInfo(var pet: PetInfo? = null, var date: Long? = null,
                           var startTime: String? = null, var endTime: String? = null,
                           var diagnosis: String? = null)