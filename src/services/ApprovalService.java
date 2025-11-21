package services;

import entities.approval.ApprovalItem;
import kernel.Result;

import java.util.List;

import entities.approval.Type;
/**
 * Interface for Approval Service
 */
public interface ApprovalService {

    List<ApprovalItem> listItems();

    Result<Void> approve(String itemId);

    Result<Void> reject(String itemId, String reason);
}