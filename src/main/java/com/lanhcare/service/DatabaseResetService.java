package com.lanhcare.service;

/**
 * Service for resetting and seeding database
 */
public interface DatabaseResetService {
    /**
     * Reset database: Delete all data except Admin accounts, then seed new data
     * @return Reset result with statistics
     */
    ResetResult resetAndSeedDatabase();
    
    /**
     * Result of database reset operation
     */
    record ResetResult(
        int deletedAccounts,
        int deletedRecords,
        int seededAccounts,
        int seededRecords,
        String message
    ) {}
}
