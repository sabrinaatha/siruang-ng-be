package io.sabrinaatha.siruang_ng_be.repository;

import io.sabrinaatha.siruang_ng_be.model.Peminjaman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PeminjamanRepository extends JpaRepository<Peminjaman, UUID> {
    List<Peminjaman> findByStatusPeminjaman(String statusPeminjaman);
}
