package com.example.haojie_huang_myruns5.database

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow


class InformationRepository(private val informationDatabaseDao: InformationDatabaseDao)
{
    val allEntries: Flow<List<Information>> = informationDatabaseDao.getAllEntries()

    fun insert(information: Information)
    {
        CoroutineScope(Dispatchers.IO).launch{
            informationDatabaseDao.insertEntry(information)
        }
    }

    fun delete(id: Long)
    {
        CoroutineScope(Dispatchers.IO).launch {
            informationDatabaseDao.deleteEntry(id)
        }
    }

    fun deleteAll()
    {
        CoroutineScope(Dispatchers.IO).launch {
            informationDatabaseDao.deleteAll()
        }
    }

    fun getCount() = runBlocking {
        val count = async {informationDatabaseDao.getCount()}
        count.start()
        count.await()
    }
}