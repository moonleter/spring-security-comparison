package cz.osu.kunz.springsecuritycomparison.controller;

import cz.osu.kunz.springsecuritycomparison.model.dto.NoteCreateDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteEditDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteReadDto;
import cz.osu.kunz.springsecuritycomparison.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Controller for managing user notes")
public class NoteController {
    private final NoteService noteService;

    @Operation(summary = "Get all notes for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's notes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<NoteReadDto> getCurrentUserNotes() {
        return noteService.getCurrentUserNotes();
    }

    @Operation(summary = "Create a new note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public NoteReadDto createNote(@Valid @RequestBody NoteCreateDto noteCreateDto) {
        return noteService.createNote(noteCreateDto);
    }

    @Operation(summary = "Edit an existing note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{noteId}")
    public NoteReadDto editNote(
            @PathVariable UUID noteId,
            @Valid @RequestBody NoteEditDto noteEditDto) {
        return noteService.editNote(noteId, noteEditDto);
    }

    @Operation(summary = "Delete a note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{noteId}")
    public void deleteNote(@PathVariable UUID noteId) {
        noteService.deleteNote(noteId);
    }

    @Operation(summary = "Get all notes (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all notes"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notes/all")
    public List<NoteReadDto> getAllUsersNotesAsAdmin() {
        return noteService.getAllUsersNotesAsAdmin();
    }
}