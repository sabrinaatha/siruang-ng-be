package io.sabrinaatha.siruang_ng_be.controller;

import io.sabrinaatha.siruang_ng_be.exception.BadRequestException;
import io.sabrinaatha.siruang_ng_be.exception.NotFoundException;
import io.sabrinaatha.siruang_ng_be.payload.request.RuanganRequestDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.BaseResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.RuanganResponseDTO;
import io.sabrinaatha.siruang_ng_be.service.RuanganService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

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

    // Add new ruangan
    @PostMapping("/create")
    public ResponseEntity<?> addRuangan(@Valid @RequestBody RuanganRequestDTO ruanganRequestDTO, BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<RuanganResponseDTO>();

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Validasi gagal. Mohon periksa input.");
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            // Proses add ruangan
            RuanganResponseDTO ruanganResponseDTO = ruanganService.addRuangan(ruanganRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(ruanganResponseDTO);
            baseResponseDTO.setMessage("Ruangan baru berhasil disimpan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            // Menangani kasus jika ada kesalahan runtime
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal menyimpan ruangan baru karena data tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.CONFLICT.value());
            baseResponseDTO.setMessage("Gagal menyimpan ruangan baru karena konflik dalam data: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            // Menangani kasus jika tidak ada data
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Gagal menyimpan ruangan baru karena: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal membuat ruangan baru karena kesalahan sistem: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update ruangan
    @PutMapping("/update/{idRuangan}")
    public ResponseEntity<?> updateRuangan(@Valid @PathVariable UUID idRuangan, @RequestBody RuanganRequestDTO ruanganRequestDTO,
                                            BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<RuanganResponseDTO>();

        // Validasi data input
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            // Proses update ruangan
            RuanganResponseDTO ruanganResponseDTO = ruanganService.updateRuangan(idRuangan, ruanganRequestDTO);

            if (ruanganResponseDTO == null) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage(String.format("Data ruangan tidak ditemukan"));
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(ruanganResponseDTO);
            baseResponseDTO.setMessage(String.format("Ruangan dengan ID %s berhasil diubah",
                    ruanganResponseDTO.getIdRuangan()));
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (BadRequestException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal mengubah ruangan karena data tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Gagal mengubah ruangan karena data tidak ditemukan: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.CONFLICT.value());
            baseResponseDTO.setMessage("Gagal mengubah ruangan karena konflik dalam status: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            // Menangani kasus jika tidak ada data
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(" \"Gagal mengubah ruangan karena data tidak ditemukan: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal mengubah ruangan karena kesalahan sistem: " + e.getMessage());
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
