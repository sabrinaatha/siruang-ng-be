package io.sabrinaatha.siruang_ng_be.service;

import io.sabrinaatha.siruang_ng_be.exception.BadRequestException;
import io.sabrinaatha.siruang_ng_be.model.Peminjaman;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.RuanganResponseDTO;
import io.sabrinaatha.siruang_ng_be.repository.PeminjamanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PeminjamanServiceImpl implements PeminjamanService {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Override
    public List<PeminjamanResponseDTO> getAllPeminjaman() {
        List<Peminjaman> listPeminjaman = peminjamanRepository.findAll();
        var listPeminjamanResponseDTO = new ArrayList<PeminjamanResponseDTO>();

        for (Peminjaman peminjaman : listPeminjaman) {
            PeminjamanResponseDTO dto = peminjamanToPeminjamanResponseDTO(peminjaman);
            listPeminjamanResponseDTO.add(dto);
        }
        return listPeminjamanResponseDTO;
    }

    @Override
    public PeminjamanResponseDTO getPeminjamanById(UUID idPeminjaman) {
        Optional<Peminjaman> peminjaman = peminjamanRepository.findById(idPeminjaman);
        PeminjamanResponseDTO dto = peminjamanToPeminjamanResponseDTO(peminjaman.orElse(null));
        return dto;
    }

    @Override
    public PeminjamanResponseDTO ubahStatusPeminjaman(UUID idPeminjaman, String status) {
        Optional<Peminjaman> optionalPeminjaman = peminjamanRepository.findById(idPeminjaman);
        if (optionalPeminjaman.isPresent()) {
            Peminjaman existingPeminjaman = optionalPeminjaman.orElse(null);

            LocalDate today = LocalDate.now();
            if (status.toLowerCase().equals("diverifikasi")) {
                existingPeminjaman.setTanggalDiverifikasi(today);
            } else if (status.toLowerCase().equals("ditolak")) {
                existingPeminjaman.setTanggalDitolak(today);
            } else if (status.toLowerCase().equals("disetujui")) {
                existingPeminjaman.setTanggalDisetujui(today);
            } else {
                throw new BadRequestException("Status tidak tepat");
            }

            existingPeminjaman.setStatusPeminjaman(status.toUpperCase());

            peminjamanRepository.save(existingPeminjaman);
            PeminjamanResponseDTO dto = peminjamanToPeminjamanResponseDTO(existingPeminjaman);
            return dto;
        } else {
            throw new EntityNotFoundException(
                    String.format("Peminjaman dengan ID %s tidak ditemukan", idPeminjaman));
        }
    }

    @Override
    public List<PeminjamanResponseDTO> getListPersetujuanPeminjaman() {
        List<Peminjaman> listPeminjamanDiverifikasi = peminjamanRepository.findByStatusPeminjaman("DIVERIFIKASI");
        List<Peminjaman> listPeminjamanDisetujui = peminjamanRepository.findByStatusPeminjaman("DISETUJUI");
        List<Peminjaman> combinedUnique = Stream.concat(
                        listPeminjamanDiverifikasi.stream(),
                        listPeminjamanDisetujui.stream()
                )
                .distinct()
                .collect(Collectors.toList());

        var listPeminjamanResponseDTO = new ArrayList<PeminjamanResponseDTO>();

        for (Peminjaman peminjaman : combinedUnique) {
            PeminjamanResponseDTO dto = peminjamanToPeminjamanResponseDTO(peminjaman);
            listPeminjamanResponseDTO.add(dto);
        }
        return listPeminjamanResponseDTO;
    }

    @Override
    public List<PeminjamanResponseDTO> getListVerifikasiPeminjaman() {
        List<Peminjaman> listPeminjamanDiverifikasi = peminjamanRepository.findByStatusPeminjaman("DIVERIFIKASI");
        List<Peminjaman> listPeminjamanDiajukan = peminjamanRepository.findByStatusPeminjaman("DIAJUKAN");
        List<Peminjaman> combinedUnique = Stream.concat(
                        listPeminjamanDiverifikasi.stream(),
                        listPeminjamanDiajukan.stream()
                )
                .distinct()
                .collect(Collectors.toList());

        var listPeminjamanResponseDTO = new ArrayList<PeminjamanResponseDTO>();

        for (Peminjaman peminjaman : combinedUnique) {
            PeminjamanResponseDTO dto = peminjamanToPeminjamanResponseDTO(peminjaman);
            listPeminjamanResponseDTO.add(dto);
        }
        return listPeminjamanResponseDTO;
    }

    @Override
    public PeminjamanResponseDTO peminjamanToPeminjamanResponseDTO(Peminjaman peminjaman) {
        PeminjamanResponseDTO dto = new PeminjamanResponseDTO();
        dto.setIdPeminjaman(peminjaman.getIdPeminjaman());
        dto.setStatusPeminjaman(peminjaman.getStatusPeminjaman());
        dto.setKegiatan(peminjaman.getKegiatan());
        dto.setOrganisasi(peminjaman.getOrganisasi());
        dto.setJenisKegiatan(peminjaman.getJenisKegiatan());
        dto.setJumlahPeserta(peminjaman.getJumlahPeserta());
        dto.setTujuan(peminjaman.getTujuan());
        dto.setTanggalPinjam(peminjaman.getTanggalPinjam());
        dto.setTanggalMulai(peminjaman.getTanggalMulai());
        dto.setTanggalSelesai(peminjaman.getTanggalSelesai());
        dto.setWaktuMulai(peminjaman.getWaktuMulai());
        dto.setWaktuSelesai(peminjaman.getWaktuSelesai());
        dto.setPeminjam(peminjaman.getPeminjam());
        dto.setTanggalDiverifikasi(peminjaman.getTanggalDiverifikasi());
        dto.setTanggalDitolak(peminjaman.getTanggalDitolak());
        dto.setTanggalDisetujui(peminjaman.getTanggalDisetujui());
        dto.setDeleted(peminjaman.getDeleted());

        if (peminjaman.getRuangan() != null) {
            RuanganResponseDTO ruanganDto = new RuanganResponseDTO();
            ruanganDto.setIdRuangan(peminjaman.getRuangan().getIdRuangan());
            ruanganDto.setNamaRuangan(peminjaman.getRuangan().getNamaRuangan());
            dto.setRuangan(ruanganDto);
        }

        return dto;
    }
}