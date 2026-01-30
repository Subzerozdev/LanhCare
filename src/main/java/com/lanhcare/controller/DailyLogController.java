package com.lanhcare.controller;

import com.lanhcare.dto.dailylog.DailyLogRequest;
import com.lanhcare.dto.dailylog.DailyLogResponse;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.AccountService;
import com.lanhcare.service.DailyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-logs")
@RequiredArgsConstructor
@Tag(name = "User - Daily Log", description = "APIs for tracking daily nutrition, steps, and calories")
public class DailyLogController {
    private final DailyLogService dailyLogService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create daily log", description = "Initialize a new daily log entry for an account")
    public ResponseEntity<DailyLogResponse> createLog(
            @RequestBody DailyLogRequest request,
            @RequestHeader("Authorization") String token
    ) {
        int accountId = Integer.parseInt( jwtTokenProvider.getIdentifierFromToken(token));
        request.setAccountId(accountId);

        return new ResponseEntity<>(dailyLogService.mapToResponse(
                dailyLogService.createLog(request)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get log by ID", description = "Retrieve specific daily log details using its primary ID")
    public ResponseEntity<DailyLogResponse> getLogById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(dailyLogService.mapToResponse(
                dailyLogService.getLogById(id)));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get log by Account and Date", description = "Retrieve a daily log for a specific user on a specific date")
    public ResponseEntity<DailyLogResponse> getLogByDate(
            @RequestHeader("Authorization") String token,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        int accountId = Integer.parseInt( jwtTokenProvider.getIdentifierFromToken(token));
        return ResponseEntity.ok(dailyLogService.mapToResponse(
                dailyLogService.getLogByAccountAndDate(accountId, date)));
    }

    @GetMapping()
    @Operation(summary = "Get all logs for an account", description = "Retrieve a full history of daily logs for a specific account")
    public ResponseEntity<List<DailyLogResponse>> getAllLogsByAccount(
            @RequestHeader("Authorization") String token
    ) {
        int accountId = Integer.parseInt( jwtTokenProvider.getIdentifierFromToken(token));
        return ResponseEntity.ok(dailyLogService.getAllLogsByAccountId(accountId));
    }

    @PatchMapping("/{id}/steps")
    @Operation(summary = "Update daily steps", description = "Update the step count for a specific log and recalculate calories burned")
    public ResponseEntity<DailyLogResponse> updateSteps(
            @PathVariable Integer id,
            @RequestParam Integer steps
    ) {
        return ResponseEntity.ok(dailyLogService.mapToResponse(
                dailyLogService.updateSteps(id, steps)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete daily log", description = "Remove a daily log entry by its ID")
    public ResponseEntity<String> deleteLog(@PathVariable Integer id) {
        dailyLogService.deleteLog(id);
        return ResponseEntity.ok("Delete successfully");
    }
}