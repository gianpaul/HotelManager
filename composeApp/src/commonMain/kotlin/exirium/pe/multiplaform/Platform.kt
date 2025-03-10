package exirium.pe.multiplaform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform