package io.sabrinaatha.siruang_ng_be.controller;

import io.sabrinaatha.siruang_ng_be.payload.response.BaseResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.RuanganResponseDTO;
import io.sabrinaatha.siruang_ng_be.service.RuanganService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ruangan")
public class RuanganController {

    @Autowired
    private RuanganService ruanganService;

    // GET all
    @GetMapping("/all")
    public ResponseEntity<?> viewAllRuangan() {
        var baseResponseDTO = new BaseResponseDTO<List<RuanganResponseDTO>>();

        try {
            // Mengambil daftar ruangan dari service
            List<RuanganResponseDTO> listRuangan = ruanganService.getAllRuangan();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(listRuangan);
            baseResponseDTO.setMessage("Seluruh ruangan berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Permintaan tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Terjadi kesalahan internal pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get detail ruangan
    @GetMapping("/detail/{idRuangan}")
    public ResponseEntity<?> viewDetailRuangan(@PathVariable UUID idRuangan) {
        var baseResponseDTO = new BaseResponseDTO<RuanganResponseDTO>();

        try {
            // Mengambil detail ruangan dari service
            RuanganResponseDTO ruangan = ruanganService.getRuanganById(idRuangan);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(ruangan);
            baseResponseDTO.setMessage("Ruangan dengan id " + idRuangan + " berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Permintaan tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Terjadi kesalahan internal pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Soft delete ruangan data
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRuangan(@Valid @PathVariable("id") UUID id) {
        var baseResponseDTO = new BaseResponseDTO<RuanganResponseDTO>();
        try {
            // Menghapus ruangan menggunakan soft delete
            ruanganService.deleteRuangan(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Ruangan berhasil dihapus");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Ruangan dengan id " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (ConstraintViolationException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal menghapus ruangan karena " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal menghapus ruangan karena " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Ubah status ruangan
    @PostMapping("/change/status/{id}")
    public ResponseEntity<?> changeStatusRuangan(@Valid @PathVariable("id") UUID id, @RequestParam("status") String status) {
        var baseResponseDTO = new BaseResponseDTO<RuanganResponseDTO>();
        try {
            // Ubah status ruangan
            RuanganResponseDTO ruangan = ruanganService.changeStatusRuangan(id, status);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(ruangan);
            baseResponseDTO.setMessage("Status ruangan berhasil diubah");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Ruangan dengan id " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (ConstraintViolationException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal mengubah status ruangan karena " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal mengubah status ruangan karena " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Jadwal Pemakaian Ruangan
    @GetMapping("/jadwal/{idRuangan}")
    public ResponseEntity<?> viewJadwalRuangan(@PathVariable UUID idRuangan) {
        var baseResponseDTO = new BaseResponseDTO<List<PeminjamanResponseDTO>>();

        try {
            // Mengambil daftar jadwal ruangan dari service
            List<PeminjamanResponseDTO> listJadwal = ruanganService.getAllJadwalRuangan(idRuangan);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(listJadwal);
            baseResponseDTO.setMessage("Seluruh jadwal ruangan berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Permintaan tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Terjadi kesalahan internal pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
