import ktor.KtorAuthRemoteDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton
import settings.SettingsAuthDataSource

val authModule = DI.Module("authModule") {
    bind<SettingsAuthDataSource>() with provider {
        SettingsAuthDataSource(instance())
    }

    bind<KtorAuthRemoteDataSource>() with provider {
        KtorAuthRemoteDataSource(instance())
    }

    bind<AuthRepository>() with singleton {
        AuthRepositoryImpl(instance(), instance())
    }
}