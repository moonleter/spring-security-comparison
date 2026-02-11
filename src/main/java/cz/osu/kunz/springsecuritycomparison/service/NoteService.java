package cz.osu.kunz.springsecuritycomparison.service;

import java.util.List;

public interface NoteService {
    List<NoteReadDto> getCurrentUserNotes();
