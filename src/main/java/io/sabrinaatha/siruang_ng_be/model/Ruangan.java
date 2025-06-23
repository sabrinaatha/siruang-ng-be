package io.sabrinaatha.siruang_ng_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ruangan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE ruangan SET is_deleted = true WHERE id_ruangan=?")
@Where(clause = "is_deleted=false")
public class Ruangan {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID idRuangan;

    @Column(name = "nama_ruangan", nullable = false)
    private String namaRuangan;

    @Column(name = "status_ruangan", nullable = false)
    private String statusRuangan;

    @Column(name = "tipe_ruangan", nullable = false)
    private String tipeRuangan;

    @OneToMany(mappedBy = "ruangan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Peminjaman> daftarPeminjaman;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public boolean getDeleted() {
        return isDeleted;
    }
}
