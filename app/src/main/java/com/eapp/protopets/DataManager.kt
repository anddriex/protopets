package com.eapp.protopets

object DataManager {
    val pets = HashMap<String, PetInfo>()
    val appointments = ArrayList<AppointmentInfo>()

    init {
        initializePets()
        initializeAppointments()
    }

    fun addAppointment(date: Long, startTime: String, endTime: String, petInfo: PetInfo,
                       diagnosis: String): Int {
        val appointment = AppointmentInfo(petInfo, date, startTime, endTime, diagnosis)
        appointments.add(appointment)
        return appointments.lastIndex
    }

    private fun initializePets() {
        var pet = PetInfo("golden1", "Leia Morgana")
        pets[pet.petId] = pet
        pet = PetInfo("schnauzer1", "Chuwi")
        pets[pet.petId] = pet
    }

    private fun initializeAppointments() {
        var pet = pets["golden1"]!!
        var appointment = AppointmentInfo(pet, 1603116124389, "12:30pm",
            "13:00pm", "injection")
        appointments.add(appointment)

        pet = pets["schnauzer1"]!!
        appointment = AppointmentInfo(pet, 1602306000000, "15:00pm",
            "16:00pm", "pills")
        appointments.add(appointment)
    }
}