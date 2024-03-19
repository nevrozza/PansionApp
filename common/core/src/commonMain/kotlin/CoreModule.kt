//import database.databaseModule
import json.serializationModule
import ktor.ktorModule
import org.kodein.di.DI
import settings.settingsInjectModule

val coreModule = DI.Module("coreModule") {
    importAll(
        ktorModule,
        serializationModule,
//        databaseModule,
        settingsInjectModule
    )
}