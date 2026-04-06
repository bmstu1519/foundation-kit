package org.bmstu1519.kmptemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform