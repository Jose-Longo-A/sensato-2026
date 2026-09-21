package jln.microservicos.p1.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jln.microservicos.p1.playlist.Playlist;

@FeignClient(name = "playlist-client", url = "${app.base-url}")
public interface PlaylistClient {

    @GetMapping("/playlists/{playlistid}")
    Playlist buscar(@PathVariable("playlistid") Integer playlistId);

    @PostMapping("/playlists/{playlistid}/musicas/{musicaId}")
    void adicionarMusica(@PathVariable("playlistid") Integer playlistId, @PathVariable("musicaId") Integer musicaId);

}
