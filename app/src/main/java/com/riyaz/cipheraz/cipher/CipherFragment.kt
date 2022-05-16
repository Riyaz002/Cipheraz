package com.riyaz.cipheraz.cipher

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.content.res.loader.ResourcesProvider
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.riyaz.cipheraz.R
import com.riyaz.cipheraz.databinding.CipherFragmentBinding
import com.riyaz.cipheraz.utils.Mode
import java.io.*
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

const val CREATE_DOC_CODE = 2
const val OPEN_DOC_CODE = 11

class CipherFragment : Fragment() {

    lateinit var binding: CipherFragmentBinding
    var inputUri: Uri = Uri.EMPTY
    var outputUri: Uri = Uri.EMPTY
    lateinit var type: String
    lateinit var contentResolver: ContentResolver
    lateinit var key: String
    lateinit var cipher: Cipher

    companion object {
        fun newInstance() = CipherFragment()
    }

    private lateinit var viewModel: CipherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.cipher_fragment, container, false)
        contentResolver = requireContext().applicationContext.contentResolver
        type = ""
        viewModel = ViewModelProvider(
            this,
            CipherViewModelFactory(outputUri.toString(), type)
        )[CipherViewModel::class.java]

        binding.decryptionButton.setOnClickListener {
            it.isEnabled = false
            binding.encryptionButton.isEnabled = true
            viewModel.setMode(Mode.DECRYPT)
        }

        binding.encryptionButton.setOnClickListener {
            it.isEnabled = false
            binding.decryptionButton.isEnabled = true
            viewModel.setMode(Mode.ENCRYPT)
        }

        binding.txtKey.editText?.doOnTextChanged { text, start, before, count ->
            text?.let {
                key = it.toString()
                if (it.length <= 4) {
                    binding.txtKey.error = "Key is short or unsafe"
                } else {
                    binding.txtKey.error = null
                    binding.btnGo.isEnabled = true
                }
            }
        }

        binding.btnChooseFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, OPEN_DOC_CODE)
        }

        binding.btnGo.setOnClickListener {
            if (inputUri == null) Toast.makeText(
                requireContext(),
                "Choose file first",
                Toast.LENGTH_SHORT
            ).show()
            if (checkInputs()) return@setOnClickListener
            createDocument()
        }
        return binding.root
    }

    private fun createDocument() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = contentResolver.getType(inputUri)
        intent.putExtra(Intent.EXTRA_TITLE, binding.etOutputFileName.text.toString())
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, viewModel.initialUri.value)
        startActivityForResult(intent, CREATE_DOC_CODE)
    }

    private fun checkInputs(): Boolean {
        if (inputUri == Uri.EMPTY) {
            Toast.makeText(requireContext(), "Choose file to operate on", Toast.LENGTH_SHORT).show()
            return true
        }
//        if (key.length != 16) {
//            Toast.makeText(requireContext(), "Invalid Key Length", Toast.LENGTH_SHORT).show()
//            return true
//        }
        if (binding.etOutputFileName.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter output file name", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OPEN_DOC_CODE) {
            if (resultCode == RESULT_OK) {
                inputUri = data?.data!!
                if (inputUri == null) {
                    Toast.makeText(requireContext(), "File is not supported", Toast.LENGTH_SHORT)
                    return
                } else {
                    Toast.makeText(
                        requireContext(),
                        contentResolver.getType(inputUri),
                        Toast.LENGTH_SHORT
                    ).show()
                    type = inputUri.toString()
                        .substring(inputUri.toString().lastIndexOf('.'), inputUri.toString().length)
                }
            }
        }
        if (requestCode == CREATE_DOC_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(requireContext(), "Couldn't create document", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Document created", Toast.LENGTH_SHORT).show()
                outputUri = data?.data!!
                crypt()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun crypt() {
        try {
            startCryptionProcess(viewModel.mode.value!!)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun startCryptionProcess(mode: Mode) {
        try {
            if(key.length < 16) makeRequiredLength()
            binding.etOutputFileName.setText(key.length.toString())
            val secretKey: Key = SecretKeySpec(key.toByteArray(), "AES")
            if(cipher == null) {
                cipher = Cipher.getInstance("AES")
            }
            cipher.init(mode.getValue(), secretKey)
            Toast.makeText(requireContext(), "Mode: ${mode}", Toast.LENGTH_SHORT).show()
            contentResolver.apply {
                openInputStream(inputUri)?.use { inputStream ->
                    var inputBytes = inputStream.readBytes()

                    //val stringBuilder = StringBuilder(String(inputBytes))

                    inputBytes = cipher.doFinal(inputBytes)
                    openOutputStream(outputUri)?.use { outputStream ->
                        outputStream.write(inputBytes)
                        outputStream.close()
                    }
                    inputStream.close()
                }
            }
        } catch (e: Exception) {
            Log.e(this.tag, e.message ?: "")
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeRequiredLength() {
        key += "000000000000".substring(0, 16 - key.length)
    }
}