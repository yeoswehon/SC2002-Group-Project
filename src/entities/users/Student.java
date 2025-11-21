package entities.users;

import entities.posting.Major;

import java.util.Objects;
/**
 * Class for Student
 */
public final class Student extends User {
    private final Major major;
    private final int year; 
    private String username;
    /**
     * Constructor for new student
     */
    public Student(String username, String passwordHash, String displayName,
                   Major major, int year) {
        super(Role.STUDENT, passwordHash, displayName); 
        setUsername(Objects.requireNonNull(username, "username must not be null"));
        this.major = Objects.requireNonNull(major, "major must not be null");
        this.year = year;
    }
    /**
     * Constructor for loading student from serialized file
     */
    public Student(String id, String username, String passwordHash, String displayName,
                   Major major, int year) {
        super(id, Role.STUDENT, passwordHash, displayName); // uses existing id
        setUsername(Objects.requireNonNull(username, "username must not be null"));
        this.major = Objects.requireNonNull(major, "major must not be null");
        this.year = year;
    }
    /**
     * Accessor for Student's major
     */
    public Major getMajor() { return major; }
    /**
     * Accessor for Student's year of study
     */
    public int getYear() { return year; }
}
