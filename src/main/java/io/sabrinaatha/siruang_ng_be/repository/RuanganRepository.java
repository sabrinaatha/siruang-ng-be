package io.sabrinaatha.siruang_ng_be.repository;

import io.sabrinaatha.siruang_ng_be.model.Ruangan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RuanganRepository extends JpaRepository<Ruangan, UUID> {
}
