package io.sabrinaatha.siruang_ng_be.service;

import io.sabrinaatha.siruang_ng_be.model.Peminjaman;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import java.util.List;
import java.util.UUID;

public interface PeminjamanService {
    List<PeminjamanResponseDTO> getAllPeminjaman();
    PeminjamanResponseDTO getPeminjamanById(UUID idPeminjaman);
    PeminjamanResponseDTO ubahStatusPeminjaman(UUID idPeminjaman, String status);
    List<PeminjamanResponseDTO> getListPersetujuanPeminjaman();
    List<PeminjamanResponseDTO> getListVerifikasiPeminjaman();
    PeminjamanResponseDTO peminjamanToPeminjamanResponseDTO(Peminjaman peminjaman);
}
