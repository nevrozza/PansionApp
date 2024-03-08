import ktor.KtorAdminRemoteDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

val adminModule = DI.Module("adminModule") {

    bind<KtorAdminRemoteDataSource>() with provider {
        KtorAdminRemoteDataSource(instance())
    }

    bind<AdminRepository>() with singleton {
        AdminRepositoryImpl(instance())
    }
}