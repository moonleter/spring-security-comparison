package cz.osu.kunz.springsecuritycomparison.service.impl;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final AccessLogService accessLogService;
    private final ProtocolResolver protocolResolver;
    private final SecurityContextHelper securityContextHelper;
    private final NoteMapper noteMapper;

    public List<NoteReadDto> getCurrentUserNotes() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        accessLogService.logAccess(currentUsername, "GET_NOTES", protocolResolver.getCurrentProtocol(), true);

        return noteRepository.findAllByOwnerUsername(currentUsername)
                .stream()
                .map(n -> new NoteReadDto(n.getId(), n.getContent(), n.getOwnerUsername(), n.getCreatedAt()))
                .toList();
    }

    @Transactional
    public NoteReadDto createNote(NoteCreateDto noteCreateDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Note note = new Note();
        note.setContent(noteCreateDto.getContent());
        note.setOwnerUsername(currentUsername);
        Note savedNote = noteRepository.save(note);

        accessLogService.logAccess(currentUsername, "CREATE_NOTE", protocolResolver.getCurrentProtocol(), true);
        return noteMapper.mapNoteToNoteReadDto(savedNote);
    }

    @Transactional
    public NoteReadDto editNote(NoteEditDto noteEditDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Note note = noteRepository.findById(noteEditDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Note not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!note.getOwnerUsername().equals(currentUsername) && !isAdmin) {

            accessLogService.logAccess(currentUsername, "EDIT_NOTE_UNAUTHORIZED", protocolResolver.getCurrentProtocol(), false);

            throw new AccessDeniedException("You do not have permission to edit this note.");
        }

        note.setContent(noteEditDto.getContent());
        Note savedNote = noteRepository.save(note);

        accessLogService.logAccess(currentUsername, "EDIT_NOTE", protocolResolver.getCurrentProtocol(), true);

        return noteMapper.mapNoteToNoteReadDto(savedNote);
    }

    @Transactional
    public void deleteNote(NoteDeleteDto noteDeleteDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Note note = noteRepository.findById(noteDeleteDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Note not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!note.getOwnerUsername().equals(currentUsername) && !isAdmin) {

            accessLogService.logAccess(currentUsername, "DELETE_NOTE_UNAUTHORIZED", protocolResolver.getCurrentProtocol(), false);

            throw new AccessDeniedException("You do not have permission to delete this note.");
        }

        noteRepository.delete(note);

        accessLogService.logAccess(currentUsername, "DELETE_NOTE", protocolResolver.getCurrentProtocol(), true);
    }
}
