package cz.osu.kunz.springsecuritycomparison.service;

import cz.osu.kunz.springsecuritycomparison.model.dto.NoteCreateDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteEditDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteReadDto;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    List<NoteReadDto> getCurrentUserNotes();

    NoteReadDto createNote(NoteCreateDto noteCreateDto);

    NoteReadDto editNote(UUID noteId, NoteEditDto noteEditDto);

    void deleteNote(UUID noteId);

    List<NoteReadDto> getAllUsersNotesAsAdmin();
}