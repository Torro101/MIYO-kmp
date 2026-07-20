import SwiftUI

/// Placeholder host UI for the Miyo KMP `shared` framework.
/// Wire `import shared` once the framework is produced on a Mac:
///   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
struct ContentView: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                Image(systemName: "book.pages")
                    .font(.system(size: 56))
                    .foregroundStyle(.tint)
                Text("Miyo")
                    .font(.largeTitle.bold())
                Text("Kotlin Multiplatform shell")
                    .font(.headline)
                    .foregroundStyle(.secondary)
                Text(
                    "This iOS app hosts the shared KMP module. " +
                    "Domain models, expect/actual platform bridges, and use-cases live in :shared. " +
                    "Full reader UI parity with Android is incremental."
                )
                .font(.body)
                .multilineTextAlignment(.center)
                .padding(.horizontal)

                VStack(alignment: .leading, spacing: 8) {
                    Label("shared.framework from :shared", systemImage: "shippingbox")
                    Label("Android app is :app thin shell", systemImage: "iphone")
                    Label("Native C++ stays Android JNI for now", systemImage: "cpu")
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(.thinMaterial, in: RoundedRectangle(cornerRadius: 12))
                .padding(.horizontal)
            }
            .padding()
            .navigationTitle("Miyo")
        }
    }
}

#Preview {
    ContentView()
}
