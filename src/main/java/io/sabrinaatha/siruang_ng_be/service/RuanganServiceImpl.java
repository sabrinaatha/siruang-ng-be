package io.sabrinaatha.siruang_ng_be.service;

import io.sabrinaatha.siruang_ng_be.model.Peminjaman;
import io.sabrinaatha.siruang_ng_be.model.Ruangan;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.RuanganResponseDTO;
import io.sabrinaatha.siruang_ng_be.repository.RuanganRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RuanganServiceImpl implements RuanganService{

    @Autowired
    private RuanganRepository ruanganRepository;

    @Autowired
    private PeminjamanService peminjamanService;

    @Override
    public List<RuanganResponseDTO> getAllRuangan() {
        List<Ruangan> listRuangan = ruanganRepository.findAll();
        var listRuanganResponseDTO = new ArrayList<RuanganResponseDTO>();

        for (Ruangan ruangan : listRuangan) {
            RuanganResponseDTO dto = ruanganToRuanganResponseDTO(ruangan);
            listRuanganResponseDTO.add(dto);
        }
        return listRuanganResponseDTO;
    }

    @Override
    public RuanganResponseDTO getRuanganById(UUID idRuangan) {
        Optional<Ruangan> ruangan = ruanganRepository.findById(idRuangan);
        RuanganResponseDTO dto = ruanganToRuanganResponseDTO(ruangan.orElse(null));
        return dto;
    }

    @Override
    public void deleteRuangan(UUID idRuangan) {
        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(idRuangan);
        if (optionalRuangan.isPresent()) {
            Ruangan existingRuangan = optionalRuangan.orElse(null);
            ruanganRepository.delete(existingRuangan);
        } else {
            throw new EntityNotFoundException(
                    String.format("Ruangan dengan ID %s tidak ditemukan", idRuangan));
        }
    }

    @Override
    public RuanganResponseDTO changeStatusRuangan(UUID idRuangan, String status) {
        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(idRuangan);
        if (optionalRuangan.isPresent()) {
            Ruangan existingRuangan = optionalRuangan.orElse(null);
            existingRuangan.setStatusRuangan(status);
            ruanganRepository.save(existingRuangan);
            RuanganResponseDTO dto = ruanganToRuanganResponseDTO(existingRuangan);
            return dto;
        } else {
            throw new EntityNotFoundException(
                    String.format("Ruangan dengan ID %s tidak ditemukan", idRuangan));
        }
    }

    @Override
    public List<PeminjamanResponseDTO> getAllJadwalRuangan(UUID idRuangan) {
        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(idRuangan);
        Ruangan ruangan = optionalRuangan.orElse(null);

        var listPeminjaman = ruangan.getDaftarPeminjaman();
        var listPeminjamanResponseDTO = new ArrayList<PeminjamanResponseDTO>();

        for (Peminjaman peminjaman : listPeminjaman) {
            PeminjamanResponseDTO dto = peminjamanService.peminjamanToPeminjamanResponseDTO(peminjaman);
            listPeminjamanResponseDTO.add(dto);
        }
        return listPeminjamanResponseDTO;
    }

    public RuanganResponseDTO ruanganToRuanganResponseDTO(Ruangan ruangan) {
        RuanganResponseDTO dto = new RuanganResponseDTO();
        dto.setIdRuangan(ruangan.getIdRuangan());
        dto.setNamaRuangan(ruangan.getNamaRuangan());
        dto.setStatusRuangan(ruangan.getStatusRuangan());
        dto.setTipeRuangan(ruangan.getTipeRuangan());
        dto.setDeleted(ruangan.getDeleted());
        return dto;
    }
}
