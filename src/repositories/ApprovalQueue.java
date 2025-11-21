package repositories;

import java.util.*;
import entities.approval.ApprovalItem;
/**
 * Interface for ApprovalQueue
 */
public interface ApprovalQueue {
    void submit(ApprovalItem item);
    List<ApprovalItem> list();
    Optional<ApprovalItem> find(String id);
    void remove(String id);
}
