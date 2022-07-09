package com.example.haojie_huang_myruns2

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File

class ProfileActivity : AppCompatActivity()
{
    //Global variable
    private lateinit var image_view: ImageView
    private lateinit var name_view: TextView
    private lateinit var email_address_view: TextView
    private lateinit var phone_number_view: TextView
    private lateinit var gender_view: RadioGroup
    private lateinit var class_year_view: TextView
    private lateinit var major_view: TextView

    private val temp_image_name = "temp_image.jpg"
    private val profile_image_name = "profile_image.jpg"
    private lateinit var temp_image_uri: Uri
    private lateinit var profile_image_uri: Uri
    private lateinit var view_model: ViewModel

    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    //For view model
    private var userName: String? = ""
    private var userEmail: String? = ""
    private var userPhone: String? = ""
    private var userGender: Int = -1
    private var userClass: String? = ""
    private var userMajor: String? = ""

    //Key
    private val NAME_KEY = "name_key"
    private val EMAIL_KEY = "email_key"
    private val PHONE_NUMBER_KEY = "phone_key"
    private val CLASS_KEY = "class_key"
    private val MAJOR_KEY = "major_key"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Default code
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        title = "MyRuns"
        Util.checkPermissions(this)

        //Find the views of profile layout
        image_view = findViewById(R.id.profile_picture)
        name_view = findViewById(R.id.profile_name)
        email_address_view = findViewById(R.id.profile_email_address)
        phone_number_view = findViewById(R.id.profile_phone_number)
        gender_view = findViewById(R.id.profile_gender)
        class_year_view = findViewById(R.id.profile_class_year)
        major_view = findViewById(R.id.profile_major)


        //Get the uri of image
        val temp_image_file = File(getExternalFilesDir(null), temp_image_name)
        val profile_image_file = File(getExternalFilesDir(null), profile_image_name)
        temp_image_uri = FileProvider.getUriForFile(this, "com.example.haojie_huang_myruns2", temp_image_file)
        profile_image_uri = FileProvider.getUriForFile(this, "com.example.haojie_huang_myruns2", profile_image_file)

        //Reload profile image and shared preferences
        if (profile_image_file.exists())
        {
            val bitmap = Util.getBitmap(this, profile_image_uri, 90f)
            image_view.setImageBitmap(bitmap)
        }
        loadWithSp()

        //Used view model
        view_model = ViewModelProvider(this).get(ViewModel::class.java)
        view_model.user_image.observe(this)
        {
            val bitmap = Util.getBitmap(this, temp_image_uri, 90f)
            image_view.setImageBitmap(bitmap)
        }

        view_model.user_name.observe(this)
        {
            name_view.text = userName
        }

        view_model.user_email.observe(this)
        {
            email_address_view.text = userEmail
        }

        view_model.user_phone.observe(this)
        {
            phone_number_view.text = userPhone
        }

        view_model.user_class.observe(this)
        {
            class_year_view.text = userClass
        }

        view_model.user_major.observe(this)
        {
            major_view.text = userMajor
        }

        view_model.user_gender.observe(this)
        {
            gender_view.check(userGender)
        }

