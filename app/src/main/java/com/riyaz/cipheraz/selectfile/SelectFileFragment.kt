package com.riyaz.cipheraz.selectfile

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
import com.riyaz.cipheraz.utils.OpenMyDocument

const val OPEN_DOC_CODE = 10

class SelectFileFragment : Fragment() {

    lateinit var binding: SelectFileFragmentBinding
    lateinit var uri: String
    lateinit var type: String

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
        viewModel = ViewModelProvider(this)[SelectFileViewModel::class.java]

        viewModel.eventOnFileSelected.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(requireContext(), "uri: ${uri.length} \n type: ${type}", Toast.LENGTH_LONG).show()

                    findNavController()
                        .navigate(
                            SelectFileFragmentDirections
                                .actionSelectFileFragmentToCipherFragment(uri,type)
                        )
                    viewModel.onNavigated()
                }
            }
        })
        // TODO: Use the ViewModel
        binding.buttonChooseFile.setOnClickListener {
            viewModel.onChooseFileButtonClicked()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, OPEN_DOC_CODE)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OPEN_DOC_CODE) {
            if (resultCode == RESULT_OK) {
                uri = data?.data.toString()
                type = data?.resolveType(requireContext()).toString()
                if (uri.isEmpty() || type.isEmpty()) {
                    Toast.makeText(requireContext(), "File is not supported", Toast.LENGTH_SHORT)
                    return
                }
                viewModel.onFileSelected()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
