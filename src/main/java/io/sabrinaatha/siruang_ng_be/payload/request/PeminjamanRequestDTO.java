package io.sabrinaatha.siruang_ng_be.payload.request;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeminjamanRequestDTO {
    private UUID idRuangan;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private String statusPeminjaman;
    private String peminjam;
    private String organisasi;
    private String tujuan;
    private String jenisKegiatan;
    private String kegiatan;
    private Integer jumlahPeserta;
    private LocalDate tanggalDiverifikasi;
    private LocalDate tanggalDitolak;
    private LocalDate tanggalDisetujui;
    private LocalDate tanggalDibatalkan;
}