package com.aposbot.report;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReportDto {

    public String username;
    public String base64EncodedScreenshot;
    public String bankViewTimestamp;

    public SkillDto skill;

    public List<ItemDto> bankItems;
    public List<ItemDto> inventoryItems;

    public static ReportDto create(String username, String base64EncodedScreenshot, int[][] inventoryItems,
                                   int[][] skillLevels, Instant bankViewTimestamp, int[][] bankItems) {
        ReportDto report = new ReportDto();
        report.base64EncodedScreenshot = base64EncodedScreenshot;
        report.username = username;

        report.skill = SkillDto.Create(skillLevels);

        report.inventoryItems = IntStream.range(0, inventoryItems.length)
                .mapToObj(i -> ItemDto.Create(inventoryItems[i], i))
                .collect(Collectors.toList());

        report.bankViewTimestamp = bankViewTimestamp != null
                ? bankViewTimestamp.toString()
                : null;

        report.bankItems = bankItems != null
                ? IntStream.range(0, bankItems.length)
                    .mapToObj(i -> ItemDto.Create(bankItems[i], i))
                    .collect(Collectors.toList())
                : null;

        return report;
    }
}
