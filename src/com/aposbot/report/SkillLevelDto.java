package com.aposbot.report;

public class SkillLevelDto {
    public int current;
    public int base;

    public static SkillLevelDto Create(int[] item) {
        SkillLevelDto dto = new SkillLevelDto();
        dto.current = item[1];
        dto.base = item[2];
        return dto;
    }
}
