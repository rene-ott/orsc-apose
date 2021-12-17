package com.aposbot.report;

public class SkillLevelDto {
    public int currentLevel;
    public int level;
    public int xp;

    public static SkillLevelDto Create(int[] item) {
        SkillLevelDto dto = new SkillLevelDto();
        dto.currentLevel = item[1];
        dto.level = item[2];
        dto.xp = item[3];
        return dto;
    }
}
