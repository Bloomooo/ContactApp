package com.yanis.mechta

/**
 * @author Yanis Mechta
 * Représente un contact avec ses informations telles que le sexe, le prénom, le nom de famille,
 * le numéro de téléphone, l'adresse e-mail, la date de naissance, la photo et le statut favori.
 *
 * @property sex Le sexe du contact.
 * @property firstName Le prénom du contact.
 * @property lastName Le nom de famille du contact.
 * @property phone Le numéro de téléphone du contact.
 * @property mail L'adresse e-mail du contact.
 * @property birth La date de naissance du contact.
 * @property photo L'URI de la photo du contact.
 * @property favorite Le statut favori du contact.
 */
class Contact(sex: String?, firstName: String?, lastName: String?, phone: String?, mail: String?, birth: String?, photo: String?, favorite: Boolean?) {

    private var sex = sex
    private var firstName = firstName
    private var lastName = lastName
    private var phone = phone
    private var mail = mail
    private var birth = birth
    private var photo = photo
    private var favorite = favorite

    /**
     * Obtient le sexe du contact.
     *
     * @return Le sexe du contact.
     */
    fun getSex(): String? {
        return this.sex
    }

    /**
     * Obtient le prénom du contact.
     *
     * @return Le prénom du contact.
     */
    fun getFirstName(): String? {
        return this.firstName
    }

    /**
     * Obtient le nom de famille du contact.
     *
     * @return Le nom de famille du contact.
     */
    fun getLastName(): String? {
        return this.lastName
    }

    /**
     * Obtient le numéro de téléphone du contact.
     *
     * @return Le numéro de téléphone du contact.
     */
    fun getPhone(): String? {
        return this.phone
    }

    /**
     * Obtient l'adresse e-mail du contact.
     *
     * @return L'adresse e-mail du contact.
     */
    fun getMail(): String? {
        return this.mail
    }

    /**
     * Obtient la date de naissance du contact.
     *
     * @return La date de naissance du contact.
     */
    fun getBirth(): String? {
        return this.birth
    }

    /**
     * Obtient l'URI de la photo du contact.
     *
     * @return L'URI de la photo du contact.
     */
    fun getPhoto(): String? {
        return this.photo
    }

    /**
     * Obtient le statut favori du contact.
     *
     * @return Le statut favori du contact.
     */
    fun isFavorite(): Boolean? {
        return this.favorite
    }

    /**
     * Définit le sexe du contact.
     *
     * @param sex Le sexe du contact à définir.
     */
    fun setSex(sex: String?) {
        this.sex = sex
    }

    /**
     * Définit le prénom du contact.
     *
     * @param firstName Le prénom du contact à définir.
     */
    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    /**
     * Définit le nom de famille du contact.
     *
     * @param lastName Le nom de famille du contact à définir.
     */
    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    /**
     * Définit le numéro de téléphone du contact.
     *
     * @param phone Le numéro de téléphone du contact à définir.
     */
    fun setPhone(phone: String?) {
        this.phone = phone
    }

    /**
     * Définit l'adresse e-mail du contact.
     *
     * @param mail L'adresse e-mail du contact à définir.
     */
    fun setMail(mail: String?) {
        this.mail = mail
    }

    /**
     * Définit la date de naissance du contact.
     *
     * @param birth La date de naissance du contact à définir.
     */
    fun setBirth(birth: String?) {
        this.birth = birth
    }

    /**
     * Définit l'URI de la photo du contact.
     *
     * @param photo L'URI de la photo du contact à définir.
     */
    fun setPhoto(photo: String?) {
        this.photo = photo
    }

    /**
     * Définit le statut favori du contact.
     *
     * @param favorite Le statut favori du contact à définir.
     */
    fun setFavorite(favorite: Boolean?) {
        this.favorite = favorite
    }
}
