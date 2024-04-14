import ktor.KtorJournalRemoteDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton
//import settings.SettingsAuthDataSource

val journalModule = DI.Module("journalModule") {
//    bind<SettingsAuthDataSource>() with provider {
//        SettingsAuthDataSource(instance())
//    }

    bind<KtorJournalRemoteDataSource>() with provider {
        KtorJournalRemoteDataSource(instance())
    }

    bind<JournalRepository>() with singleton {
        JournalRepositoryImpl(instance())
    }
}