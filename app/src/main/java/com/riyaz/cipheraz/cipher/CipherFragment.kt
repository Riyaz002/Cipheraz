package com.riyaz.cipheraz.cipher

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.util.ULocale
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.riyaz.cipheraz.R
import com.riyaz.cipheraz.databinding.CipherFragmentBinding
import com.riyaz.cipheraz.selectfile.SelectFileFragment
import com.riyaz.cipheraz.utils.CypherMode
import java.util.*

const val CREATE_DOC_REQUEST_CODE = 21

class CipherFragment : Fragment() {

    lateinit var binding: CipherFragmentBinding

    companion object {
        fun newInstance() = CipherFragment()
    }

    private lateinit var viewModel: CipherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.cipher_fragment, container, false)

        val args: CipherFragmentArgs by navArgs()
        val path: String = args.path?:""

        viewModel = ViewModelProvider(this, CipherViewModelFactory(path)).get(CipherViewModel::class.java)

        viewModel.crypt.observe( viewLifecycleOwner, Observer {
            it?.let{
                if(it){
                    if(!isFieldsFilled()) return@Observer
                    createFile()
                    viewModel.crypt()
                }
            }
        })

        binding.apply {
            etOutputFileName.editText?.doOnTextChanged { text, start, before, count ->
                btnGo.isEnabled = count != 0
                text?.let {
                    viewModel.setNameOfOutputFile(it.toString())
                }
            }
            etKey.editText?.doOnTextChanged { text, start, before, count ->
                when {
                    count != 16 -> {
                        btnGo.isEnabled = false
                        etKey.error = getString(R.string.error)
                    }
                    else -> {
                        etKey.error = null
                        viewModel.setKey(text.toString())
                    }
                }
            }
            encryptionButton.setOnClickListener {
                encryptionButton.isEnabled = false
                decryptionButton.isEnabled = true
                viewModel.setMode(CypherMode.ENCRYPT)
            }
            decryptionButton.setOnClickListener {
                decryptionButton.isEnabled = false
                encryptionButton.isEnabled = true
                viewModel.setMode(CypherMode.DECRYPT)
            }
        }

        //setDropDownET()
        return binding.root
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = ""
            putExtra(Intent.EXTRA_TITLE, viewModel.nameOfOutputFile.value)
        }
        startActivityForResult(intent, CREATE_DOC_REQUEST_CODE)
    }


    private fun isFieldsFilled(): Boolean {
        return binding.etKey.error.isNullOrEmpty() && !binding.etOutputFileName.editText?.text.isNullOrEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CREATE_DOC_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                val path = data?.data?.path.toString()
                if(!path.isNullOrEmpty()){
                    viewModel.setOutputFilePath(path)
                } else{
                    Toast.makeText(requireContext(), "path is empty", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}