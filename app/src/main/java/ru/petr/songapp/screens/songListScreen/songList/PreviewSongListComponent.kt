package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchData
import ru.petr.songapp.screens.songListScreen.songList.scrollbar.ScrollbarComponent

class PreviewSongListComponent(
    override val songItems: Value<List<SongListComponent.SongItem>>,
    override val searchIsActive: Value<Boolean>,
    override val fullTextSearchData: FullSearchData,
    override val scrollbar: ScrollbarComponent
) : SongListComponent {
    override fun onSongClicked(id: Int) {}

    override fun onFullTextSearch() {}
}