package org.webproject.sso.model.enumModel

enum class USERTYPE(val id: Int) {
    ADMIN(0),
    CUSTOMER(1),
    REPAIRMAN(2);

    companion object{
        fun returnUserType(id: Int): USERTYPE? {
            return entries.find { it.id == id }
        }
    }
}