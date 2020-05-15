package io.ymsoft.objectfinder.ui.add_object

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.TaskListener
import io.ymsoft.objectfinder.databinding.ActivityAddObjectBinding
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.ui.BaseActivity
import io.ymsoft.objectfinder.utils.ToastUtil
import io.ymsoft.objectfinder.view_model.PositionViewModel

class AddObjectActivity : BaseActivity() {

    lateinit var binding:ActivityAddObjectBinding
    private lateinit var positionViewModel: PositionViewModel

    private var imgUrl :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_object)
        setHomeBtn(binding.toolbar)


        positionViewModel = ViewModelProvider(this).get(PositionViewModel::class.java)


        binding.content.saveBtn.setOnClickListener {saveObject()}





    }

    private fun saveObject() {
        val positionName = binding.content.positionName.text.toString()
        val objectName = binding.content.objectName.text.toString()
        val positionModel = PositionModel(null, imgUrl, null, null, positionName, null)
        val objModel = ObjectModel(null, null, objectName, null)
        positionViewModel.addNew(positionModel, objModel, TaskListener {
            if(it.isSuccessful){
                finish()
            } else {
                ToastUtil.s(this, it.message)
            }
        })
    }
}
