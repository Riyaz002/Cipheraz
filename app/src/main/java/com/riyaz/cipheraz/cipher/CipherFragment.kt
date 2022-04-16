package com.riyaz.cipheraz.cipher

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.riyaz.cipheraz.R
import com.riyaz.cipheraz.databinding.CipherFragmentBinding
import com.riyaz.cipheraz.utils.Mode
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

const val CREATE_DOC_CODE = 2
class CipherFragment : Fragment() {

    lateinit var binding: CipherFragmentBinding
    lateinit var uri: Uri

    companion object {
        fun newInstance() = CipherFragment()
    }

    private lateinit var viewModel: CipherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.cipher_fragment, container, false)
        val contentResolver = requireContext().applicationContext.contentResolver

        uri = navArgs<CipherFragmentArgs>().value.uri.toUri().also {
            binding.txtUri.text = it.toString()
        }
        var type = navArgs<CipherFragmentArgs>().value.type


        viewModel = ViewModelProvider(
            this,
            CipherViewModelFactory(uri.toString(), type)
        )[CipherViewModel::class.java]

        viewModel.crypt.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    if (binding.txtKey.editText?.text?.length != 16 || binding.txtFilename.editText?.text?.length == 0) return@Observer
                    crypt(contentResolver, uri)
                    viewModel.doneCryption()
                }
            }
        })

        binding.decryptionButton.setOnClickListener {
            it.isClickable = false
            binding.encryptionButton.isClickable = true
            viewModel.setMode(Mode.DECRYPT)
        }
        binding.encryptionButton.setOnClickListener {
            it.isClickable = false
            binding.decryptionButton.isClickable = true
            viewModel.setMode(Mode.ENCRYPT)
        }

        binding.txtFilename.editText?.doOnTextChanged { text, start, before, count ->
        }
        binding.txtKey.editText?.doOnTextChanged { text, start, before, count ->
            text?.let {
                if (it.isEmpty()) {
                    binding.txtKey.error = null
                }
                if (it.isEmpty()) {
                    binding.txtKey.error = "16 character needed!"
                }
            }
        }
        viewModel.eventCreateDocument.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "${binding.txtFilename.editText?.text}/${viewModel.type}"
                        putExtra(DocumentsContract.EXTRA_INITIAL_URI, viewModel.initialUri)
                    }
                    startActivityForResult(intent, CREATE_DOC_CODE)
                }
            }
        })
        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CREATE_DOC_CODE){
            if(resultCode != RESULT_OK){
                Toast.makeText(requireContext(), "couldn't create document", Toast.LENGTH_SHORT).show()
            } else{
                //viewModel.startCryption()
                    viewModel.crypt()
                uri = data?.dataString?.toUri()?: "".toUri()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun crypt(contentResolver: ContentResolver, uri: Uri) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    it.write(
                        ("Overwritten at ${System.currentTimeMillis()}\n")
                            .toByteArray()
                    )
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}