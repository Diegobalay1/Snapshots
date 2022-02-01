package com.diego.kotlin.snapshots

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.diego.kotlin.snapshots.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class AddFragment : Fragment() {

    private val RC_GALLERY = 18 //Request Code RC
    private val PATH_SNAPSHOT = "snapshots"

    private lateinit var mBinding: FragmentAddBinding
    private lateinit var mStorageReference: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference

    private var mPhotoSelectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAddBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnPost.setOnClickListener {
            postSnapshot()
        }

        mBinding.btnSelect.setOnClickListener { openGallery() }

        mStorageReference = FirebaseStorage.getInstance().reference
        mDatabaseReference = FirebaseDatabase.getInstance("https://snapshots-bfd2a-default-rtdb.europe-west1.firebasedatabase.app").reference.child(PATH_SNAPSHOT)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //startActivityForResult(intent, RC_GALLERY)*/
        getContent.launch(intent)
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            mPhotoSelectedUri = it.data?.data
            mBinding.imgPhoto.setImageURI(mPhotoSelectedUri)
            mBinding.tilTitle.visibility = View.VISIBLE
            mBinding.tvMessage.text = getString(R.string.post_message_valid_title)
        }
    }

    private fun postSnapshot() {
        mBinding.progressBar.visibility = View.VISIBLE
        //mStorageReference.child(PATH_SNAPSHOT).child("my_photo")
        val storageReference = mStorageReference.child(PATH_SNAPSHOT).child("my_photo")
        if (mPhotoSelectedUri != null) {
            storageReference.putFile(mPhotoSelectedUri!!)
                .addOnProgressListener {
                    val progress = (100 * it.bytesTransferred/it.totalByteCount).toDouble()//bytes transferidos con respecto al total
                    mBinding.progressBar.progress = progress.toInt()
                    mBinding.tvMessage.text = "$progress%"
                }
                .addOnCompleteListener {
                    mBinding.progressBar.visibility = View.INVISIBLE
                }
                    //si el progresso ha sido exitoso
                .addOnSuccessListener {
                    Snackbar.make(mBinding.root, "Instantánea publicada.",
                        Snackbar.LENGTH_SHORT).show()
                }
                    //si fracasa la subida
                .addOnFailureListener {
                    Snackbar.make(mBinding.root, "No se pudo subir, intente mas tarde.",
                        Snackbar.LENGTH_SHORT).show()
                }
        }

    }

    private fun saveSnapshot(){

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == RC_GALLERY){
                mPhotoSelectedUri = data?.data
                mBinding.imgPhoto.setImageURI(mPhotoSelectedUri)
                mBinding.tilTitle.visibility = View.VISIBLE
                mBinding.tvMessage.text = getString(R.string.post_message_valid_title)
            }
        }
    }*/
    /*
        private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){

    }
}

        resultLauncher.launch(Intent(...))
     */

}









