package com.android.leandro.testesantander.model

class UserAccount {

    var userid: Long = 0
    var name: String? = null
    var bankaccount: String? = null
    var agency: String? = null
    var balance: Double = 0.toDouble()



    constructor(userid: Long, name: String, bankaccount: String, agency: String, balance: Double) {
        this.userid = userid
        this.name = name
        this.bankaccount = bankaccount
        this.agency = agency
        this.balance = balance
    }
    constructor() {

    }
}
