package com.aposbot.report;

public class SkillLevelDto {
    public int current;
    public int base;
    public int totalXp;

    public static SkillLevelDto Create(int[] item) {
        SkillLevelDto dto = new SkillLevelDto();
        dto.current = item[1];
        dto.base = item[2];
        dto.totalXp = item[3];
        return dto;
    }
}
