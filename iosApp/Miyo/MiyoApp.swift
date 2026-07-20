import SwiftUI

@main
struct MiyoApp: App {
    init() {
        // After linking shared.framework:
        // import shared
        // MiyoIosBootstrap.shared.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
