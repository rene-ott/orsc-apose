package com.reporting;

public class ItemDto {
    public int id;
    public int count;
    public boolean isStackable;

    public static ItemDto Create(int[] item) {
        ItemDto dto = new ItemDto();
        dto.id = item[0];
        dto.count = item[1];
        dto.isStackable = item[2] == 1;
        return dto;
    }
}
