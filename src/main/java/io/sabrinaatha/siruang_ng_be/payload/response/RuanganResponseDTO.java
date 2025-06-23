package io.sabrinaatha.siruang_ng_be.payload.response;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuanganResponseDTO {
    private UUID idRuangan;
    private String namaRuangan;
    private String statusRuangan;
    private String tipeRuangan;
    private boolean isDeleted;
}
