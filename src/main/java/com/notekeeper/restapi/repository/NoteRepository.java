package com.notekeeper.restapi.repository;

import com.notekeeper.restapi.model.Note;
import com.notekeeper.restapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findAllByUser(User user);

    void deleteAllByUser(User user);

    Optional<Note> findById(String id);

}
