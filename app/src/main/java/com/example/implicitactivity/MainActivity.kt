package com.example.implicitactivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sendSMS

class MainActivity : AppCompatActivity() {
    val CALL_REQUEST_CODE = 101
    //For capturing image
    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()
//        making a call
        img_call.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            val phone_number = et_call.getText()
            intent.data = Uri.parse("tel:" + phone_number)
            startActivity(intent)
        }
//        sendng a message
        img_message.setOnClickListener {
            val phone_number = et_call.getText().toString()
            val message = et_message.getText().toString()
            sendSMS(phone_number, message)
        }
//        visit web
        img_web.setOnClickListener {
            val url = et_web.getText().toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

        }
//        take a photo
        img_camera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    openCamera()
                }
            } else
            //system os is < marshmallow
                openCamera()

        }
//        Mpesa app
        img_mpesa.setOnClickListener(View.OnClickListener {
            val simToolKitLaunchIntent: Intent? =
                this@MainActivity.getPackageManager().getLaunchIntentForPackage("com.android.stk")
            simToolKitLaunchIntent?.let { startActivity(it) }
        })

//        view gallery
        img_gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("content://media/external/images/media/")
            startActivity(intent)
        }
        img_call_log.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = CallLog.Calls.CONTENT_TYPE
            startActivity(intent)
        }






    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CALL_PHONE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("noone", "Permission to Call has denied")
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_REQUEST_CODE)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK){
            //set image captured to image view
            img_display.setImageURI(image_uri)
        }
    }
//    //Requesting permision from the user
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        //called when user presses ALLOW or DENY from Permission Request Popup
//        when(requestCode){
//            PERMISSION_CODE -> {
//                if (grantResults.size > 0 && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED){
//                    //permission from popup was granted
//                    openCamera()
//                }
//                else{
//                    //permission from popup was denied
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

}
