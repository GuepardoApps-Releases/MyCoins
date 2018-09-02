package guepardoapps.mycoins.controller

internal interface IMailController {
    fun sendMail(subject: String, text: String, addresses: ArrayList<String>, startNewActivity: Boolean)
}