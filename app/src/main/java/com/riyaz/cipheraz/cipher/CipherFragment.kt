package com.riyaz.cipheraz.cipher

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.riyaz.cipheraz.R
import com.riyaz.cipheraz.databinding.CipherFragmentBinding

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

        val path = CipherFragmentArgs().path

        viewModel = ViewModelProvider(this, CipherViewModelFactory(path?: "")).get(CipherViewModel::class.java)

        viewModel.setOutputFileName.observe( viewLifecycleOwner, Observer {
            it?.let{
                if(it){
                    if (checkParameters()) return@Observer
                    crypt()
                }
            }
        })

        return binding.root
    }

    private fun crypt() {
        viewModel.setNameOfOutputFile(binding.etOutputFileName.text.toString())
        viewModel.setKey(binding.etKey.text.toString())
        viewModel.crypt()
    }

    private fun checkParameters(): Boolean {
        if (binding.etKey.text.toString().length == 0) {
            Toast.makeText(
                this.requireContext(),
                "Please, set the key.",
                Toast.LENGTH_SHORT)
                .show()
            return true
        }
        if (binding.etOutputFileName.text.toString().length == 0) {
            Toast.makeText(
                this.requireContext(),
                "Please, give the output file name.",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}