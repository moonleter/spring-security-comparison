package cz.osu.kunz.springsecuritycomparison.service.impl;

import cz.osu.kunz.springsecuritycomparison.mapper.CycleAvoidingMappingContext;
import cz.osu.kunz.springsecuritycomparison.mapper.NoteMapper;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteCreateDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteEditDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteReadDto;
import cz.osu.kunz.springsecuritycomparison.model.entity.Note;
import cz.osu.kunz.springsecuritycomparison.repository.NoteRepository;
import cz.osu.kunz.springsecuritycomparison.service.AccessLogService;
import cz.osu.kunz.springsecuritycomparison.service.NoteService;
import cz.osu.kunz.springsecuritycomparison.service.helper.ProtocolResolver;
import cz.osu.kunz.springsecuritycomparison.service.helper.SecurityContextHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final AccessLogService accessLogService;
    private final ProtocolResolver protocolResolver;
    private final SecurityContextHelper securityContextHelper;
    private final NoteMapper noteMapper;

    public List<NoteReadDto> getCurrentUserNotes() {
        String currentUsername = securityContextHelper.getCurrentUsername();
        accessLogService.logAccess(currentUsername, "GET_NOTES", protocolResolver.getCurrentProtocol(), true);


        return noteMapper.notesToNoteReadDtos(
                noteRepository.findAllByOwnerUsername(currentUsername),
                new CycleAvoidingMappingContext()
        );
    }

    @Transactional
    public NoteReadDto createNote(NoteCreateDto noteCreateDto) {
        String currentUsername = securityContextHelper.getCurrentUsername();

        Note note = new Note();
        note.setContent(noteCreateDto.getContent());
        note.setOwnerUsername(currentUsername);
        Note savedNote = noteRepository.save(note);

        accessLogService.logAccess(currentUsername, "CREATE_NOTE", protocolResolver.getCurrentProtocol(), true);
        return noteMapper.noteToNoteReadDto(savedNote, new CycleAvoidingMappingContext());
    }

    @Transactional
    public NoteReadDto editNote(UUID noteId, NoteEditDto noteEditDto) {
        String currentUsername = securityContextHelper.getCurrentUsername();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found"));

        boolean isAdmin = securityContextHelper.isCurrentUserAdmin();

        if (!note.getOwnerUsername().equals(currentUsername) && !isAdmin) {

            accessLogService.logAccess(currentUsername, "EDIT_NOTE_UNAUTHORIZED", protocolResolver.getCurrentProtocol(), false);

            throw new AccessDeniedException("You do not have permission to edit this note.");
        }

        note.setContent(noteEditDto.getContent());
        Note savedNote = noteRepository.save(note);

        accessLogService.logAccess(currentUsername, "EDIT_NOTE", protocolResolver.getCurrentProtocol(), true);

        return noteMapper.noteToNoteReadDto(savedNote, new CycleAvoidingMappingContext());
    }

    @Transactional
    public void deleteNote(UUID noteId) {
        String currentUsername = securityContextHelper.getCurrentUsername();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found"));

        boolean isAdmin = securityContextHelper.isCurrentUserAdmin();

        if (!note.getOwnerUsername().equals(currentUsername) && !isAdmin) {

            accessLogService.logAccess(currentUsername, "DELETE_NOTE_UNAUTHORIZED", protocolResolver.getCurrentProtocol(), false);

            throw new AccessDeniedException("You do not have permission to delete this note.");
        }

        noteRepository.delete(note);

        accessLogService.logAccess(currentUsername, "DELETE_NOTE", protocolResolver.getCurrentProtocol(), true);
    }

    public List<NoteReadDto> getAllUsersNotesAsAdmin() {
        if (!securityContextHelper.isCurrentUserAdmin()) {
            String currentUsername = securityContextHelper.getCurrentUsername();
            accessLogService.logAccess(currentUsername, "GET_ALL_NOTES_UNAUTHORIZED", protocolResolver.getCurrentProtocol(), false);
            throw new AccessDeniedException("You do not have permission to view all notes.");
        }

        accessLogService.logAccess(securityContextHelper.getCurrentUsername(), "GET_ALL_NOTES", protocolResolver.getCurrentProtocol(), true);

        return noteMapper.notesToNoteReadDtos(
                noteRepository.findAll(),
                new CycleAvoidingMappingContext()
        );
    }
}


