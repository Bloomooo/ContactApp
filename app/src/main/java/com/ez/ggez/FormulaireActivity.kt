package com.ez.ggez

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.util.Locale

/**
 * `MainActivity` est le point d'entrée de l'application, responsable de l'initialisation
 * de l'interface utilisateur et de la gestion des interactions utilisateur.
 */
class FormulaireActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private var useDefaultImage: Boolean = true
    /**
     * Appelée lorsque l'activité commence. C'est ici que la plupart des initialisations doivent avoir lieu :
     * appel de `setContentView(int)` pour gonfler l'interface utilisateur de l'activité, utilisation de `findViewById(int)`
     * pour interagir programmatiquement avec les widgets dans l'UI.
     *
     * @param savedInstanceState Si l'activité est réinitialisée après avoir été précédemment arrêtée,
     * alors ce Bundle contient les données qu'elle a le plus récemment fournies dans
     * onSaveInstanceState(Bundle). Sinon, il est null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulaire)
        val firstNameExtra = intent.getStringExtra("firstName")
        val prenomEditText = findViewById<EditText>(R.id.editTextPrenom)
        prenomEditText.setText(firstNameExtra)
        val button = findViewById<Button>(R.id.button)
        val dateListener = findViewById<EditText>(R.id.editTextDate)

        dateListener.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = sdf.format(selectedDate.time)
                    dateListener.setText(formattedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        val img = findViewById<ImageView>(R.id.imageView)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        img.setOnClickListener(){
            showImagePickerOptions()
        }

        button.setOnClickListener {
            val name = findViewById<EditText>(R.id.editTextName).text.toString()
            val prenom = prenomEditText.text.toString()
            val date = dateListener.text.toString()
            val tel = findViewById<EditText>(R.id.editTextPhone).text.toString()
            val mail = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val selectedFavoris = findViewById<CheckBox>(R.id.checkBox).isChecked
            val favoris = if (selectedFavoris) "Oui" else "Non"
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedSexe = if (selectedRadioButtonId != -1) {
                selectedRadioButton.text.toString()
            } else {
                ""
            }
            if (name.isEmpty() || prenom.isEmpty() || date.isEmpty() || tel.isEmpty() || mail.isEmpty() || !mail.contains("@") || !mail.contains(".")) {
                var snack: Snackbar? = null
                if(!mail.contains("@") || !mail.contains(".")){
                    snack = Snackbar.make(it, "Email format incorrecte !", Snackbar.LENGTH_LONG)
                } else {
                    snack = Snackbar.make(it, "Un ou plusieurs champs est/sont vide(s)", Snackbar.LENGTH_LONG)
                }
                snack.show()
            } else {

                val message =
                    "Sexe : $selectedSexe\n"+
                    "Prénom : $prenom\n" +
                            "Nom : $name\n" +
                            "Date de naissance : $date\n" +
                            "Téléphone : $tel\n" +
                            "Email : $mail\n" +
                            "Favoris : $favoris"
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Souhaitez-vous ajouter ce contact ?")
                    .setPositiveButton("Envoyer") { _, _ ->
                        val resultIntent = Intent()
                        resultIntent.putExtra("resultKey", message)
                        if (!useDefaultImage) {
                            imageUri?.let {
                                resultIntent.putExtra("imageUri", it.toString())
                            }
                        } else {
                            resultIntent.putExtra("useDefaultImage", true)
                        }
                        setResult(RESULT_OK, resultIntent)
                        returnImageToMainActivity()
                        val toast = Toast.makeText(this, "Contact ajouter !", Toast.LENGTH_SHORT)
                        toast.show()
                        finish()
                    }
                    .setNegativeButton("Annuler") { dialog, _ ->
                        dialog.dismiss()
                    }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun showImagePickerOptions() {
        val items = arrayOf<CharSequence>("Prendre une photo", "Choisir depuis la galerie", "Annuler")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ajouter une photo")
        builder.setItems(items) { dialog, item ->
            when (items[item]) {
                "Prendre une photo" -> dispatchTakePictureIntent()
                "Choisir depuis la galerie" -> {
                    Log.d("Gallery", "Choisir depuis la galerie a été sélectionné")
                    dispatchGalleryIntent()
                }
                "Annuler" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(packageManager)?.also {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun dispatchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    imageBitmap = data?.extras?.get("data") as Bitmap
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)
                    useDefaultImage = false
                }
                REQUEST_IMAGE_GALLERY -> {
                    imageUri = data?.data
                    findViewById<ImageView>(R.id.imageView).setImageURI(imageUri)
                    useDefaultImage = false
                }
            }
        }
    }

    private fun returnImageToMainActivity() {
        val resultIntent = Intent()
        imageBitmap?.let { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            resultIntent.putExtra("imageBitmap", byteArray)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }





}


