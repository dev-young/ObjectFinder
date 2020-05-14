package io.ymsoft.objectfinder

import androidx.appcompat.widget.SearchView

class Test {
    var view: SearchView? = null
    fun s() {
        view!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }
}