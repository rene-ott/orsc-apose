package com.aposbot.report;

public class SkillDto {

    public SkillLevelDto attack;
    public SkillLevelDto defense;
    public SkillLevelDto strength;
    public SkillLevelDto hits;
    public SkillLevelDto ranged;
    public SkillLevelDto prayer;
    public SkillLevelDto magic;
    public SkillLevelDto cooking;
    public SkillLevelDto woodcut;
    public SkillLevelDto fletching;
    public SkillLevelDto fishing;
    public SkillLevelDto firemaking;
    public SkillLevelDto crafting;
    public SkillLevelDto smithing;
    public SkillLevelDto mining;
    public SkillLevelDto herblaw;
    public SkillLevelDto agility;
    public SkillLevelDto thieving;

    public static SkillDto Create(int[][] skillLevels) {
        SkillDto dto = new SkillDto();

        dto.attack = SkillLevelDto.Create(skillLevels[0]);
        dto.defense = SkillLevelDto.Create(skillLevels[1]);
        dto.strength = SkillLevelDto.Create(skillLevels[2]);
        dto.hits = SkillLevelDto.Create(skillLevels[3]);
        dto.ranged = SkillLevelDto.Create(skillLevels[4]);
        dto.prayer = SkillLevelDto.Create(skillLevels[5]);
        dto.magic = SkillLevelDto.Create(skillLevels[6]);
        dto.cooking = SkillLevelDto.Create(skillLevels[7]);
        dto.woodcut = SkillLevelDto.Create(skillLevels[8]);
        dto.fletching = SkillLevelDto.Create(skillLevels[9]);
        dto.fishing = SkillLevelDto.Create(skillLevels[10]);
        dto.firemaking = SkillLevelDto.Create(skillLevels[11]);
        dto.crafting = SkillLevelDto.Create(skillLevels[12]);
        dto.smithing = SkillLevelDto.Create(skillLevels[13]);
        dto.mining = SkillLevelDto.Create(skillLevels[14]);
        dto.herblaw = SkillLevelDto.Create(skillLevels[15]);
        dto.agility = SkillLevelDto.Create(skillLevels[16]);
        dto.thieving = SkillLevelDto.Create(skillLevels[17]);

        return dto;
    }
}
