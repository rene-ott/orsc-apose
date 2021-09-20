package com.aposbot.report;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDto {
    public String timestamp;
    public String username;

    public List<ItemDto> inventoryItems;
    public List<SkillDto> skillLevels;

    public String bankViewTimestamp;
    public List<ItemDto> bankItems;

    public static ReportDto create(Instant timestamp, String username, int[][] inventoryItems,
                                   int[][] skillLevels, Instant bankViewTimestamp, int[][] bankItems) {
        ReportDto report = new ReportDto();
        report.timestamp = timestamp.toString();
        report.username = username;

        report.inventoryItems = Arrays.stream(inventoryItems)
                .map(ItemDto::Create)
                .collect(Collectors.toList());

        report.skillLevels = Arrays.stream(skillLevels)
                .map(SkillDto::Create)
                .collect(Collectors.toList());

        report.bankViewTimestamp = bankViewTimestamp != null
                ? bankViewTimestamp.toString()
                : null;

        report.bankItems = bankItems != null
                ? Arrays.stream(bankItems)
                    .map(ItemDto::Create)
                    .collect(Collectors.toList())
                : null;

        return report;
    }
}
