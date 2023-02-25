package com.example.musicplayerproject.models.data

class User : java.io.Serializable {
    var uid = ""
    var userName = ""
    var email = ""
    var profilePictureURL = ""

    constructor()

    constructor(uid: String?, email: String?, profilePictureURL: String?)
    {
        this.uid = uid!!
        this.email = email!!
        this.profilePictureURL = profilePictureURL!!

    }

    constructor(uid: String, email: String) {
        this.uid = uid
        this.email = email
    }

    constructor(uid: String) {
        this.uid = uid
    }


}