        //The other option of view model to protect temp data
//        if (temp_image_file.exists())
//        {
//            val bitmap = Util.getBitmap(this, temp_image_uri)
//            image_view.setImageBitmap(bitmap)
//        }

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK)
            {
                val bitmap = Util.getBitmap(this, temp_image_uri, 90f)
                //image_view.setImageBitmap(bitmap)  //The other option of view model to protect temp data
                view_model.user_image.value = bitmap

                //For string view model
                view_model.user_name.value = name_view.text.toString()
                view_model.user_email.value = email_address_view.text.toString()
                view_model.user_phone.value = phone_number_view.text.toString()
                view_model.user_class.value = class_year_view.text.toString()
                view_model.user_major.value = major_view.text.toString()
                view_model.user_gender.value = gender_view.checkedRadioButtonId
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK)
            {
                temp_image_uri = result.data?.data!!
                Util.creatBitmap(this, temp_image_name, temp_image_uri)
                val bitmap = Util.getBitmap(this, temp_image_uri, 0f)
                image_view.setImageBitmap(bitmap)
            }
        }

        //Temp recover
        if(savedInstanceState != null)
        {
            name_view.text = savedInstanceState.getString(NAME_KEY)
            email_address_view.text = savedInstanceState.getString(EMAIL_KEY)
            phone_number_view.text = savedInstanceState.getString(PHONE_NUMBER_KEY)
            class_year_view.text = savedInstanceState.getString(CLASS_KEY)
            major_view.text = savedInstanceState.getString(MAJOR_KEY)
            gender_view.check(savedInstanceState.getInt("GENDER_KEY"))
        }
    }

    fun onChangePhotoClicked(view: View)
    {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val dialogOptions = arrayOf("Open Camera", "Select from Gallery")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Profile Picture")
        builder.setItems(dialogOptions)
        {
            dialog, index ->
            when(dialogOptions[index])
            {
                "Open Camera" ->
                {
                    intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, temp_image_uri)
                    cameraResult.launch(intent)
                }
                "Select from Gallery" ->
                {
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryResult.launch(intent)
                }
            }
        }
        val photoDialog = builder.create()
        photoDialog.show()
    }

    fun onSaveClicked(view: View)
    {

        //Save temp image to profile image
        if (File(getExternalFilesDir(null), temp_image_name).exists())
        {
            val temp_image_file = File(getExternalFilesDir(null), temp_image_name)
            val profile_image_file = File(getExternalFilesDir(null), "profile_image.jpg")
            temp_image_file.renameTo(profile_image_file)
        }

        //Save shared preferences
        saveWithSp()

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancelClicked(view: View)
    {
        val temp_image_file = File(getExternalFilesDir(null), temp_image_name)
        temp_image_file.delete()
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        finish()
    }

//    fun onResetClicked(view: View)
//    {
//        val temp_image_file = File(getExternalFilesDir(null), temp_image_name)
//        val profile_image_file = File(getExternalFilesDir(null), profile_image_name)
//        if (temp_image_file.exists())
//        {
//            temp_image_file.delete()
//
//        }
//        if (profile_image_file.exists())
//        {
//            profile_image_file.delete()
//
//        }
//        val shared_pref: SharedPreferences = getSharedPreferences("config", MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = shared_pref.edit()
//        editor.clear();
//        editor.commit();
//        Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show()
//    }

    //Temp save
    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putString(NAME_KEY, name_view.text.toString())
        outState.putString(EMAIL_KEY, email_address_view.text.toString())
        outState.putString(PHONE_NUMBER_KEY, phone_number_view.text.toString())
        outState.putString(CLASS_KEY, class_year_view.text.toString())
        outState.putString(MAJOR_KEY, major_view.text.toString())
        val gender_id = gender_view.checkedRadioButtonId
        if (gender_id != -1)
        {
            outState.putInt("GENDER_KEY", gender_id)
        }
    }

    //Shared preferences save function
    private fun saveWithSp()
    {
        val shared_pref: SharedPreferences = getSharedPreferences("config", MODE_PRIVATE)
        val gender_id: Int = gender_view.checkedRadioButtonId
        shared_pref.edit()
            .putString(NAME_KEY, name_view.text.toString())
            .putString(EMAIL_KEY, email_address_view.text.toString())
            .putString(PHONE_NUMBER_KEY, phone_number_view.text.toString())
            .putInt("GENDER_KEY", gender_id)
            .putString(CLASS_KEY, class_year_view.text.toString())
            .putString(MAJOR_KEY, major_view.text.toString())
            .apply()
    }

    //Shared preferences load function
    private fun loadWithSp()
    {
        val shared_pref: SharedPreferences = getSharedPreferences("config", MODE_PRIVATE)
        name_view.text = shared_pref.getString(NAME_KEY, "")
        email_address_view.text = shared_pref.getString(EMAIL_KEY, "")
        phone_number_view.text = shared_pref.getString(PHONE_NUMBER_KEY, "")
        gender_view.check(shared_pref.getInt("GENDER_KEY", -1))
        class_year_view.text = shared_pref.getString(CLASS_KEY, "")
        major_view.text = shared_pref.getString(MAJOR_KEY, "")
    }
}
