package com.eapp.protopets

import org.junit.Assert.*
import org.junit.Test

class DataManagerTest {

    @Test
    fun addAppointment() {
        val date = 1602478800000
        val startTime = "10:00AM"
        val endTime = "11:00AM"
        val pet = DataManager.pets["golden1"]!!
        val diagnosis = "periodic injection"

        val index = DataManager.addAppointment(date, startTime, endTime, pet, diagnosis)
        val appointment = DataManager.appointments[index]

        assertEquals(date, appointment.date)
        assertEquals(startTime, appointment.startTime)
        assertEquals(endTime, appointment.endTime)
        assertEquals(pet, appointment.pet)
        assertEquals(diagnosis, appointment.diagnosis)
    }
}