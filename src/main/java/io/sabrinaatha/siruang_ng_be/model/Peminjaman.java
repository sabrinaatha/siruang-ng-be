package io.sabrinaatha.siruang_ng_be.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "peminjaman")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE peminjaman SET is_deleted = true WHERE id_peminjaman=?")
@Where(clause = "is_deleted=false")
public class Peminjaman {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID idPeminjaman;

    @ManyToOne
    @JoinColumn(name = "ruangan_id", nullable = false)
    private Ruangan ruangan;

    @Column(name = "tanggal_pinjam", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalPinjam;

    @Column(name = "tanggal_mulai", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;

    @Column(name = "tanggal_selesai", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;

    @Column(name = "waktu_mulai", nullable = false)
    private LocalTime waktuMulai;

    @Column(name = "waktu_selesai", nullable = false)
    private LocalTime waktuSelesai;

    @Column(name = "status_peminjaman", nullable = false)
    private String statusPeminjaman;

    @Column(name = "peminjam", nullable = false)
    private String peminjam;

    @Column(name = "organisasi", nullable = false)
    private String organisasi;

    @Column(name = "tujuan", nullable = false)
    private String tujuan;

    @Column(name = "jenis_kegiatan", nullable = false)
    private String jenisKegiatan;

    @Column(name = "kegiatan", nullable = false)
    private String kegiatan;

    @Column(name = "jumlah_peserta", nullable = false)
    private Integer jumlahPeserta;

    @Column(name = "tanggal_diverifikasi")
    private LocalDate tanggalDiverifikasi;

    @Column(name = "tanggal_disetujui")
    private LocalDate tanggalDisetujui;

    @Column(name = "tanggal_ditolak")
    private LocalDate tanggalDitolak;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public boolean getDeleted() {
        return isDeleted;
    }
}
