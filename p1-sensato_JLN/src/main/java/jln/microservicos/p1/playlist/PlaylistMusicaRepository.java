package jln.microservicos.p1.playlist;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PlaylistMusicaRepository extends CrudRepository<PlaylistMusica, Integer> {

    Iterable<PlaylistMusica> findByPlaylistId(Integer playlistId);

    Optional<PlaylistMusica> findByPlaylistIdAndMusicaId(Integer playlistId, Integer musicaId);

}
