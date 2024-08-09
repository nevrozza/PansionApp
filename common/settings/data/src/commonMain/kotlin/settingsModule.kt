import ktor.KtorSettingsRemoteDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton
import settings.SettingsDataSource

val settingsModule = DI.Module("settingsModule") {
    bind<SettingsRepository>() with singleton {
        SettingsRepositoryImpl(instance(), instance())
    }

    bind<SettingsDataSource>() with provider {
        SettingsDataSource(instance())
    }
    bind<KtorSettingsRemoteDataSource>() with provider {
        KtorSettingsRemoteDataSource(instance())
    }
}