package com.notekeeper.restapi.controller;

import com.notekeeper.restapi.model.Note;
import com.notekeeper.restapi.payload.request.NoteInfo;
import com.notekeeper.restapi.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("notes/v1/notes")
public class NotesController {

    private NotesService notesService;

    @Autowired
    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping("/get-all-notes")
    public ResponseEntity<List<Note>> getAllNotes(){
        List<Note> notes = notesService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    @DeleteMapping("/delete-all-notes")
    public ResponseEntity<String> deleteAllNotes(){
        notesService.deleteAllNotes();
        return ResponseEntity.ok("Successfully deleted");
    }

    @PostMapping("/create-note")
    public ResponseEntity<Note> createNote(@RequestBody NoteInfo noteInfo){
        Note note = notesService.createNewNote(noteInfo);
        return ResponseEntity.ok(note);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable String id) throws AccessDeniedException{
        Note note = notesService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/edit-note/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable String id ,@RequestBody NoteInfo noteInfo)  throws AccessDeniedException {
        Note note = notesService.updateNote(id,noteInfo);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/delete-note/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable String id)  throws AccessDeniedException{
        notesService.deleteNote(id);
        return ResponseEntity.ok("Successfully deleted");
    }


}
