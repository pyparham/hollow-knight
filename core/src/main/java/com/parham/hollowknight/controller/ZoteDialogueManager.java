package com.parham.hollowknight.controller;

import com.parham.hollowknight.model.entities.Zote;
import com.parham.hollowknight.model.enums.ZoteDialogueState;

public class ZoteDialogueManager {

    private static final String[] FIRST_MEETING_LINES = {
        "Salam Azizam",
        "Ba man dar nayoft yarege",
        "Sham ha to khordam chaghal"
    };

    private static final String[] PRECEPTS = {
        "Precept the First: Hichvaght bedoone sham naya",
        "Precept the Second: Be ghanoone aval tavajoh nakon",
        "Precept the Third: Shaba zood nakhab",
        "Precept the Fourth: Mikhay bekhabi? to bidary kaboos bebin"
    };

    private int firstMeetingIndex = 0;
    private String currentLine = "";

    public void beginInteraction(Zote zote) {
        zote.startDialogue();
        firstMeetingIndex = 0;
        currentLine = getNextLine(zote);
    }

    public boolean advance(Zote zote) {
        if (zote.dialogueState == ZoteDialogueState.FIRST_MEETING) {
            firstMeetingIndex++;
            if (firstMeetingIndex >= FIRST_MEETING_LINES.length) {
                zote.endDialogue();
                return false;
            }
            currentLine = FIRST_MEETING_LINES[firstMeetingIndex];
            return true;
        } else {
            zote.endDialogue();
            return false;
        }
    }

    private String getNextLine(Zote zote) {
        if (zote.dialogueState == ZoteDialogueState.FIRST_MEETING) {
            return FIRST_MEETING_LINES[0];
        } else {
            int idx = (int) (Math.random() * PRECEPTS.length);
            return PRECEPTS[idx];
        }
    }

    public String getCurrentLine() {
        return currentLine;
    }
}
