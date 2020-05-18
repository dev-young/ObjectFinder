package io.ymsoft.objectfinder.ui.add

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.TaskListener
import io.ymsoft.objectfinder.databinding.ActivityAddObjectBinding
import io.ymsoft.objectfinder.makeToast
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.ui.BaseActivity
import io.ymsoft.objectfinder.view_model.StorageViewModel

class AddObjectActivity : BaseActivity() {

    lateinit var binding:ActivityAddObjectBinding
    private lateinit var storageViewModel: StorageViewModel

    private var imgUrl :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_object)
        setHomeBtn(binding.toolbar)

        binding.content.saveBtn.setOnClickListener {saveObject()}

        storageViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)



    }

    private fun saveObject() {
        val storageName = binding.content.storageName.text.toString()
        val objectName = binding.content.objectName.text.toString()
        val storageModel = StorageModel(null, imgUrl, null, null, storageName, null)
        val objModel = ObjectModel(null, null, objectName, null)
        storageViewModel.addNew(storageModel, objModel, TaskListener {
            if(it.isSuccessful){
                finish()
            } else {
                makeToast(it.message)
            }
        })
    }
}
