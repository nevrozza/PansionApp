import ktor.KtorMainRemoteDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

val mainModule = DI.Module("mainModule") {

    bind<KtorMainRemoteDataSource>() with provider {
        KtorMainRemoteDataSource(instance())
    }

    bind<MainRepository>() with singleton {
        MainRepositoryImpl(instance())
    }
}