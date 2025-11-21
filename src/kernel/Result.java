package kernel;

import java.util.Optional;

/**
 * Result class to pass back messages
 * @param <T>
 */
public final class Result<T> {
    private final boolean ok;
    private final T value;
    private final String error;
    /**
     * Result constructor
     */
    private Result(boolean ok, T value, String error) {
        this.ok = ok;
        this.value = value;
        this.error = error;
    }
    /**
     * Result pass with value
     */
    public static <T> Result<T> ok(T value) { return new Result<>(true, value, null); }
    /**
     * Result pass with no value
     */
    public static <T> Result<T> ok() { return new Result<>(true, null, null); }
    /**
     * Result fail with String error
     */
    public static <T> Result<T> fail(String error) { return new Result<>(false, null, error); }
    /**
     * check if result is ok
     */
    public boolean isOk() { return ok; }
    /**
     * Get object from optional
     */
    public Optional<T> get() { return Optional.ofNullable(value); }
    /**
     * Throw String error
     */
    public String error() { return error; }
}