package io.sabrinaatha.siruang_ng_be.controller;

import io.sabrinaatha.siruang_ng_be.exception.BadRequestException;
import io.sabrinaatha.siruang_ng_be.exception.NotFoundException;
import io.sabrinaatha.siruang_ng_be.payload.request.PeminjamanRequestDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.BaseResponseDTO;
import io.sabrinaatha.siruang_ng_be.payload.response.PeminjamanResponseDTO;
import io.sabrinaatha.siruang_ng_be.service.PeminjamanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/peminjaman")
public class PeminjamanController {

    @Autowired
    private PeminjamanService peminjamanService;

    // GET all
    @GetMapping("/all")
    public ResponseEntity<?> viewAllPeminjaman() {
        var baseResponseDTO = new BaseResponseDTO<List<PeminjamanResponseDTO>>();

        try {
            // Mengambil daftar peminjaman dari service
            List<PeminjamanResponseDTO> listPeminjaman = peminjamanService.getAllPeminjaman();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(listPeminjaman);
            baseResponseDTO.setMessage("Seluruh peminjaman berhasil ditemukan");
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

    // Get detail peminjaman ruangan
    @GetMapping("/detail/{idPeminjaman}")
    public ResponseEntity<?> viewDetailPeminjaman(@PathVariable UUID idPeminjaman) {
        var baseResponseDTO = new BaseResponseDTO<PeminjamanResponseDTO>();

        try {
            // Mengambil detail peminjaman dari service
            PeminjamanResponseDTO peminjaman = peminjamanService.getPeminjamanById(idPeminjaman);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(peminjaman);
            baseResponseDTO.setMessage("Peminjaman dengan id " + idPeminjaman + " berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Permintaan tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
                // Menangani kasus jika tidak ada data
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("Gagal mengambil data peminjaman dengan id " + idPeminjaman + " karena data tidak ada.");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
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
    public ResponseEntity<?> addPeminjaman(@Valid @RequestBody PeminjamanRequestDTO peminjamanRequestDTO, BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<PeminjamanResponseDTO>();

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
            // Proses add peminjaman
            PeminjamanResponseDTO peminjamanResponseDTO = peminjamanService.addPeminjaman(peminjamanRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(peminjamanResponseDTO);
            baseResponseDTO.setMessage("Peminjaman baru berhasil disimpan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            // Menangani kasus jika ada kesalahan runtime
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal menyimpan peminjaman baru karena data tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.CONFLICT.value());
            baseResponseDTO.setMessage("Gagal menyimpan peminjaman baru karena konflik dalam data: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            // Menangani kasus jika tidak ada data
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Gagal menyimpan peminjaman baru karena: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal membuat peminjaman baru karena kesalahan sistem: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update peminjaman
    @PutMapping("/update/{idPeminjaman}")
    public ResponseEntity<?> updatePeminjaman(@Valid @PathVariable UUID idPeminjaman, @RequestBody PeminjamanRequestDTO peminjamanRequestDTO,
                                           BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<PeminjamanResponseDTO>();

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
            // Proses update peminjaman
            PeminjamanResponseDTO peminjamanResponseDTO = peminjamanService.updatePeminjaman(idPeminjaman, peminjamanRequestDTO);

            if (peminjamanResponseDTO == null) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage(String.format("Data peminjaman tidak ditemukan"));
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(peminjamanResponseDTO);
            baseResponseDTO.setMessage(String.format("Peminjaman dengan ID %s berhasil diubah",
                    peminjamanRequestDTO.getIdRuangan()));
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (BadRequestException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal mengubah peminjaman karena data tidak valid: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Gagal mengubah peminjaman karena data tidak ditemukan: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.CONFLICT.value());
            baseResponseDTO.setMessage("Gagal mengubah peminjaman karena konflik dalam status: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            // Menangani kasus jika tidak ada data
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(" \"Gagal mengubah peminjaman karena data tidak ditemukan: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal mengubah peminjaman karena kesalahan sistem: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Ubah status peminjaman data
    @PutMapping("ubah/status/{id}")
    public ResponseEntity<?> ubahStatusPeminjaman(@Valid @PathVariable("id") UUID id, @RequestParam("status") String status) {
        var baseResponseDTO = new BaseResponseDTO<PeminjamanResponseDTO>();
        try {
            // Ubah status peminjaman
            PeminjamanResponseDTO peminjaman = peminjamanService.ubahStatusPeminjaman(id, status);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(peminjaman);
            baseResponseDTO.setMessage("Status peminjaman berhasil diubah");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // Menangani kasus jika tidak ada entity
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Peminjaman ruangan dengan id " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (ConstraintViolationException e) {
            // Menangani kasus jika ada argumen tidak valid
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Gagal mengubah status peminjaman karena " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Menangani semua jenis error lainnya
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal mengubah status peminjaman karena " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get detail peminjaman ruangan
    @GetMapping("/list/persetujuan")
    public ResponseEntity<?> viewListPersetujuanPeminjaman() {
        var baseResponseDTO = new BaseResponseDTO<List<PeminjamanResponseDTO>>();

        try {
            // Mengambil daftar peminjaman dari service
            List<PeminjamanResponseDTO> listPeminjaman = peminjamanService.getListPersetujuanPeminjaman();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(listPeminjaman);
            baseResponseDTO.setMessage("Seluruh peminjaman yang perlu persetujuan / sudah disetujui berhasil ditemukan");
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

    // Get detail peminjaman ruangan
    @GetMapping("/list/verifikasi")
    public ResponseEntity<?> viewListVerifikasiPeminjaman() {
        var baseResponseDTO = new BaseResponseDTO<List<PeminjamanResponseDTO>>();

        try {
            // Mengambil daftar peminjaman dari service
            List<PeminjamanResponseDTO> listPeminjaman = peminjamanService.getListVerifikasiPeminjaman();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(listPeminjaman);
            baseResponseDTO.setMessage("Seluruh peminjaman yang perlu verifikasi / sudah diverifikasi berhasil ditemukan");
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
