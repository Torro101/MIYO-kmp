import SwiftUI

/// Host UI for the Miyo KMP `shared` framework.
/// On a Mac after linking shared.framework:
///   import shared
///   and replace DemoStore with MiyoShared / MiyoRuntime calls.
struct ContentView: View {
    @StateObject private var store = DemoStore()

    var body: some View {
        TabView {
            NavigationStack {
                LibraryView(store: store)
            }
            .tabItem { Label("Library", systemImage: "books.vertical") }

            NavigationStack {
                FavoritesView(store: store)
            }
            .tabItem { Label("Favorites", systemImage: "heart") }

            NavigationStack {
                HistoryView(store: store)
            }
            .tabItem { Label("History", systemImage: "clock") }

            NavigationStack {
                SettingsView(store: store)
            }
            .tabItem { Label("Settings", systemImage: "gearshape") }
        }
        .onAppear { store.bootstrap() }
    }
}

// MARK: - Demo models (mirror shared DTOs until framework is linked)

struct DemoManga: Identifiable, Hashable {
    let id: Int64
    var title: String
    var author: String
    var state: String
    var description: String
    var totalChapters: Int
    var readChapters: Int
    var isFavorite: Bool
    var rating: Float

    var progress: Float {
        totalChapters > 0 ? Float(readChapters) / Float(totalChapters) : 0
    }
}

struct DemoChapter: Identifiable, Hashable {
    let id: Int64
    let mangaId: Int64
    let title: String
    let number: Float
    var isRead: Bool
}

struct DemoHistory: Identifiable, Hashable {
    var id: Int64 { mangaId }
    let mangaId: Int64
    let chapterId: Int64
    let page: Int
    let totalPages: Int
    let title: String
}

/// Local stand-in that matches SampleCatalog so Xcode builds without the framework.
/// Replace body of bootstrap/refresh with:
///   MiyoIosBootstrap.shared.start()
///   library = MiyoShared.shared.library().map { ... }
final class DemoStore: ObservableObject {
    @Published var library: [DemoManga] = []
    @Published var favorites: [DemoManga] = []
    @Published var history: [DemoHistory] = []
    @Published var statusLine: String = "Miyo iOS shell"
    @Published var query: String = ""
    @Published var sharedLinked: Bool = false

    func bootstrap() {
        // When shared.framework is linked, switch to:
        // MiyoIosBootstrap.shared.start()
        // statusLine = MiyoIosBootstrap.shared.statusLine()
        // sharedLinked = true
        // refreshFromShared()
        statusLine = "Miyo iOS shell · demo data (link shared.framework on Mac)"
        library = Self.sample
        favorites = library.filter(\.isFavorite)
        history = [
            DemoHistory(mangaId: 1, chapterId: 1003, page: 8, totalPages: 20, title: "Aurora Library"),
            DemoHistory(mangaId: 4, chapterId: 4010, page: 3, totalPages: 20, title: "Paper Crane Express"),
        ]
    }

    var filteredLibrary: [DemoManga] {
        let q = query.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        guard !q.isEmpty else { return library }
        return library.filter {
            $0.title.lowercased().contains(q) || $0.author.lowercased().contains(q)
        }
    }

    func toggleFavorite(_ id: Int64) {
        // Shared: _ = MiyoShared.shared.toggleFavorite(mangaId: id)
        guard let idx = library.firstIndex(where: { $0.id == id }) else { return }
        library[idx].isFavorite.toggle()
        favorites = library.filter(\.isFavorite)
    }

    func chapters(for mangaId: Int64) -> [DemoChapter] {
        guard let m = library.first(where: { $0.id == mangaId }) else { return [] }
        return (1...m.totalChapters).map { n in
            DemoChapter(
                id: mangaId * 1000 + Int64(n),
                mangaId: mangaId,
                title: "Chapter \(n)",
                number: Float(n),
                isRead: n <= m.readChapters
            )
        }
    }

