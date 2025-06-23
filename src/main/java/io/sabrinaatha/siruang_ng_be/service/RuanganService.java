package io.sabrinaatha.siruang_ng_be.service;

import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.RuanganResponseDTO;

import java.util.List;
import java.util.UUID;

public interface RuanganService {
    List<RuanganResponseDTO> getAllRuangan();
    RuanganResponseDTO getRuanganById(UUID idRuangan);
    void deleteRuangan(UUID idRuangan);
    RuanganResponseDTO changeStatusRuangan(UUID idRuangan, String status);
    List<PeminjamanResponseDTO> getAllJadwalRuangan(UUID idRuangan);
}
