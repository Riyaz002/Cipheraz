package com.riyaz.cipheraz.selectfile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.riyaz.cipheraz.R
import com.riyaz.cipheraz.databinding.SelectFileFragmentBinding


class SelectFileFragment : Fragment() {

    lateinit var binding: SelectFileFragmentBinding

    companion object {
        fun newInstance() = SelectFileFragment()
    }

    private lateinit var viewModel: SelectFileViewModel
    private var path: String? = null

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
                if(it){
                    findNavController()
                        .navigate(SelectFileFragmentDirections
                            .actionSelectFileFragmentToCipherFragment(path?:"")
                        )

                    viewModel.onNavigated()

                }
            }
        })
        // TODO: Use the ViewModel
        binding.buttonChooseFile.setOnClickListener {
            viewModel.onChooseFileButtonClicked()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_APP_FILES)
            intent.type = "*/*"
            startActivityForResult(this.requireActivity(), intent, 10, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == 10){
            if(requestCode == RESULT_OK){
                path = data?.data?.path
                if(path.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "File is not supported", Toast.LENGTH_SHORT)
                    return
                }
                viewModel.onFileSelected()
            }
        }
    }
}