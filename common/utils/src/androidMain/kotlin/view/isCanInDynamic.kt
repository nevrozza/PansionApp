package view

import android.os.Build

actual fun isCanInDynamic(): Boolean  = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
