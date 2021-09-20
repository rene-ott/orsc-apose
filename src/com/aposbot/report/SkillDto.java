package com.aposbot.report;

public class SkillDto {
    public int id;
    public int currentLevel;
    public int baseLevel;

    public static SkillDto Create(int[] item) {
        SkillDto dto = new SkillDto();
        dto.id = item[0];
        dto.currentLevel = item[1];
        dto.baseLevel = item[2];
        return dto;
    }
}
