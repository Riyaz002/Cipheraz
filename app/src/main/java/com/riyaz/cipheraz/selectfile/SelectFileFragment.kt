package com.riyaz.cipheraz.selectfile

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils.substring
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.arrayMapOf
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.riyaz.cipheraz.R
import com.riyaz.cipheraz.databinding.SelectFileFragmentBinding
import java.security.Permission
import java.security.Permissions
import java.util.*
import kotlin.collections.ArrayList

const val SELECT_FILE_REQUEST_CODE = 1
const val REQUEST_STORAGE_PERMISSION = 10
const val IS_GRANTED = 1
const val NOT_GRANTED = 2

class SelectFileFragment : Fragment() {

    lateinit var binding: SelectFileFragmentBinding

    companion object {
        fun newInstance() = SelectFileFragment()
    }

    private lateinit var viewModel: SelectFileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_file_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SelectFileViewModel::class.java)

        viewModel.eventOnFileSelected.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it && viewModel.path != null){
                    findNavController()
                        .navigate(SelectFileFragmentDirections
                            .actionSelectFileFragmentToCipherFragment(viewModel.path.value)
                        )

                    viewModel.onNavigated()
                } else if(viewModel.path.value.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "file path is not valid", Toast.LENGTH_SHORT).show()
                    viewModel.filePathInvalidEvent()
                }
            }
        })
        // TODO: Use the ViewModel
        binding.buttonChooseFile.setOnClickListener {
            //viewModel.onChooseFileButtonClicked()
            selectFile()
        }

        //requestStoragePermission();
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*")
        }
        startActivityForResult(this.requireActivity(), intent, SELECT_FILE_REQUEST_CODE, null)
    }

    private fun requestStoragePermission() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == REQUEST_STORAGE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showPermissionResultToast(true)
            } else {
                showPermissionResultToast(false)
            }
        } else{
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showPermissionResultToast(isGranted: Boolean) {
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        if(isGranted){
        toast.setText("Permission Granted")
        toast.show()
        } else{
            toast.setText("Permission Not Granted")
            toast.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == SELECT_FILE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                data?.let {
                    //viewModel.fetchPath(it.data?.path.toString())
                    Toast.makeText(requireContext(), "${it.data?.path.toString()}", Toast.LENGTH_SHORT).show()
                }
                //viewModel.fileSelected()
            }
        }
    }
}