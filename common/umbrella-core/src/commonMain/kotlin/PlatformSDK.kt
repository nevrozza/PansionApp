import di.Inject
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.singleton

object PlatformSDK {

    fun init(
        configuration: PlatformConfiguration,
        cConfiguration: CommonPlatformConfiguration
    ) {
        val umbrellaModule = DI.Module(
            name = "umbrella",
            init = {
                bind<PlatformConfiguration>() with singleton { configuration }
                bind<CommonPlatformConfiguration>() with singleton { cConfiguration }
            }
        )

        Inject.createDependencies(
            DI {
                importAll(
                    umbrellaModule,
                    coreModule,
                    settingsModule,
                    authModule,
                    adminModule,
                    mainModule
                )
            }.direct
        )
    }

}