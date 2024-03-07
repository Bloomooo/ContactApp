package com.yanis.mechta

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
import com.yanis.mechta.R

/**
 * @author Yanis Mechta
 * `ContactAdapter` est un adaptateur pour afficher une liste de contacts dans un `RecyclerView`.
 * Il est utilisé pour lier les données des contacts à leurs éléments de vue correspondants
 * et gérer les interactions utilisateur avec ces éléments.
 *
 * @property contacts Liste des contacts à afficher dans le RecyclerView.
 * @property listener Écouteur pour les événements de clic sur les contacts.
 */
class ContactAdapter(private var contacts: MutableList<Contact>, private val listener: ContactClickListener) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    /**
     * Crée une nouvelle instance de `ContactViewHolder` en inflatant la mise en page du contact à partir du XML.
     *
     * @param parent Le parent auquel la nouvelle vue est attachée.
     * @param viewType Le type de vue.
     * @return Une nouvelle instance de `ContactViewHolder`.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    /**
     * Lie les données du contact à son élément de vue correspondant.
     *
     * @param holder Le ViewHolder à mettre à jour.
     * @param position La position de l'élément dans la liste.
     */
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



    /**
     * Affiche un dialogue contenant les détails du contact.
     *
     * @param contact Le contact à afficher dans le dialogue.
     * @param context Le contexte actuel.
     * @param position La position du contact dans la liste.
     */
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

    /**
     * Retourne le nombre total d'éléments dans la liste de contacts.
     *
     * @return Le nombre total de contacts.
     */
    override fun getItemCount(): Int {
        return contacts.size
    }

    /**
     * Met à jour les données de la liste de contacts.
     *
     * @param newContacts La nouvelle liste de contacts.
     */
    fun updateData(newContacts: MutableList<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    /**
     * Classe interne représentant le ViewHolder pour chaque élément de contact dans le RecyclerView.
     * @property itemView La vue de l'élément de contact.
     */
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

        /**
         * Met à jour le statut du favori pour un contact à une position donnée.
         *
         * @param position La position du contact dans la liste.
         * @param isFavorite Le nouveau statut du favori.
         */
        fun updateFavoriteStatus(position: Int, isFavorite: Boolean) {
            if (position != RecyclerView.NO_POSITION && position < contacts.size) {
                contacts[position].setFavorite(isFavorite)
                notifyItemChanged(position)
            }
        }

        /**
         * Lie les données du contact à l'élément de vue correspondant.
         *
         * @param contact Le contact à lier.
         */
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
    /**
     * Interface pour écouter les événements de clic sur les contacts.
     */
    interface ContactClickListener {

        /**
         * Appelé lorsqu'un contact est mis à jour dans la liste.
         *
         * @param contact Le contact mis à jour.
         * @param position La position du contact dans la liste.
         */
        fun updateListContact(contact: Contact, position: Int)

        /**
         * Appelé lorsqu'un contact est supprimé de la liste.
         *
         * @param contact Le contact supprimé.
         */
        fun onContactDeleted(contact: Contact)
    }
}