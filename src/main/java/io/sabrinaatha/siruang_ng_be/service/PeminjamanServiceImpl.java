package io.sabrinaatha.siruang_ng_be.service;

import io.sabrinaatha.siruang_ng_be.exception.BadRequestException;
import io.sabrinaatha.siruang_ng_be.exception.NotFoundException;
import io.sabrinaatha.siruang_ng_be.model.Peminjaman;
import io.sabrinaatha.siruang_ng_be.model.Ruangan;
import io.sabrinaatha.siruang_ng_be.payload.request.PeminjamanRequestDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.RuanganResponseDTO;
import io.sabrinaatha.siruang_ng_be.repository.PeminjamanRepository;
import io.sabrinaatha.siruang_ng_be.repository.RuanganRepository;
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

    @Autowired
    private RuanganRepository ruanganRepository;

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
        Optional<Peminjaman> optionalPeminjaman = peminjamanRepository.findById(idPeminjaman);
        Peminjaman peminjaman = optionalPeminjaman.orElse(null);
        if (peminjaman == null) {
            throw new NotFoundException("Peminjaman dengan id " + idPeminjaman + " tidak ada.");
        }
        PeminjamanResponseDTO dto = peminjamanToPeminjamanResponseDTO(peminjaman);
        return dto;
    }

    @Override
    public PeminjamanResponseDTO addPeminjaman(PeminjamanRequestDTO peminjamanRequestDTO) {
        Peminjaman peminjaman = new Peminjaman();

        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(peminjamanRequestDTO.getIdRuangan());
        Ruangan ruangan = optionalRuangan.orElse(null);

        if (ruangan == null) {
            throw new NotFoundException("Ruangan dengan id " + peminjamanRequestDTO.getIdRuangan() + " tidak ada.");
        }

        List<Peminjaman> existingPeminjaman = peminjamanRepository.findAll().stream()
                .filter(p -> p.getRuangan().getIdRuangan().equals(peminjamanRequestDTO.getIdRuangan()))
                .filter(p -> List.of("DISETUJUI").contains(p.getStatusPeminjaman()))
                .collect(Collectors.toList());

        boolean isConflict = existingPeminjaman.stream().anyMatch(p -> {
            boolean tanggalBentrok =
                    !p.getTanggalSelesai().isBefore(peminjamanRequestDTO.getTanggalMulai()) &&
                            !p.getTanggalMulai().isAfter(peminjamanRequestDTO.getTanggalSelesai());

            boolean waktuBentrok =
                    !p.getWaktuSelesai().isBefore(peminjamanRequestDTO.getWaktuMulai()) &&
                            !p.getWaktuMulai().isAfter(peminjamanRequestDTO.getWaktuSelesai());

            return tanggalBentrok && waktuBentrok;
        });

        if (isConflict) {
            throw new BadRequestException("Ruangan sudah dipinjam pada waktu yang diminta.");
        }

        peminjaman.setRuangan(ruangan);

        peminjaman.setTanggalMulai(peminjamanRequestDTO.getTanggalMulai());
        peminjaman.setTanggalSelesai(peminjamanRequestDTO.getTanggalSelesai());
        peminjaman.setTanggalPinjam(peminjamanRequestDTO.getTanggalPinjam());

        peminjaman.setWaktuMulai(peminjamanRequestDTO.getWaktuMulai());
        peminjaman.setWaktuSelesai(peminjamanRequestDTO.getWaktuSelesai());

        peminjaman.setKegiatan(peminjamanRequestDTO.getKegiatan());
        peminjaman.setJenisKegiatan(peminjamanRequestDTO.getJenisKegiatan());
        peminjaman.setTujuan(peminjamanRequestDTO.getTujuan());

        peminjaman.setStatusPeminjaman("DIAJUKAN");
        peminjaman.setPeminjam(peminjamanRequestDTO.getPeminjam());
        peminjaman.setOrganisasi(peminjamanRequestDTO.getOrganisasi());
        peminjaman.setJumlahPeserta(peminjamanRequestDTO.getJumlahPeserta());

        peminjaman.setTanggalDiverifikasi(peminjamanRequestDTO.getTanggalDiverifikasi());
        peminjaman.setTanggalDitolak(peminjamanRequestDTO.getTanggalDitolak());
        peminjaman.setTanggalDisetujui(peminjamanRequestDTO.getTanggalDisetujui());
        peminjaman.setTanggalDibatalkan(peminjamanRequestDTO.getTanggalDibatalkan());

        var peminjamanBaru = peminjamanRepository.save(peminjaman);
        return peminjamanToPeminjamanResponseDTO(peminjamanBaru);
    }

    @Override
    public PeminjamanResponseDTO updatePeminjaman(UUID idPeminjaman, PeminjamanRequestDTO peminjamanRequestDTO) {
        Optional<Peminjaman> optionalPeminjaman = peminjamanRepository.findById(idPeminjaman);
        Peminjaman peminjaman = optionalPeminjaman.orElse(null);

        if (peminjaman == null) {
            throw new NotFoundException("Peminjaman dengan id " + idPeminjaman + " tidak ada.");
        }

        Optional<Ruangan> optionalRuangan = ruanganRepository.findById(peminjamanRequestDTO.getIdRuangan());
        Ruangan ruangan = optionalRuangan.orElse(null);

        if (ruangan == null) {
            throw new NotFoundException("Ruangan dengan id " + peminjamanRequestDTO.getIdRuangan() + " tidak ada.");
        }

        List<Peminjaman> existingPeminjaman = peminjamanRepository.findAll().stream()
                .filter(p -> !p.getIdPeminjaman().equals(idPeminjaman))
                .filter(p -> p.getRuangan().getIdRuangan().equals(peminjamanRequestDTO.getIdRuangan()))
                .filter(p -> List.of("DISETUJUI").contains(p.getStatusPeminjaman()))
                .collect(Collectors.toList());

        boolean isConflict = existingPeminjaman.stream().anyMatch(p -> {
            boolean tanggalBentrok =
                    !p.getTanggalSelesai().isBefore(peminjamanRequestDTO.getTanggalMulai()) &&
                            !p.getTanggalMulai().isAfter(peminjamanRequestDTO.getTanggalSelesai());

            boolean waktuBentrok =
                    p.getWaktuMulai().isBefore(peminjamanRequestDTO.getWaktuSelesai()) &&
                            p.getWaktuSelesai().isAfter(peminjamanRequestDTO.getWaktuMulai());

            return tanggalBentrok && waktuBentrok;
        });

        if (isConflict) {
            throw new BadRequestException("Perubahan pada tanggal dan waktu ruangan tidak dapat dilakukan karena ruangan sudah dipinjam pada waktu yang diminta.");
        }

        peminjaman.setRuangan(ruangan);

        peminjaman.setTanggalMulai(peminjamanRequestDTO.getTanggalMulai());
        peminjaman.setTanggalSelesai(peminjamanRequestDTO.getTanggalSelesai());
        peminjaman.setTanggalPinjam(peminjamanRequestDTO.getTanggalPinjam());

        peminjaman.setWaktuMulai(peminjamanRequestDTO.getWaktuMulai());
        peminjaman.setWaktuSelesai(peminjamanRequestDTO.getWaktuSelesai());

        peminjaman.setKegiatan(peminjamanRequestDTO.getKegiatan());
        peminjaman.setJenisKegiatan(peminjamanRequestDTO.getJenisKegiatan());
        peminjaman.setTujuan(peminjamanRequestDTO.getTujuan());

        peminjaman.setStatusPeminjaman(peminjamanRequestDTO.getStatusPeminjaman());
        peminjaman.setPeminjam(peminjamanRequestDTO.getPeminjam());
        peminjaman.setOrganisasi(peminjamanRequestDTO.getOrganisasi());
        peminjaman.setJumlahPeserta(peminjamanRequestDTO.getJumlahPeserta());

        peminjaman.setTanggalDiverifikasi(peminjamanRequestDTO.getTanggalDiverifikasi());
        peminjaman.setTanggalDitolak(peminjamanRequestDTO.getTanggalDitolak());
        peminjaman.setTanggalDisetujui(peminjamanRequestDTO.getTanggalDisetujui());
        peminjaman.setTanggalDibatalkan(peminjamanRequestDTO.getTanggalDibatalkan());

        try {
            var updatedPeminjaman = peminjamanRepository.save(peminjaman);
            return peminjamanToPeminjamanResponseDTO(updatedPeminjaman);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Peminjaman: " + e.getMessage(), e);
        }
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
            } else if (status.toLowerCase().equals("diajukan")) {
                existingPeminjaman.setTanggalDiverifikasi(null);
                existingPeminjaman.setTanggalDitolak(null);
                existingPeminjaman.setTanggalDisetujui(null);
                existingPeminjaman.setTanggalDibatalkan(null);
            } else if (status.toLowerCase().equals("disetujui")) {
                existingPeminjaman.setTanggalDisetujui(today);
            } else if (status.toLowerCase().equals("dibatalkan")) {
                existingPeminjaman.setTanggalDibatalkan(today);
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