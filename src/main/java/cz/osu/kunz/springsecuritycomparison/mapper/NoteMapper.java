package cz.osu.kunz.springsecuritycomparison.mapper;

import cz.osu.kunz.springsecuritycomparison.model.dto.NoteCreateDto;
import cz.osu.kunz.springsecuritycomparison.model.dto.NoteReadDto;
import cz.osu.kunz.springsecuritycomparison.model.entity.Note;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public interface NoteMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "ownerUsername", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    Note noteCreateDtoToNote(NoteCreateDto noteCreateDto, @Context CycleAvoidingMappingContext context);

    NoteReadDto noteToNoteReadDto(Note note, @Context CycleAvoidingMappingContext context);

    List<NoteReadDto> notesToNoteReadDtos(List<Note> notes, @Context CycleAvoidingMappingContext context);
}
