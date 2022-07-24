package com.example.haojie_huang_myruns5.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.haojie_huang_myruns5.database.Information
import com.example.haojie_huang_myruns5.database.InformationRepository

class InformationViewModel(private val informationRepository: InformationRepository): ViewModel()
{
    val allEntriesLiveData: LiveData<List<Information>> = informationRepository.allEntries.asLiveData()

    fun insert(information: Information)
    {
        informationRepository.insert(information)
    }

    fun delete(id: Long)
    {
        informationRepository.delete(id)
    }

    fun deleteAll()
    {
        informationRepository.deleteAll()
    }

    fun getCount(): Int
    {
        return informationRepository.getCount()
    }

}

class InformationViewModelFactory(private val repository: InformationRepository): ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(InformationViewModel::class.java))
        {
            return InformationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}