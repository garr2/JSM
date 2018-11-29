package com.garr.pavelbobrovko.notsimplechat.presentation.screen.userSettings

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.garr.pavelbobrovko.notsimplechat.R
import com.garr.pavelbobrovko.notsimplechat.databinding.ActivityUserSettingsBinding
import com.garr.pavelbobrovko.notsimplechat.deprecated.ImageOrientationChecker
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseMvvmActivity
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserSettingsActivity : BaseMvvmActivity<UserSettingsViewModel,UserSettingsRouter,ActivityUserSettingsBinding>() {

    lateinit var userData: RegistrationUserData
    lateinit var instanceType: String

    private var photoUrlLocal = Uri.parse("")

    private var imageTile: String = ""

    val GALLERY_REQUEST_CODE = 1
    val CAMERA_REQUES_CODE = 2

    companion object {
        const val INSTANCE_TYPE = "INSTANCE_TYPE"
        const val INSTANCE_DATA = "INSTANCE_DATA"
        private const val ID_EXTRA = "ID_EXTRA"
        fun getInstance(context: Context, instanceType: String, instanceData: RegistrationUserData)
                : Intent{
            val intent = Intent(context, UserSettingsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(INSTANCE_TYPE, instanceType)
            bundle.putParcelable(INSTANCE_DATA, instanceData)
            intent.putExtra(ID_EXTRA, bundle)
            return intent
        }
    }

    override fun provideViewModel(): UserSettingsViewModel {
        return ViewModelProviders.of(this).get(UserSettingsViewModel::class.java)
    }

    override fun provideRouter(): UserSettingsRouter {
        return UserSettingsRouter(this)
    }

    override fun provideLayoutId(): Int {
        return R.layout.activity_user_settings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle = intent.getBundleExtra(ID_EXTRA)
        userData = bundle.getParcelable(INSTANCE_DATA)!!
        instanceType = bundle.getString(INSTANCE_TYPE)!!
        viewModel.setUserData(userData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_REQUES_CODE-> {
                    ImageOrientationChecker()
                            .checkImage(photoUrlLocal,this)
                }
                GALLERY_REQUEST_CODE-> {
                photoUrlLocal = data?.data
            }
            }
            viewModel.setNewPhotoUrl(photoUrlLocal)
        }
    }

     fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {

            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.d(ConstantInterface.LOG_TAG, "ERROR $ex")
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoUrlLocal = FileProvider.getUriForFile(this,
                            this.getApplicationContext().getPackageName() + ".provider",
                            photoFile)
                } else {
                    photoUrlLocal = Uri.fromFile(photoFile)
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUrlLocal)
                //cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION)

                startActivityForResult(cameraIntent, CAMERA_REQUES_CODE)
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageTile = "photo_$timeStamp"
        val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageTile, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        //image= new File(storageDir + "/" + imageTile + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        photoUrlLocal = Uri.fromFile(image)
        return image
    }
}
