package com.example.haojie_huang_myruns5.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.haojie_huang_myruns5.R
import com.example.haojie_huang_myruns5.viewModel.InformationViewModel
import com.example.haojie_huang_myruns5.viewModel.InformationViewModelFactory
import com.example.haojie_huang_myruns5.database.Information
import com.example.haojie_huang_myruns5.database.InformationDatabase
import com.example.haojie_huang_myruns5.database.InformationDatabaseDao
import com.example.haojie_huang_myruns5.database.InformationRepository

class HistoryFragment : Fragment()
{
    private lateinit var historyListView: ListView

    private lateinit var database: InformationDatabase
    private lateinit var databaseDao: InformationDatabaseDao
    private lateinit var repository: InformationRepository
    private lateinit var viewModel: InformationViewModel
    private lateinit var factory: InformationViewModelFactory
    private lateinit var arrayList: ArrayList<Information>
    private lateinit var arrayAdapter: HistoryListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        historyListView = view.findViewById(R.id.historyList)
        database = InformationDatabase.getInstance(requireActivity())
        databaseDao = database.informationDatabaseDao
        repository = InformationRepository(databaseDao)
        factory = InformationViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(InformationViewModel::class.java)
        arrayList = ArrayList()
        arrayAdapter = HistoryListAdapter(requireActivity(), arrayList)
        historyListView.adapter = arrayAdapter
        viewModel.allEntriesLiveData.observe(requireActivity())
        {
            arrayAdapter.updateList(it)
            arrayAdapter.notifyDataSetChanged()
        }
        return view
    }

    override fun onResume()
    {
        super.onResume()
        arrayAdapter.notifyDataSetChanged()
    }
}