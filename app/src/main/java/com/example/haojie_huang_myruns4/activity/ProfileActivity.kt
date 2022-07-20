package com.example.haojie_huang_myruns4.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.haojie_huang_myruns4.R
import com.example.haojie_huang_myruns4.ViewModel.ProfileViewModel
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

    //About profile image
    private val temp_profile_image_name = "temp_profile_image.jpg"
    private val profile_image_name = "profile_image.jpg"
    private lateinit var temp_profile_image_uri: Uri
    private lateinit var profile_image_uri: Uri
    private lateinit var temp_profile_image_file: File
    private lateinit var profile_image_file: File
    private val authority = "com.example.haojie_huang_myruns4"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var startRotationActivity: ActivityResultLauncher<Intent>
    private lateinit var profile_view_model: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Find the views of profile layout
        image_view = findViewById(R.id.profile_picture)
        name_view = findViewById(R.id.profile_name)
        email_address_view = findViewById(R.id.profile_email_address)
        phone_number_view = findViewById(R.id.profile_phone_number)
        gender_view = findViewById(R.id.profile_gender)
        class_year_view = findViewById(R.id.profile_class_year)
        major_view = findViewById(R.id.profile_major)

        //Get the uri of image
        temp_profile_image_file = File(getExternalFilesDir(null), temp_profile_image_name)
        profile_image_file = File(getExternalFilesDir(null), profile_image_name)
        temp_profile_image_uri = FileProvider.getUriForFile(this, authority, temp_profile_image_file)
        profile_image_uri = FileProvider.getUriForFile(this, authority, profile_image_file)

        //Reload profile image and data from shared preferences
        if (profile_image_file.exists())
        {
            image_view.setImageBitmap(getBitmap(profile_image_uri))
        }
        loadWithSp()

        //savedInstanceState
        if(savedInstanceState != null)
        {
            name_view.text = savedInstanceState.getString("NAME_KEY")
            email_address_view.text = savedInstanceState.getString("EMAIL_KEY")
            phone_number_view.text = savedInstanceState.getString("PHONE_NUMBER_KEY")
            class_year_view.text = savedInstanceState.getString("CLASS_KEY")
            major_view.text = savedInstanceState.getString("MAJOR_KEY")
            gender_view.check(savedInstanceState.getInt("GENDER_KEY"))
        }

        //View model
        profile_view_model = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profile_view_model.user_image.observe(this)
        { it ->
            image_view.setImageBitmap(it)
        }

        startRotationActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK)
            {
                val temp_profile_image_rotated_file = File(getExternalFilesDir(null), "temp_profile_image_rotated.jpg")
                temp_profile_image_rotated_file.renameTo(temp_profile_image_file)
                val bitmap = getBitmap(temp_profile_image_uri)
                profile_view_model.user_image.value = bitmap
                //只要改变viewmodel里面的值就会call .observe的代码，不管顺序.
                //Change the value in viewmodel and will call the .observe code automatically, no matter of the code order.
            }
        }

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode == Activity.RESULT_OK)
            {
                toRotation(temp_profile_image_uri)
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK)
            {
                toRotation(it.data?.data!!)
            }
        }
    }

    fun onChangePhotoClicked(view: View)
    {
        val dialogOptions = arrayOf("Open Camera", "Select from Gallery")
        val builder = AlertDialog.Builder(this)
        var intent: Intent
        builder.setTitle("Pick Profile Picture")
        builder.setItems(dialogOptions)
        {
                _, index ->
            when(dialogOptions[index])
            {
                "Open Camera" -> {
                    intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, temp_profile_image_uri)
                    cameraResult.launch(intent)
                }
                "Select from Gallery" -> {
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryResult.launch(intent)
                }
            }
        }
        builder.show()
    }

    fun onSaveClicked(view: View)
    {
        if (File(getExternalFilesDir(null), temp_profile_image_name).exists())
        {
            temp_profile_image_file.renameTo(profile_image_file)
        }
        if (image_view.drawable == null)
        {
            temp_profile_image_file.delete()
            profile_image_file.delete()
        }
        saveWithSp()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancelClicked(view: View)
    {
        if (File(getExternalFilesDir(null), temp_profile_image_name).exists())
        {
            temp_profile_image_file.delete()
        }
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onResetClicked(view: View)
    {
        profile_view_model.user_image.value = null
        image_view.setImageDrawable(null)
        name_view.text = ""
        email_address_view.text = ""
        phone_number_view.text = ""
        class_year_view.text = ""
        major_view.text = ""
        gender_view.clearCheck()
    }

    private fun getBitmap(imgUri: Uri): Bitmap
    {
        val bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(imgUri))
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, null, true)
    }

    private fun toRotation(imgUri: Uri)
    {
        val intent = Intent(this, RotationActivity::class.java)
        intent.putExtra("PASS_URI", imgUri.toString())
        startRotationActivity.launch(intent)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putString("NAME_KEY", name_view.text.toString())
        outState.putString("EMAIL_KEY", email_address_view.text.toString())
        outState.putString("PHONE_NUMBER_KEY", phone_number_view.text.toString())
        outState.putString("CLASS_KEY", class_year_view.text.toString())
        outState.putString("MAJOR_KEY", major_view.text.toString())
        val gender_id = gender_view.checkedRadioButtonId
        if (gender_id != -1)
        {
            outState.putInt("GENDER_KEY", gender_id)
        }
    }

    //Shared preferences save function
    private fun saveWithSp()
    {
        val shared_pref: SharedPreferences = getSharedPreferences("PROFILE_CONFIG", MODE_PRIVATE)
        val gender_id: Int = gender_view.checkedRadioButtonId
        shared_pref.edit()
            .putString("NAME_KEY", name_view.text.toString())
            .putString("EMAIL_KEY", email_address_view.text.toString())
            .putString("PHONE_NUMBER_KEY", phone_number_view.text.toString())
            .putInt("GENDER_KEY", gender_id)
            .putString("CLASS_KEY", class_year_view.text.toString())
            .putString("MAJOR_KEY", major_view.text.toString())
            .apply()
    }

    //Shared preferences load function
    private fun loadWithSp()
    {
        val shared_pref: SharedPreferences = getSharedPreferences("PROFILE_CONFIG", MODE_PRIVATE)
        name_view.text = shared_pref.getString("NAME_KEY", "")
        email_address_view.text = shared_pref.getString("EMAIL_KEY", "")
        phone_number_view.text = shared_pref.getString("PHONE_NUMBER_KEY", "")
        gender_view.check(shared_pref.getInt("GENDER_KEY", -1))
        class_year_view.text = shared_pref.getString("CLASS_KEY", "")
        major_view.text = shared_pref.getString("MAJOR_KEY", "")
    }
}
