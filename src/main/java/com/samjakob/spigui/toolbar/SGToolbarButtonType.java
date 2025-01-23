package com.samjakob.spigui.toolbar;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SGToolbarButtonType {
    PREV_BUTTON(3),
    CURRENT_BUTTON(4),
    NEXT_BUTTON(5),
    UNASSIGNED(-1); // Default for unassigned slots.

    private final int defaultSlot;

    // Reverse lookup map for slot-to-button type mapping.
    private static final Map<Integer, SGToolbarButtonType> SLOT_TO_TYPE_MAP = Stream.of(values())
            .filter(type -> type.defaultSlot >= 0) // Exclude unassigned by default
            .collect(Collectors.toMap(SGToolbarButtonType::getDefaultSlot, Function.identity()));

    SGToolbarButtonType(int defaultSlot) {
        this.defaultSlot = defaultSlot;
    }

    public int getDefaultSlot() {
        return defaultSlot;
    }

    public static SGToolbarButtonType getDefaultForSlot(int slot) {
        return SLOT_TO_TYPE_MAP.getOrDefault(slot, UNASSIGNED);
    }
}
