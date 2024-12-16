package systems.ajax.codetests.application.extensions

import java.io.File

fun File.isValidFile() = exists() && isFile
