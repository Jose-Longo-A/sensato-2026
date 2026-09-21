package jln.microservicos.p1.reproducao;

import org.springframework.data.repository.CrudRepository;

public interface ReproducaoRepository extends CrudRepository<Reproducao, Integer> {

    Iterable<Reproducao> findByPlaylistId(Integer playlistId);

    long countByPlaylistId(Integer playlistId);

}
