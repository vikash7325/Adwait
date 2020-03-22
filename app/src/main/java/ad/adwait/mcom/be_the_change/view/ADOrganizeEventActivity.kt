package ad.adwait.mcom.be_the_change.view

import ad.adwait.mcom.R
import ad.adwait.mcom.be_the_change.model.ADOrganiseEventModel
import ad.adwait.mcom.utils.ADViewClickListener
import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_organize_event.*
import kotlinx.android.synthetic.main.common_toolbar.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ADOrganizeEventActivity : ADBaseActivity() {

    private val TAG: String = "ADOrganizeEventActivity"
    private lateinit var mEventModel: ADOrganiseEventModel
    private var FROM_GALLERY = 126
    private var FROM_CAMERA = 127
    private var SELECT_FILE = 128
    private var selectedImagePath: String = ""
    private var selectedFilePath: String = ""
    private val ORGANIZE_EVENT_TABLE: String = "Organize_event_details"
    private lateinit var mProgressDialog: AlertDialog

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organize_event)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        val cal = Calendar.getInstance()
        event_time.setText(SimpleDateFormat("HH:mm").format(cal.time))
        event_date.setText(SimpleDateFormat("dd-MM-yyyy").format(cal.time))

        /*Show Date dialog*/
        event_date.setOnClickListener(View.OnClickListener {

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    event_date.setText(SimpleDateFormat("dd-MM-yyyy").format(c.time))
                }, year, month, day
            )

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        })

        event_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 8) {
                    other_event_type_layout.visibility = View.VISIBLE
                } else {
                    other_event_type_layout.visibility = View.GONE
                }

            }

        }

        /*Show time dialog*/
        event_time.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                event_time.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()

        })

        privacy_setting.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.public_event) {
                mEventModel.eventPrivacy = "Public"
            } else {
                mEventModel.eventPrivacy = "Private"
            }
        }

        invite_others.setOnCheckedChangeListener { buttonView, isChecked ->
            mEventModel.eventInviteOthers = isChecked
        }

        auto_checkin.setOnCheckedChangeListener { buttonView, isChecked ->
            mEventModel.eventAutoCheckIn = isChecked
        }

        allow_gift.setOnCheckedChangeListener { buttonView, isChecked ->
            mEventModel.eventAllowGift = isChecked
        }

        add_image.setOnClickListener(View.OnClickListener {
            showOptionDialog()
        })

        choosed_image.setOnClickListener(View.OnClickListener { showOptionDialog() })

        choose_files.setOnClickListener(View.OnClickListener {
            val intent = Intent(ACTION_PICK)
                .setType("*/*")

            startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"),
                SELECT_FILE
            )
        })

        /*Page 1 next btn*/
        event_next1.setOnClickListener(View.OnClickListener {

            val name = event_name.text.toString()
            val location = event_location.text.toString()
            val date = event_date.text.toString()
            val time = event_time.text.toString()
            var type = event_type.selectedItem.toString()
            val description = event_desc.text.toString()

            if (name.isEmpty()) {
                event_name.setError(getString(R.string.empty_event_name))
            } else if (location.isEmpty()) {
                event_location.setError(getString(R.string.empty_location))
            } else if (date.isEmpty()) {
                event_date.setError(getString(R.string.empty_date))
            } else if (time.isEmpty()) {
                event_time.setError(getString(R.string.empty_time))
            } else if (event_type.selectedItemPosition == 0) {
                showMessage(getString(R.string.empty_type), organize_parent, true)
            } else if (event_type.selectedItemPosition == 8 && other_event_type.text.toString().isEmpty()) {
                type = other_event_type.text.toString()
                other_event_type.setError(getString(R.string.empty_desc))
            } else if (description.isEmpty()) {
                event_desc.setError(getString(R.string.empty_desc))
            } else {
                val userId: String =
                    MySharedPreference(applicationContext).getValueString(getString(R.string.userId))
                        .toString()
                mEventModel =
                    ADOrganiseEventModel(userId, name, location, date, time, type, description)
                event_page1.visibility = View.GONE
                event_page2.visibility = View.VISIBLE
            }

        })

        /*Page 2 next btn*/
        event_next2.setOnClickListener(View.OnClickListener {
            if (!selectedImagePath.isEmpty()) {
                event_page1.visibility = View.GONE
                event_page2.visibility = View.GONE
                event_page3.visibility = View.VISIBLE
            } else {
                showAlertDialog(
                    getString(R.string.no_event_image),
                    getString(R.string.yes),
                    getString(R.string.no),
                    object : ADViewClickListener {
                        override fun onViewClicked() {
                            event_page1.visibility = View.GONE
                            event_page2.visibility = View.GONE
                            event_page3.visibility = View.VISIBLE
                        }

                    })
            }
        })

        /*Page 3 submit btn*/
        event_submit.setOnClickListener(View.OnClickListener {
            if (!isNetworkAvailable()) {
                showMessage(getString(R.string.no_internet), organize_parent, true)
                return@OnClickListener
            }
            mProgressDialog = showProgressDialog("", false)
            mProgressDialog.show()

            mEventModel.eventInviteOthers = invite_others.isChecked
            mEventModel.eventAutoCheckIn = auto_checkin.isChecked
            mEventModel.eventAllowGift = allow_gift.isChecked
            saveImageInServer()
        })

        action_menu.setOnClickListener(View.OnClickListener {
            setResult(ad.adwait.mcom.utils.ADConstants.KEY_MENU)
            finish()
        })

        action_profile.setOnClickListener(View.OnClickListener {
            setResult(ad.adwait.mcom.utils.ADConstants.KEY_PROFILE)
            finish()
        })
    }

    private fun saveImageInServer() {

        if (selectedImagePath.isEmpty()) {
            mEventModel.eventDocumentsUrl = ""
            if (!selectedFilePath.isEmpty()) {
                saveFileInServer()
            } else {
                saveDataInServer()
            }
            return
        }
        val storage = FirebaseStorage.getInstance()

        val path = Uri.fromFile(File(selectedImagePath))
        val userId =
            MySharedPreference(applicationContext).getValueString(getString(R.string.userId))
        val fileName = path.lastPathSegment

        val fileRef =
            storage.reference.child("OrganiseEvent/Image/" + userId + "/" + fileName)
        val uploadTask = fileRef.putFile(path)

        val urlTask =
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    mEventModel.eventBannerUrl = downloadUri.toString()
                    if (!selectedFilePath.isEmpty()) {
                        saveFileInServer()
                    } else {
                        saveDataInServer()
                    }
                } else {
                    hideProgress(mProgressDialog)
                    showMessage(getString(R.string.contact_failed), organize_parent, true)
                }
            }
    }

    private fun saveFileInServer() {
        val storage = FirebaseStorage.getInstance()
        val path = Uri.fromFile(File(selectedFilePath))
        val userId =
            MySharedPreference(applicationContext).getValueString(getString(R.string.userId))
        val fileName = path.lastPathSegment

        val fileRef =
            storage.reference.child("OrganiseEvent/File/" + userId + "/" + fileName)

        val uploadTask = fileRef.putFile(path)

        val urlTask =
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    mEventModel.eventDocumentsUrl = downloadUri.toString()
                    saveDataInServer()
                } else {
                    hideProgress(mProgressDialog)
                    showMessage(getString(R.string.contact_failed), organize_parent, true)
                }
            }
    }

    private fun saveDataInServer() {

        val fireBaseInstance = FirebaseDatabase.getInstance()
        val fireBaseDB = fireBaseInstance.getReference(ORGANIZE_EVENT_TABLE)

        val key = fireBaseDB.push().key.toString()

        fireBaseDB.child(key).setValue(mEventModel)
            .addOnSuccessListener {
                hideProgress(mProgressDialog)
                showAlertDialog(getString(R.string.event_success),
                    getString(R.string.close),
                    "",
                    ad.adwait.mcom.utils.ADViewClickListener {
                        finish()
                    })
            }
            .addOnFailureListener {
                hideProgress(mProgressDialog)
                showMessage(getString(R.string.contact_failed), organize_parent, true)
            }
    }

    private fun showOptionDialog() {
        val builder = AlertDialog.Builder(this)

        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")

        builder.setCancelable(true)
        builder.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> choosePhoto(false)
                1 -> choosePhoto(true)
            }
        }
        builder.create().show()
    }

    private fun choosePhoto(isCamera: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isCamera) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermissions(permissions, FROM_GALLERY);
                } else {
                    choosePhotoFromGallary()
                }
            } else {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.CAMERA);
                    requestPermissions(permissions, FROM_CAMERA);
                } else {
                    takePhotoFromCamera()
                }
            }
        } else {
            //system OS is < Marshmallow
            if (isCamera) {
                takePhotoFromCamera()
            } else {
                choosePhotoFromGallary()
            }
        }
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FROM_GALLERY -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoFromGallary()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            FROM_CAMERA -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhotoFromCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun choosePhotoFromGallary() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, FROM_GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile())
        startActivityForResult(intent, FROM_CAMERA)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            .apply { selectedImagePath = absolutePath }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == FROM_GALLERY && resultCode == RESULT_OK) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                selectedImagePath = picturePath
                cursor.close()
                choosed_image.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                add_image.visibility = View.GONE
                choosed_image.visibility = View.VISIBLE
            } else if (requestCode == FROM_CAMERA && resultCode == RESULT_OK) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                choosed_image.setImageBitmap(thumbnail)
                add_image.visibility = View.GONE
                choosed_image.visibility = View.VISIBLE
            } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
                cursor!!.moveToFirst()

                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                selectedFilePath = picturePath
                choose_files.text = selectedFilePath
                cursor.close()
            }
        }
    }
}