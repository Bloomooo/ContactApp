package com.yanis.mechta

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.yanis.mechta.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author Yanis Mechta
 * Activité principale de l'application.
 */
class MainActivity : AppCompatActivity(), ContactAdapter.ContactClickListener {

    /**
     * Méthode appelée lors de la création de l'activité.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var recyclerView = findViewById<RecyclerView>(R.id.contactRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var adapter = ContactAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter


        loadContact()
        val button = findViewById<Button>(R.id.button2)
        val mainActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getStringExtra("resultKey")
                    val imageUriString = result.data?.getStringExtra("imageUri")

                    val bitmapImage: Bitmap? = if (imageUriString != null) {
                        val imageUri = Uri.parse(imageUriString)
                        contentResolver.takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val inputStream = contentResolver.openInputStream(imageUri)
                        BitmapFactory.decodeStream(inputStream).also {
                            inputStream?.close()
                        }
                    } else {
                        BitmapFactory.decodeResource(resources, R.drawable.ez)
                    }

                    val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
                    val dialogImageView = dialogView.findViewById<ImageView>(R.id.dialog_image_view)
                    dialogImageView.setImageBitmap(bitmapImage)


                    val sexe = data?.split("\n")?.get(0)?.split(":")?.get(1)?.trim()
                    val prenom = data?.split("\n")?.get(1)?.split(":")?.get(1)?.trim()
                    val nom = data?.split("\n")?.get(2)?.split(":")?.get(1)?.trim()
                    val dateNaissance = data?.split("\n")?.get(3)?.split(":")?.get(1)?.trim()
                    val telephone = data?.split("\n")?.get(4)?.split(":")?.get(1)?.trim()
                    val email = data?.split("\n")?.get(5)?.split(":")?.get(1)?.trim()
                    val favoris = data?.split("\n")?.get(6)?.split(":")?.get(1)?.trim()

                    val contact = Contact(sexe, prenom, nom, telephone, email, dateNaissance, imageUriString, favorite = if(favoris == "Oui") true else false)
                    val contacts = getContacts().toMutableList()
                    contacts.add(contact)
                    saveContacts(contacts)
                    dialogView.findViewById<TextView>(R.id.dialog_name).text =
                        "Contact : $prenom $nom"
                    dialogView.findViewById<TextView>(R.id.dialog_sexe).text = "Sexe : $sexe"
                    dialogView.findViewById<TextView>(R.id.dialog_date_of_birth).text =
                        "Date de naissance : $dateNaissance"
                    dialogView.findViewById<TextView>(R.id.dialog_phone).text =
                        "Téléphone : $telephone"
                    dialogView.findViewById<TextView>(R.id.dialog_email).text = "Email : $email"
                    dialogView.findViewById<TextView>(R.id.dialog_favoris).text =
                        "Favoris : $favoris"

                    val builder = AlertDialog.Builder(this)
                    builder.setView(dialogView)
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()

                    }

                    builder.show()
                }
            }

        button.setOnClickListener {
            val intent = Intent(this, FormulaireActivity::class.java)
            intent.putExtra("firstName", "Yanis")
            mainActivityResultLauncher.launch(intent)
        }
        val contactButton = findViewById<Button>(R.id.listecontact)
        contactButton.setOnClickListener {
            loadContact()
        }

        val favoriteButton = findViewById<Button>(R.id.listefavoris)
        favoriteButton.setOnClickListener {
            loadFavorites()
        }
    }

    /**
     * Enregistre la liste des contacts dans les préférences partagées.
     *
     * @param contacts La liste des contacts à enregistrer.
     */
    private fun saveContacts(contacts: MutableList<Contact>) {
        val sharedPreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(contacts)
        editor.putString("contacts_list", json)
        editor.apply()
    }

    /**
     * Met à jour un contact dans la liste des contacts.
     *
     * @param contact Le contact à mettre à jour.
     * @param position La position du contact dans la liste.
     */
    private fun updateContact(contact: Contact, position: Int) {
        val contacts = getContacts().toMutableList()
        contacts[position] = contact
        saveContacts(contacts)
    }

    /**
     * Supprime un contact de la liste des contacts.
     *
     * @param contact Le contact à supprimer.
     */
    private fun deleteContact(contact: Contact) {
        val contacts = getContacts().toMutableList()
        contacts.removeAll { it.getPhone() == contact.getPhone() }
        saveContacts(contacts)
    }


    /**
     * Obtient la liste des contacts à partir des préférences partagées.
     *
     * @return La liste des contacts.
     */
    private fun getContacts(): MutableList<Contact> {
        val sharedPreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("contacts_list", null)
        val type = object : TypeToken<MutableList<Contact>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    /**
     * Charge tous les contacts dans la vue de la liste des contacts.
     */
    private fun loadContact() {
        val contacts = getContacts()
        val recyclerView = findViewById<RecyclerView>(R.id.contactRecyclerView)
        val adapter = recyclerView.adapter as? ContactAdapter
        adapter?.updateData(contacts)
    }

    /**
     * Charge tous les contacts favoris dans la vue de la liste des contacts.
     */
    private fun loadFavorites() {
        val favorites = getContacts().filter { it.isFavorite() == true }
        val recyclerView = findViewById<RecyclerView>(R.id.contactRecyclerView)
        val adapter = recyclerView.adapter as? ContactAdapter
        adapter?.updateData(favorites.toMutableList())
    }


    override fun onResume() {
        super.onResume()
        loadContact()
    }

    /**
     * Méthode appelée lorsque la liste des contacts doit être mise à jour.
     *
     * @param contact Le contact mis à jour.
     * @param position La position du contact dans la liste.
     */
    override fun updateListContact(contact: Contact, position: Int) {
        updateContact(contact, position)
    }

    /**
     * Méthode appelée lorsqu'un contact est supprimé de la liste.
     *
     * @param contact Le contact supprimé.
     */
    override fun onContactDeleted(contact: Contact) {
        deleteContact(contact)
    }


}