    private static let sample: [DemoManga] = [
        .init(id: 1, title: "Aurora Library", author: "Miyo Demo", state: "ONGOING",
              description: "Sample ongoing series for shared KMP validation.",
              totalChapters: 12, readChapters: 3, isFavorite: true, rating: 4.6),
        .init(id: 2, title: "Harbor Lights", author: "Coast Studio", state: "COMPLETED",
              description: "Completed demo title.",
              totalChapters: 24, readChapters: 24, isFavorite: false, rating: 4.2),
        .init(id: 3, title: "Circuit Garden", author: "Pixel Grove", state: "HIATUS",
              description: "Hiatus demo entry.",
              totalChapters: 8, readChapters: 1, isFavorite: false, rating: 3.9),
        .init(id: 4, title: "Paper Crane Express", author: "Fold Works", state: "ONGOING",
              description: "Search and favorites demo.",
              totalChapters: 40, readChapters: 10, isFavorite: true, rating: 4.8),
    ]
}

struct LibraryView: View {
    @ObservedObject var store: DemoStore

    var body: some View {
        List(store.filteredLibrary) { item in
            NavigationLink(value: item) {
                MangaRow(manga: item)
            }
        }
        .navigationTitle("Library")
        .searchable(text: $store.query, prompt: "Search titles")
        .navigationDestination(for: DemoManga.self) { manga in
            MangaDetailView(store: store, mangaId: manga.id)
        }
    }
}

struct FavoritesView: View {
    @ObservedObject var store: DemoStore

    var body: some View {
        List(store.favorites) { item in
            NavigationLink(value: item) {
                MangaRow(manga: item)
            }
        }
        .navigationTitle("Favorites")
        .navigationDestination(for: DemoManga.self) { manga in
            MangaDetailView(store: store, mangaId: manga.id)
        }
        .overlay {
            if store.favorites.isEmpty {
                ContentUnavailableView("No favorites", systemImage: "heart", description: Text("Star titles in the library."))
            }
        }
    }
}

struct HistoryView: View {
    @ObservedObject var store: DemoStore

    var body: some View {
        List(store.history) { h in
            VStack(alignment: .leading, spacing: 4) {
                Text(h.title).font(.headline)
                Text("Ch \(h.chapterId % 1000) · page \(h.page)/\(h.totalPages)")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
        }
        .navigationTitle("History")
    }
}

struct SettingsView: View {
    @ObservedObject var store: DemoStore

    var body: some View {
        List {
            Section("Shared KMP") {
                LabeledContent("Status", value: store.statusLine)
                LabeledContent("Framework linked", value: store.sharedLinked ? "yes" : "not yet")
                LabeledContent("Library count", value: "\(store.library.count)")
                LabeledContent("Favorites", value: "\(store.favorites.count)")
            }
            Section("Build on Mac") {
                Text("1. ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64")
                Text("2. Link shared.framework in Xcode")
                Text("3. import shared · MiyoIosBootstrap.shared.start()")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
        .navigationTitle("Settings")
    }
}

struct MangaRow: View {
    let manga: DemoManga

    var body: some View {
        HStack(spacing: 12) {
            RoundedRectangle(cornerRadius: 8)
                .fill(.tertiary)
                .frame(width: 44, height: 62)
                .overlay { Image(systemName: "book.closed").foregroundStyle(.secondary) }
            VStack(alignment: .leading, spacing: 4) {
                Text(manga.title).font(.headline)
                Text(manga.author).font(.subheadline).foregroundStyle(.secondary)
                ProgressView(value: Double(manga.progress))
                    .progressViewStyle(.linear)
            }
            if manga.isFavorite {
                Image(systemName: "heart.fill").foregroundStyle(.pink)
            }
        }
        .padding(.vertical, 4)
    }
}

struct MangaDetailView: View {
    @ObservedObject var store: DemoStore
    let mangaId: Int64

    private var manga: DemoManga? { store.library.first { $0.id == mangaId } }

    var body: some View {
        List {
            if let manga {
                Section {
                    Text(manga.description)
                    LabeledContent("Status", value: manga.state)
                    LabeledContent("Rating", value: String(format: "%.1f", manga.rating))
                    LabeledContent("Progress", value: "\(manga.readChapters)/\(manga.totalChapters)")
                    Button(manga.isFavorite ? "Remove favorite" : "Add favorite") {
                        store.toggleFavorite(manga.id)
                    }
                }
                Section("Chapters") {
                    ForEach(store.chapters(for: manga.id)) { ch in
                        HStack {
                            Text(ch.title)
                            Spacer()
                            if ch.isRead {
                                Image(systemName: "checkmark.circle.fill").foregroundStyle(.green)
                            }
                        }
                    }
                }
            }
        }
        .navigationTitle(manga?.title ?? "Details")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    ContentView()
}
