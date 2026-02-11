package cz.osu.kunz.springsecuritycomparison.repository;

import cz.osu.kunz.springsecuritycomparison.model.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findAllByOwnerUsername(String ownerUsername);
}
