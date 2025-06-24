package io.sabrinaatha.siruang_ng_be.payload.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuanganRequestDTO {
    private String namaRuangan;
    private String statusRuangan;
    private String tipeRuangan;
}