package io.ymsoft.objectfinder.ui.pick_photo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ActivityPickPhotoFromSpecificDirBinding
import java.io.File

const val REQUEST_PICK_FROM_STORAGE = 20

class PickPhotoFromSpecificDirActivity : AppCompatActivity() {

    companion object {
        private const val ARG_PATH = "directory path"
        fun start(from: Fragment, filePath: String?=null){
            val intent = Intent(from.requireContext(), PickPhotoFromSpecificDirActivity::class.java)
            intent.putExtra(ARG_PATH, filePath)
            from.startActivityForResult(intent, REQUEST_PICK_FROM_STORAGE)
        }
    }

    lateinit var binding: ActivityPickPhotoFromSpecificDirBinding

    private val adapter: PhotoListAdapter = PhotoListAdapter { filePath ->
        val intent = Intent()
        intent.putExtra("filePath", filePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_photo_from_specific_dir)

        binding.backBtn.setOnClickListener { onBackPressed() }

        binding.recyclerView.adapter = adapter

        val dirPath: String? = intent.getStringExtra(ARG_PATH)

        var directory =
            if(dirPath == null) getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            else File(dirPath)

//        logI("${directory?.absolutePath}")

        arrayListOf<String>().apply {
            directory?.listFiles()?.forEach {
//                logI("${it.name} // ${it.absolutePath}")
                if(it.length() > 0)
                    add(it.absolutePath)
            }
        }.also {
            if(it.isEmpty()) binding.emptyMessage.visibility = View.VISIBLE
            else binding.emptyMessage.visibility = View.GONE
            adapter.photoList = it
        }

    }
}
