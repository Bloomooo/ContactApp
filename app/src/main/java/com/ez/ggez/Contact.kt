package com.ez.ggez

class Contact (sex:String?, firstname:String?, lastName:String?, phone:String?, mail:String?, birth:String?, photo:String?,favorite:Boolean?){

    private var sex = sex
    private var firstname = firstname
    private var lastName = lastName
    private var phone = phone
    private var mail = mail
    private var birth = birth
    private var photo = photo
    private var favorite = favorite


    fun getSex():String?{
        return this.sex
    }

    fun getFirstName(): String?{
        return this.firstname
    }

    fun getLastName(): String?{
        return this.lastName
    }

    fun getPhone(): String?{
        return this.phone
    }

    fun getMail(): String?{
        return this.mail
    }

    fun getBirth():String?{
        return this.birth
    }
    fun getPhoto():String?{
        return this.photo
    }
    fun isFavorite(): Boolean?{
        return this.favorite
    }

    fun setSex(sex: String?) {
        this.sex = sex
    }

    fun setFirstName(firstName: String?) {
        this.firstname = firstName
    }

    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    fun setPhone(phone: String?) {
        this.phone = phone
    }

    fun setMail(mail: String?) {
        this.mail = mail
    }

    fun setBirth(birth: String?) {
        this.birth = birth
    }

    fun setPhoto(photo: String?){
        this.photo = photo
    }
    fun setFavorite(favorite: Boolean?){
        this.favorite = favorite
    }
}