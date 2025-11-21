package settings;

import entities.posting.Level;
import entities.posting.Major;
import entities.posting.PostingStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to store posting settings
 */
public final class SessionSettings {
    /**
     * Accessor for posting filter
     */
    public static PostingFilter getFilter(String userId) {
        return FILTERS.computeIfAbsent(userId, id -> new PostingFilter());
    }
    /**
     * Mutator for posting filter
     */
    public static void setFilter(String userId, PostingFilter f) {
        if (f == null) f = new PostingFilter();
        FILTERS.put(userId, f);
    }

    public static final class PostingFilter implements Serializable {
        @Serial private static final long serialVersionUID = 1L;

        public String companyNameContains;

        public Level level;

        public LocalDate closeBy;

        public Major major;

        public PostingStatus status;

        public Optional<String> companyOpt() { return Optional.ofNullable(companyNameContains).filter(s -> !s.isBlank()); }
        public Optional<Level> levelOpt()    { return Optional.ofNullable(level); }
        public Optional<LocalDate> closeByOpt() { return Optional.ofNullable(closeBy); }
        public Optional<Major> majorOpt()    { return Optional.ofNullable(major); }
        public Optional<PostingStatus> statusOpt() { return Optional.ofNullable(status); }
    }


    public enum SortBy { TITLE, LEVEL, OPEN_DATE, CLOSE_DATE }

    private static final Map<String, PostingFilter> FILTERS = new ConcurrentHashMap<>();

    static Map<String, PostingFilter> copyFilters() { return new HashMap<>(FILTERS); }
    static void replaceAll(Map<String, PostingFilter> filters) {
        FILTERS.clear(); if (filters != null) FILTERS.putAll(filters);
    }

    private SessionSettings() {}
}