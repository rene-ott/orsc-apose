package com.aposbot.report;

public class SkillDto {
    public int currentLevel;
    public int baseLevel;

    public static SkillDto Create(int[] item) {
        SkillDto dto = new SkillDto();
        dto.currentLevel = item[1];
        dto.baseLevel = item[2];
        return dto;
    }
}
