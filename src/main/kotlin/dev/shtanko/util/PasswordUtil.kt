package dev.shtanko.util

import at.favre.lib.crypto.bcrypt.BCrypt

fun passwordMatches(plainPassword: String, hashedPassword: String): Boolean {
 return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified
}

fun String.toHashString(): String {
 return BCrypt.withDefaults().hashToString(12, this.toCharArray())
}
