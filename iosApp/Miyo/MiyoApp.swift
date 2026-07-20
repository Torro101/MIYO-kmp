import SwiftUI

@main
struct MiyoApp: App {
    init() {
        // After linking shared.framework from KMP:
        // MiyoShared.shared.initialize()
        // Exact ObjC names come from the generated Kotlin/Native headers.
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
