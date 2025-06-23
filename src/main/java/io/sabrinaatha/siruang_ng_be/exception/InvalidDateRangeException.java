package io.sabrinaatha.siruang_ng_be.exception;

public class InvalidDateRangeException extends RuntimeException {

    // Konstruktor tanpa parameter
    public InvalidDateRangeException() {
        super("Rentang tanggal tidak valid.");
    }

    // Konstruktor dengan pesan khusus
    public InvalidDateRangeException(String message) {
        super(message);
    }

    // Konstruktor dengan pesan dan sebab pengecualian lain
    public InvalidDateRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    // Konstruktor dengan sebab pengecualian lain
    public InvalidDateRangeException(Throwable cause) {
        super(cause);
    }
}

