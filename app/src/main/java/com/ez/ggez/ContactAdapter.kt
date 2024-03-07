package com.ez.ggez

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private var contacts: MutableList<Contact>, private val listener: ContactClickListener) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contacts[position]
        holder.bind(currentContact)

        holder.itemView.setOnClickListener {
            showContactDialog(currentContact, holder.itemView.context, position)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Supprimer le contact")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce contact ?")
                .setPositiveButton("Oui") { dialog, which ->
                    val deletedContact = contacts.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, contacts.size - position)
                    listener.onContactDeleted(deletedContact)
                }
                .setNegativeButton("Non", null)
                .show()
            true
        }
    }



    private fun showContactDialog(contact: Contact, context: Context?, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)
        val dialogImageView = dialogView.findViewById<ImageView>(R.id.dialog_image_view)
        val dialogNameTextView = dialogView.findViewById<TextView>(R.id.dialog_name)
        val dialogSexeTextView = dialogView.findViewById<TextView>(R.id.dialog_sexe)
        val dialogDateOfBirthTextView = dialogView.findViewById<TextView>(R.id.dialog_date_of_birth)
        val dialogPhoneTextView = dialogView.findViewById<TextView>(R.id.dialog_phone)
        val dialogEmailTextView = dialogView.findViewById<TextView>(R.id.dialog_email)
        val dialogFavorisTextView = dialogView.findViewById<TextView>(R.id.dialog_favoris)

        dialogNameTextView.text = "${contact.getFirstName()} ${contact.getLastName()}"
        dialogSexeTextView.text = "Sexe: ${contact.getSex()}"
        dialogDateOfBirthTextView.text = "Date de Naissance: ${contact.getBirth()}"
        dialogPhoneTextView.text = "Téléphone: ${contact.getPhone()}"
        dialogEmailTextView.text = "Email: ${contact.getMail()}"
        dialogFavorisTextView.text = "Favoris: ${if (contact.isFavorite()!!) "Oui" else "Non"}"
        contact.getPhoto()?.let { imageUriString ->
            val imageUri = Uri.parse(imageUriString)
            val inputStream = dialogImageView.context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            dialogImageView.setImageBitmap(bitmap)
            inputStream?.close()
        } ?: dialogImageView.setImageResource(R.drawable.ez)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Modifier") { dialog, _ ->
            val builderModify = AlertDialog.Builder(context)
            val modifyView = LayoutInflater.from(context).inflate(R.layout.dialog_modify, null)
            val dialogNameModify = modifyView.findViewById<EditText>(R.id.dialog_name_modify)
            val dialogSexeModify = modifyView.findViewById<RadioGroup>(R.id.radioGroup)
            val dialogDateOfBirthModify = modifyView.findViewById<EditText>(R.id.dialog_date_of_birth_modify)
            val dialogPhoneModify = modifyView.findViewById<EditText>(R.id.dialog_phone_modify)
            val dialogEmailModify = modifyView.findViewById<EditText>(R.id.dialog_email_modify)
            val dialogFavorisModify = modifyView.findViewById<CheckBox>(R.id.dialog_favoris_modify)
            val dialogImageViewModify = modifyView.findViewById<ImageView>(R.id.dialog_image_view_modify)

            dialogNameModify.setText("${contact.getFirstName()} ${contact.getLastName()}")
            when (contact.getSex()) {
                "Homme" -> dialogSexeModify.check(R.id.radioButton4)
                "Femme" -> dialogSexeModify.check(R.id.radioButton3)
            }
            dialogDateOfBirthModify.setText(contact.getBirth())
            dialogPhoneModify.setText(contact.getPhone())
            dialogEmailModify.setText(contact.getMail())

            dialogFavorisModify.isChecked = contact.isFavorite() == true

            var uriggez: String? = null
            contact.getPhoto()?.let { imageUriString ->
                uriggez = imageUriString
                val imageUri = Uri.parse(imageUriString)
                val inputStream = dialogImageViewModify.context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                dialogImageViewModify.setImageBitmap(bitmap)
                inputStream?.close()
            } ?: dialogImageViewModify.setImageResource(R.drawable.ez)

            builderModify.setView(modifyView)
            builderModify.setPositiveButton("Enregistrer") { dialogModify, _ ->

                val newFullName = dialogNameModify.text.toString()
                val newSexe = when (dialogSexeModify.checkedRadioButtonId) {
                    R.id.radioButton4 -> "Homme"
                    R.id.radioButton3 -> "Femme"
                    else -> ""
                }
                val newDateOfBirth = dialogDateOfBirthModify.text.toString()
                val newPhone = dialogPhoneModify.text.toString()
                val newEmail = dialogEmailModify.text.toString()
                val newFavorite = dialogFavorisModify.isChecked

                val firstName = newFullName.split(" ")[0]
                val lastName = newFullName.split(" ")[1]
                val updatedContact = Contact(newSexe, firstName, lastName, newPhone, newEmail, newDateOfBirth, uriggez, newFavorite)

                contacts[position] = updatedContact
                notifyItemChanged(position)
                listener.updateListContact(updatedContact, position)
                dialogModify.dismiss()
            }
            builderModify.setNegativeButton("Annuler") { dialogModify, _ ->
                dialogModify.dismiss()
            }
            builderModify.show()
        }
        builder.show()
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun updateData(newContacts: MutableList<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        private val imgView: ImageView = itemView.findViewById(R.id.dialog_image_ez)
        private val favoriteImageView: ImageView = itemView.findViewById(R.id.favoriteImageView)

        init {
            favoriteImageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val contact = contacts[position]
                    val newFavoriteStatus = !contact.isFavorite()!!
                    updateFavoriteStatus(position, newFavoriteStatus)
                    listener.updateListContact(contact, position)
                }
            }
        }
        fun updateFavoriteStatus(position: Int, isFavorite: Boolean) {
            if (position != RecyclerView.NO_POSITION && position < contacts.size) {
                contacts[position].setFavorite(isFavorite)
                notifyItemChanged(position)
            }
        }

        fun bind(contact: Contact) {
            nameTextView.text = "${contact.getFirstName()} ${contact.getLastName()} "
            phoneTextView.text = contact.getPhone()
            contact.getPhoto()?.let { imageUriString ->
                val imageUri = Uri.parse(imageUriString)
                val inputStream = itemView.context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imgView.setImageBitmap(bitmap)
                inputStream?.close()
            } ?: imgView.setImageResource(R.drawable.ez)
            if (contact.isFavorite()!!) {
                favoriteImageView.setImageResource(R.drawable.fav)
            } else {
                favoriteImageView.setImageResource(R.drawable.unfav)
            }
        }
    }
    interface ContactClickListener {
        fun updateListContact(contact: Contact, position: Int)
        fun onContactDeleted(contact: Contact)
    }
}