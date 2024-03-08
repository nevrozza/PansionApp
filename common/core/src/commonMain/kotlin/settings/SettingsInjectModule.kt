package settings

import com.russhwolf.settings.Settings
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal val settingsInjectModule = DI.Module("settingsInjectModule") {
    bind<Settings>() with singleton { Settings() }
}