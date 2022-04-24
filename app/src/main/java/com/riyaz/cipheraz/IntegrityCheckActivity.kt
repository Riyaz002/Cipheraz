package com.riyaz.cipheraz

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.riyaz.cipheraz.databinding.ActivityIntegrityCheckBinding
import java.security.MessageDigest
import android.provider.OpenableColumns




const val FILE1 = 1
const val FILE2 = 2

class IntegrityCheckActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntegrityCheckBinding
    lateinit var file1: Uri
    lateinit var file2: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_integrity_check)

        binding.apply {
            btnChooseFile1.setOnClickListener {
                resetImage()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(intent, FILE1)
            }
            binding.btnChooseFile2.setOnClickListener {
                resetImage()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(intent, FILE2)
            }
            binding.btnCheckIntegrity.setOnClickListener {
                resetImage()
                if (isBothFileSelected()) {
                    checkIntegrity()
                } else {
                    Toast.makeText(
                        this@IntegrityCheckActivity,
                        "Choose both files",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun resetImage() {
        binding.integrityResultPic.setImageResource(R.color.white)
    }

    private fun checkIntegrity() {
        contentResolver.apply {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashValue1: ByteArray
            val hashValue2: ByteArray
            openInputStream(file1).use {
                val file1Bytes = it?.readBytes()
                hashValue1 = digest.digest(file1Bytes)
            }
            openInputStream(file2).use {
                val file2Bytes = it?.readBytes()
                hashValue2 = digest.digest(file2Bytes)
            }
            if (hashValue1.contentEquals(hashValue2)) {
                fileContentIsEqual()
            } else {

                fileContentIsNotEqual()
            }
        }
    }

    private fun fileContentIsNotEqual() {
        binding.integrityResultPic.setImageResource(R.drawable.ic_baseline_block_24)
    }

    private fun fileContentIsEqual() {
        binding.integrityResultPic.setImageResource(R.drawable.ic_baseline_check_circle_24)
    }

    private fun isBothFileSelected(): Boolean {
        return file1 != Uri.EMPTY && file2 != Uri.EMPTY
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE1) {
            if (resultCode == RESULT_OK) {
                binding.file1.text = getFileName(data?.data?: Uri.EMPTY)
                file1 = data?.data ?: Uri.EMPTY
            } else {
                Toast.makeText(this, "couldn't select file 1", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == FILE2) {
            if (resultCode == RESULT_OK) {
                binding.file2.text = getFileName(data?.data?: Uri.EMPTY)
                file2 = data?.data ?: Uri.EMPTY
            } else {
                Toast.makeText(this, "couldn't select file 2", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}
