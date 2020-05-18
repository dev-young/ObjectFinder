package io.ymsoft.objectfinder.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)

        // TODO: 이런식으로 메인 엑티비티를 가져와서 하지 말고 변경된 텍스트를 ViewModel에 넣어서 사용해보는건 어떨까?
        
        if(activity is MainActivity){
            val sv = (activity as MainActivity).searchView

            sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.text.text = newText
                    return false
                }

            })
        }

        binding.btn.setOnClickListener {
            findNavController().navigate(R.id.action_navSearch_to_navPositionDetail)
        }

        super.onViewCreated(view, savedInstanceState)
    }

}
