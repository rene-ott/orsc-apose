package com.aposbot.report;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDto {

    public String username;

    public SkillDto attack;
    public SkillDto defense;
    public SkillDto strength;
    public SkillDto hits;
    public SkillDto ranged;
    public SkillDto prayer;
    public SkillDto magic;
    public SkillDto cooking;
    public SkillDto woodcut;
    public SkillDto fletching;
    public SkillDto fishing;
    public SkillDto firemaking;
    public SkillDto crafting;
    public SkillDto smithing;
    public SkillDto mining;
    public SkillDto herblaw;
    public SkillDto agility;
    public SkillDto thieving;

    public String bankViewTimestamp;

    public List<ItemDto> bankItems;
    public List<ItemDto> inventoryItems;

    public static ReportDto create(String username, int[][] inventoryItems,
                                   int[][] skillLevels, Instant bankViewTimestamp, int[][] bankItems) {
        ReportDto report = new ReportDto();
        report.username = username;

        report.inventoryItems = Arrays.stream(inventoryItems)
                .map(ItemDto::Create)
                .collect(Collectors.toList());

        report.attack = SkillDto.Create(skillLevels[0]);
        report.defense = SkillDto.Create(skillLevels[1]);
        report.strength = SkillDto.Create(skillLevels[2]);
        report.hits = SkillDto.Create(skillLevels[3]);
        report.ranged = SkillDto.Create(skillLevels[4]);
        report.prayer = SkillDto.Create(skillLevels[5]);
        report.magic = SkillDto.Create(skillLevels[6]);
        report.cooking = SkillDto.Create(skillLevels[7]);
        report.woodcut = SkillDto.Create(skillLevels[8]);
        report.fletching = SkillDto.Create(skillLevels[9]);
        report.fishing = SkillDto.Create(skillLevels[10]);
        report.firemaking = SkillDto.Create(skillLevels[11]);
        report.crafting = SkillDto.Create(skillLevels[12]);
        report.smithing = SkillDto.Create(skillLevels[13]);
        report.mining = SkillDto.Create(skillLevels[14]);
        report.herblaw = SkillDto.Create(skillLevels[15]);
        report.agility = SkillDto.Create(skillLevels[16]);
        report.thieving = SkillDto.Create(skillLevels[17]);

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
