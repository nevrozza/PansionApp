import SwiftUI

@main
struct iOSApp: App {
//    init() {
//            PlatformSDK().doInit(configuration: PlatformConfiguration())
//        }
        
    var body: some Scene {
        WindowGroup {
    //            ContentView()
            ZStack {
                MainComposeView().ignoresSafeArea()
            }
        }
    }
}
