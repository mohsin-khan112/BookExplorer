package com.example.acropolisassignment.book

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.acropolisassignment.R
import com.example.acropolisassignment.bookBrowsing.BookBrowsingActivity
import com.example.acropolisassignment.bookBrowsing.GenreListAdapter
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.Genre
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream


class AddBookActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var ivCoverPhoto: ImageView
    private lateinit var etBookName: TextInputEditText
    private lateinit var etBookDescription: TextInputEditText
    private lateinit var genreSpinner: Spinner

    private lateinit var context: Context
    private var viewModel: AddBookViewModel? = null
    private var genreList: List<Genre>?= null
    private var genreListAdapter: GenreListAdapter? = null
    private var selectedGenre: Genre? = null
    private var imageData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        toolbar = findViewById(R.id.toolbar_addBook)
        ivCoverPhoto = findViewById(R.id.iv_addBook_coverPhoto)
        etBookName = findViewById(R.id.et_addBook_name)
        etBookDescription = findViewById(R.id.et_addBook_description)
        genreSpinner = findViewById(R.id.spinner_addBook_genreList)
        context = this
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(AddBookViewModel::class.java)
        viewModel?.getGenreListLiveData()?.observe(this,
            Observer<List<Genre>> { genreList ->
                Log.d(TAG, "setupViewModel: Genre list size : ${genreList.size}")
                this.genreList = genreList
                setupGenreSpinner(genreList)
            })

        viewModel?.getAddBookLiveData()?.observe(this,
        Observer {row ->
            if (row != null) {
                setResult(BookBrowsingActivity.ADD_BOOK_SCREEN_CODE)
                finish()
            } else {
                Toast.makeText(context, "Failed to add Book", Toast.LENGTH_LONG).show()
            }
        })
        viewModel?.fetchGenreList()
    }

    private fun setCoverPhoto() {
        val bytes = Base64.decode(imageData!!, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Glide.with(context)
            .asBitmap()
            .load(bitmap)
            .into(ivCoverPhoto)
    }

    private fun setupGenreSpinner(genreList: List<Genre>) {
        genreListAdapter = GenreListAdapter(context, genreList)
        genreSpinner.adapter = genreListAdapter
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedGenre = genreList[position]
            }

        }
    }

    fun selectPhoto(view: View) {
        if (checkStoragePermission()) {
           startImagePicker()
        }
    }

    fun addBook(view: View) {
        if (isValidationPassed()) {
            viewModel?.addBook(Book(
                name = etBookName.text!!.toString(),
                description = etBookDescription.text!!.toString(),
                photo = imageData!!,
                genreId = selectedGenre!!.id
            ))
        }
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_LOAD_IMAGE)
    }

    private fun isValidationPassed(): Boolean {
        if (imageData == null) {
            Toast.makeText(context, "Please choose cover photo of the book", Toast.LENGTH_LONG).show()
            return false
        } else if (etBookName.text!!.isEmpty() || etBookDescription.text!!.isEmpty()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun checkStoragePermission(): Boolean {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION)
            return false
        }

        return true
    }

    private fun convertToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream();
        val scaledBitmap = getResizedBitmap(bitmap, 310, 476)
        if (scaledBitmap != null) {
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        return ""
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            if (selectedImage != null) {
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? =
                    contentResolver.query(selectedImage, filePathColumn, null, null, null)
                cursor?.moveToFirst()
                val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
                if (columnIndex != null) {
                    val picturePath: String? = cursor.getString(columnIndex)
                    cursor.close()
                    val bitmap = BitmapFactory.decodeFile(picturePath)
                    imageData = convertToBase64(bitmap)
                    setCoverPhoto()

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val TAG = "AddBookActivity"
        private const val REQUEST_CODE_LOAD_IMAGE = 300
        private const val REQUEST_CODE_STORAGE_PERMISSION = 400
    }
}