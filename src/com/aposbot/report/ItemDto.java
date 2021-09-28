package com.aposbot.report;

public class ItemDto {
    public int id;
    public int count;
    public boolean isStackable;
    public int position;

    public static ItemDto Create(int[] item, int position) {
        ItemDto dto = new ItemDto();
        dto.id = item[0];
        dto.count = item[1];
        dto.isStackable = item[2] == 1;
        dto.position = position;
        return dto;
    }
}
