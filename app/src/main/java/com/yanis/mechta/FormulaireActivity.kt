package com.yanis.mechta

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
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.yanis.mechta.R
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale

/**
 * @author Yanis Mechta
 * `MainActivity` est le point d'entrée de l'application, responsable de l'initialisation
 * de l'interface utilisateur et de la gestion des interactions utilisateur.
 */
class FormulaireActivity : AppCompatActivity() {

    /**
     * Codes de requête pour les différentes opérations
     */
    private val REQUEST_CAMERA_PERMISSION = 101

    /**
     * Codes de requête pour les différentes opérations
     */
    private val REQUEST_STORAGE_PERMISSION = 102

    /**
     * Codes de requête pour les différentes opérations
     */
    private val REQUEST_MANAGE_STORAGE_PERMISSION = 103

    /**
     * Codes de requête pour les différentes opérations
     */
    private val REQUEST_IMAGE_CAPTURE = 1

    /**
     * Codes de requête pour les différentes opérations
     */
    private val REQUEST_IMAGE_GALLERY = 2

    /**
     * URI de l'image sélectionnée
     */
    private var imageUri: Uri? = null

    /**
     * Bitmap de l'image sélectionnée
     */
    private var imageBitmap: Bitmap? = null

    /**
     * Indicateur pour savoir si l'image par défaut est utilisée
     */
    private var useDefaultImage: Boolean = true

    /**
     * hemin de la photo capturée
     */
    private var currentPhotoPath: String? = null


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

        val dateListener = findViewById<EditText>(R.id.editTextDate)
        dateListener.setOnClickListener {
            showDatePickerDialog()
        }

        val img = findViewById<ImageView>(R.id.imageView)
        img.setOnClickListener(){
            showImagePickerOptions()
        }

        val button = findViewById<Button>(R.id.button)
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
                        val toast = Toast.makeText(this, "Contact ajouté !", Toast.LENGTH_SHORT)
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


        checkAndRequestPermissions()
    }

    /**
     * Vérification et demande des permissions nécessaires
     */
    private fun showDatePickerDialog() {
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
                findViewById<EditText>(R.id.editTextDate).setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    /**
     * Méthode pour afficher les options de sélection d'image
     */
    private fun showImagePickerOptions() {
        val options = arrayOf<CharSequence>("Prendre une photo", "Choisir depuis la galerie", "Annuler")
        AlertDialog.Builder(this).setTitle("Ajouter une photo").setItems(options) { dialog, which ->
            when (which) {
                0 -> if (hasPermission(Manifest.permission.CAMERA)) {
                    dispatchTakePictureIntent()
                } else {
                    requestPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION)
                }
                1 -> if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    dispatchGalleryIntent()
                } else {
                    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION)
                }
                2 -> dialog.dismiss()
            }
        }.show()
    }

    /**
     * Méthode pour vérifier si une permission est accordée
     */
    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * Méthode pour demander une permission
     */
    private fun requestPermission(permission: String, requestCode: Int) = ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

    /**
     * Méthode pour vérifier et demander les permissions nécessaires
     */
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, REQUEST_MANAGE_STORAGE_PERMISSION)
            }
        } else {
            val hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val hasReadStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            val permissionsToRequest = mutableListOf<String>()
            if (!hasCameraPermission) {
                permissionsToRequest.add(Manifest.permission.CAMERA)
            }
            if (!hasReadStoragePermission) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_STORAGE_PERMISSION)
            }
        }
    }

    /**
     * Méthode pour capturer une photo avec l'appareil photo
     */
    private fun dispatchTakePictureIntent() {
        if (hasPermission(Manifest.permission.CAMERA)) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } else {
                Toast.makeText(this, "Aucune application pour gérer la capture de photo", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION)
        }
    }

    /**
     * Méthode pour ouvrir la galerie pour sélectionner une image
     */
    private fun dispatchGalleryIntent() {
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION)
        }
    }

    /**
     * Méthode appelée lorsqu'un résultat est retourné par une activité lancée pour un résultat
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    imageBitmap?.let {
                        val imageFile = createImageFile()
                        imageFile?.let { file ->
                            try {
                                val fileOutputStream = FileOutputStream(file)
                                it.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                                fileOutputStream.close()
                                imageUri = Uri.parse(file.absolutePath)
                                findViewById<ImageView>(R.id.imageView).setImageURI(imageUri)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(this, "Erreur lors de l'enregistrement de l'image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } ?: run {
                        Toast.makeText(this, "Erreur lors de la capture de l'image", Toast.LENGTH_SHORT).show()
                    }

                }
                REQUEST_IMAGE_GALLERY -> {
                    imageUri = data?.data
                    imageBitmap = null
                    findViewById<ImageView>(R.id.imageView).setImageURI(imageUri)
                    useDefaultImage = false
                }
            }
        }
    }

    /**
     * Méthode pour créer un fichier image temporaire
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    /**
     * Méthode appelée lorsque les résultats des demandes de permission sont retournés
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(this, "Permission Camera refusée", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchGalleryIntent()
                } else {
                    Toast.makeText(this, "Permission de lecture refusée", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Méthode pour retourner l'image à l'activité principale
     */
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


