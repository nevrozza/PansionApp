package view

import android.annotation.SuppressLint
import android.os.Build

@SuppressLint("AnnotateVersionCheck")
actual fun isCanInDynamic(): Boolean  = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
