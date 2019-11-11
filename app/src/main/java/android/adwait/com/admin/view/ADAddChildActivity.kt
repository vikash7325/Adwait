package android.adwait.com.admin.view

import and.com.polam.utils.ADBaseActivity
import android.Manifest
import android.adwait.com.R
import android.adwait.com.admin.model.*
import android.adwait.com.rest_api.ApiClient
import android.adwait.com.rest_api.ApiInterface
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_child.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ADAddChildActivity : ADBaseActivity() {

    private var mChildData: ADAddChildModel = ADAddChildModel()
    private var mIsEdit = false
    private var mKey = ""
    private val GALLERY = 1
    private val CAMERA = 2
    private var mImageTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_child)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        dob.setOnClickListener(View.OnClickListener {
            dob.hideKeyboard()
            showCalendar()
        })

        mImageTaken = false

        add_image.setOnClickListener(View.OnClickListener {
            showPictureDialog()
        })

        if (intent != null && intent.hasExtra("childData")) {
            var data = intent.getParcelableExtra<ADAddChildModel>("childData")

            child_name.setText(data.childName)
            dob.setText(data.dateOfBirth)
            ngo_name.setText(data.NGOName)
            school_name.setText(data.schoolName)
            email.setText(data.email)
            mentor_name.setText(data.mentorName)
            monthly_amt.setText(data.amountNeeded.toString())
            carrier_int.setText(data.careerInterest)
            hobbies.setText(data.hobbies)
            guardian_name.setText(data.guardianName)
            pincode.setText(data.pincode)
            city.setText(data.city)
            address.setText(data.address)

            account_name.setText(data.bankDetails.holderName)
            account_no.setText(data.bankDetails.accountNumber)
            ifsc_code.setText(data.bankDetails.ifsccode)
            account_type.setText(data.bankDetails.accountType)
            business_name.setText(data.businessName)
            business_type.setText(data.businessType)

            account_name.isEnabled = false
            account_name.isFocusable = false
            account_no.isEnabled = false
            account_no.isFocusable = false
            ifsc_code.isEnabled = false
            ifsc_code.isFocusable = false
            account_type.isEnabled = false
            account_type.isFocusable = false
            business_name.isEnabled = false
            business_name.isFocusable = false
            business_type.isEnabled = false
            business_type.isFocusable = false

            education.setText(data.splitDetails.miscellaneousAmount.toString())
            food.setText(data.splitDetails.foodAmount.toString())
            necessity.setText(data.splitDetails.necessityAmount.toString())
            extras.setText(data.splitDetails.miscellaneousAmount.toString())
            hobbies_amt.setText(data.splitDetails.hobbiesAmount.toString())

            mIsEdit = true
            mKey = data.keyId
            if (data.childImage != null && !data.childImage.equals("")) {
                Glide.with(this).load(data.childImage).error(R.drawable.image_not_found).into(add_image)
            }
            mChildData = data
        } else {
            mIsEdit = false
        }

        submit_btn.setOnClickListener(View.OnClickListener {

            val name = child_name.text.toString()
            val doBirth = dob.text.toString()
            val ngo = ngo_name.text.toString()
            val school = school_name.text.toString()
            val emailId = email.text.toString()
            val mentor = mentor_name.text.toString()
            val amountNeeded = monthly_amt.text.toString()
            val carrier = carrier_int.text.toString()
            val childHobby = hobbies.text.toString()
            val guardian = guardian_name.text.toString()
            val zipcode = pincode.text.toString()
            val childCity = city.text.toString()
            val ngoAddress = address.text.toString()

            val holderName = account_name.text.toString()
            val number = account_no.text.toString()
            val ifsc = ifsc_code.text.toString()
            val accountType = account_type.text.toString()
            val businessName = business_name.text.toString()
            val businessType = business_type.text.toString()

            val educationAmt = education.text.toString()
            val foodAmt = food.text.toString()
            val hobbiesAmt = hobbies_amt.text.toString()
            val necessityAmt = necessity.text.toString()
            val extrasAmt = extras.text.toString()

            extras.hideKeyboard()
            var tempHolder = ADAddChildModel()

            if (TextUtils.isEmpty(name)) {
                child_name.error = getString(R.string.empty_username)
            } else if (TextUtils.isEmpty(doBirth)) {
                dob.error = getString(R.string.empty_dob)
            } else if (TextUtils.isEmpty(ngo)) {
                ngo_name.error = getString(R.string.empty_ngo)
            } else if (TextUtils.isEmpty(school)) {
                school_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(emailId)) {
                email.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(amountNeeded)) {
                monthly_amt.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(carrier)) {
                carrier_int.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(childHobby)) {
                hobbies.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(guardian)) {
                guardian_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(zipcode)) {
                pincode.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(childCity)) {
                city.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(ngoAddress)) {
                address.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(holderName)) {
                account_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(number)) {
                account_no.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(ifsc)) {
                ifsc_code.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(accountType)) {
                account_type.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(businessName)) {
                business_name.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(businessType)) {
                business_type.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(educationAmt)) {
                education.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(foodAmt)) {
                food.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(hobbiesAmt)) {
                hobbies_amt.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(necessityAmt)) {
                necessity.error = getString(R.string.empty_field)
            } else if (TextUtils.isEmpty(extrasAmt)) {
                extras.error = getString(R.string.empty_field)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), add_child_parent, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE

                val bankDetails = ADBankDetails(number, ifsc, holderName, accountType)
                val splitDetails = ADMoneySplitUp(
                    educationAmt.toInt(), foodAmt.toInt(), hobbiesAmt.toInt(),
                    necessityAmt.toInt(), extrasAmt.toInt()
                )

                if (mIsEdit) {
                    tempHolder = mChildData
                }

                mChildData = ADAddChildModel(
                    name,
                    doBirth,
                    mentor,
                    ngoAddress,
                    emailId,
                    amountNeeded.toLong(),
                    carrier,
                    guardian,
                    childHobby,
                    zipcode,
                    childCity,
                    school,
                    ngo, "",
                    businessName,
                    businessType
                )

                mChildData.bankDetails = bankDetails
                mChildData.splitDetails = splitDetails


                if (mIsEdit) {
                    mChildData.accountId = tempHolder.accountId
                    mChildData.keyId = tempHolder.keyId
                    mChildData.childImage = tempHolder.childImage
                    uploadImage()
                    return@OnClickListener
                }

                var accountRequest = ADCreateAccountRequest(
                    holderName,
                    emailId,
                    businessName, businessType, ifsc.toUpperCase(), accountType, number.toLong()
                )

                val apiService = ApiClient.getClient().create(ApiInterface::class.java)

                val call = apiService.createAccount(accountRequest)

                call.enqueue(object : Callback<ADCreateAccountResponse> {

                    override fun onResponse(
                        call: Call<ADCreateAccountResponse>?,
                        response: Response<ADCreateAccountResponse>?
                    ) {
                        if (response != null && response.isSuccessful) {
                            if (response.body().isSuccessFlag) {
                                if (response.body() != null) {
                                    val fullResponse: ADCreateAccountResponse = response?.body()!!
                                    mChildData.accountId = fullResponse.data.id
                                    uploadImage()
                                }
                            } else {
                                showMessage(response.body().message, add_child_parent, true)
                                progress_layout.visibility = View.GONE
                            }
                        } else {
                            val error = Gson().fromJson(
                                response?.errorBody()?.charStream(),
                                RestError::class.java
                            )
                            Log.i("Testing ==> ", error.toString())
                            showMessage(error.error.description, add_child_parent, true)
                            progress_layout.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<ADCreateAccountResponse>?, t: Throwable?) {
                        progress_layout.visibility = View.GONE
                        showMessage("Something went wrong. Try again later", add_child_parent, true)
                        Log.i("Testing ==> ", t?.message.toString())
                    }

                })
            }

        })
    }

    private fun addChildToServer() {
        val mChildTable =
            FirebaseDatabase.getInstance().reference.child(CHILD_TABLE_NAME)

        var key = mChildTable.push().key.toString()

        if (mIsEdit) {
            key = mKey
        }
        mChildData.keyId = key

        if (mIsEdit) {

            mChildTable.child(key).updateChildren(mChildData.toMap())
                .addOnSuccessListener {
                    progress_layout.visibility = View.GONE
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    progress_layout.visibility = View.GONE
                    showMessage(it.message.toString(), null, true)
                }
        } else {

            mChildTable.child(key).setValue(mChildData)
                .addOnSuccessListener {
                    progress_layout.visibility = View.GONE
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    progress_layout.visibility = View.GONE
                    showMessage(it.message.toString(), null, true)
                }
        }
    }

    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                dob.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year)
            },
            year,
            month,
            day
        )

        dpd.datePicker.maxDate = System.currentTimeMillis() + 1000
        dpd.show()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 34
            )
            return
        }

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val permission2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 35
            )
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            34 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("", "Permission has been denied by user")
                } else {
                    choosePhotoFromGallary()
                }
            }

            35 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("", "Permission has been denied by user")
                } else {
                    takePhotoFromCamera()
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    add_image!!.setImageBitmap(bitmap)
                    mImageTaken = true
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ADAddChildActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            add_image!!.setImageBitmap(thumbnail)
            mImageTaken = true
        }
    }

    private fun uploadImage() {

        if (!mImageTaken) {
            addChildToServer()
            return
        }
        // Get the data from an ImageView as bytes
        add_image.isDrawingCacheEnabled = true
        add_image.buildDrawingCache()
        val bitmap = (add_image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        if (data != null) {

            val storage = FirebaseStorage.getInstance();
            val storageReference = storage.getReference();

            val childImagesRef = storageReference.child("images/" + child_name.text.toString())

            var progressDialog = ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            var uploadTask = childImagesRef.putBytes(data)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation childImagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mChildData.childImage = task.result.toString()
                    progressDialog.dismiss()
                    progress_layout.visibility = View.VISIBLE
                    addChildToServer()
                    Log.d("TAG", "File Saved::--->" + mChildData.childImage)
                } else {
                    progressDialog.dismiss()
                }
            }
        }
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }
}