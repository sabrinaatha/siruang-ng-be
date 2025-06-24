package io.sabrinaatha.siruang_ng_be.service;

import io.sabrinaatha.siruang_ng_be.exception.BadRequestException;
import io.sabrinaatha.siruang_ng_be.exception.NotFoundException;
import io.sabrinaatha.siruang_ng_be.model.Peminjaman;
import io.sabrinaatha.siruang_ng_be.model.Ruangan;
import io.sabrinaatha.siruang_ng_be.payload.request.RuanganRequestDTO;
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
        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(idRuangan);
        Ruangan ruangan = optionalRuangan.orElse(null);
        if (ruangan == null) {
            throw new NotFoundException("Ruangan dengan id " + idRuangan + " tidak ada.");
        }
        RuanganResponseDTO dto = ruanganToRuanganResponseDTO(ruangan);
        return dto;
    }

    @Override
    public RuanganResponseDTO addRuangan(RuanganRequestDTO ruanganRequestDTO) {
        Optional<Ruangan> checkExist = ruanganRepository.findByNamaRuangan(ruanganRequestDTO.getNamaRuangan());
        Ruangan ExistingRoom = checkExist.orElse(null);

        if (ExistingRoom != null) {
            throw new BadRequestException("Ruangan dengan nama " + ruanganRequestDTO.getNamaRuangan() + " sudah ada.");
        }

        Ruangan ruangan = new Ruangan();
        if (ruanganRequestDTO.getStatusRuangan().toLowerCase().equals("enabled") || ruanganRequestDTO.getStatusRuangan().toLowerCase().equals("disabled")) {
            ruangan.setStatusRuangan(ruanganRequestDTO.getStatusRuangan().toUpperCase());
        } else {
            throw new BadRequestException("Ruangan dengan status " + ruanganRequestDTO.getStatusRuangan() + " tidak sesuai.");
        }

        ruangan.setTipeRuangan(ruanganRequestDTO.getTipeRuangan());
        ruangan.setNamaRuangan(ruanganRequestDTO.getNamaRuangan());

        var newRuangan = ruanganRepository.save(ruangan);
        return ruanganToRuanganResponseDTO(newRuangan);
    }

    @Override
    public RuanganResponseDTO updateRuangan(UUID idRuangan, RuanganRequestDTO ruanganRequestDTO) {
        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(idRuangan);
        Ruangan ruangan = optionalRuangan.orElseThrow(() -> new NotFoundException("Ruangan dengan ID: " + idRuangan + " tidak ditemukan "));

        Optional<Ruangan> checkExist = ruanganRepository.findByNamaRuangan(ruanganRequestDTO.getNamaRuangan());
        Ruangan existingRoom = checkExist.orElse(null);

        if ((existingRoom != null) && (existingRoom.getNamaRuangan().toLowerCase().equals(ruanganRequestDTO.getNamaRuangan()))) {
            throw new BadRequestException("Nama ruangan yang diubah menjadi " + ruanganRequestDTO.getNamaRuangan() + " sudah ada.");
        }

        ruangan.setNamaRuangan(ruanganRequestDTO.getNamaRuangan());
        ruangan.setTipeRuangan(ruanganRequestDTO.getTipeRuangan());
        if (ruanganRequestDTO.getStatusRuangan().toLowerCase().equals("enabled") || ruanganRequestDTO.getStatusRuangan().toLowerCase().equals("disabled")) {
            ruangan.setStatusRuangan(ruanganRequestDTO.getStatusRuangan().toUpperCase());
        } else {
            throw new BadRequestException("Ruangan dengan status " + ruanganRequestDTO.getStatusRuangan() + " tidak sesuai.");
        }

        try {
            var updatedRuangan = ruanganRepository.save(ruangan);
            return ruanganToRuanganResponseDTO(updatedRuangan);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Ruangan: " + e.getMessage(), e);
        }
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
