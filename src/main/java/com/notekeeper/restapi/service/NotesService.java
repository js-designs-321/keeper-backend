package com.notekeeper.restapi.service;

import com.notekeeper.restapi.exception.ResourceNotFoundException;
import com.notekeeper.restapi.model.Note;
import com.notekeeper.restapi.payload.request.NoteInfo;
import com.notekeeper.restapi.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class NotesService {

    private UserAuthenticationService userAuthenticationService;

    private NoteRepository noteRepository;

    @Autowired
    public NotesService(UserAuthenticationService userAuthenticationService,NoteRepository noteRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = noteRepository.findAllByUser(userAuthenticationService.getCurrentUser());
        return notes;
    }

    public void deleteAllNotes() {
        noteRepository.deleteAllByUser(userAuthenticationService.getCurrentUser());
    }

    public Note createNewNote(NoteInfo noteInfo) {
        Note note = new Note(noteInfo.getTitle(), noteInfo.getBody(), userAuthenticationService.getCurrentUser());
        return noteRepository.save(note);
    }

    public Note getNoteById(String id) throws AccessDeniedException{
        Note note =  noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note","id",id));
        if(!userAuthenticationService.getCurrentUser().getUsername().equals(note.getUser().getUsername())){
            throw new AccessDeniedException("Unauthorised Request");
        }
        return note;
    }

    public Note updateNote(String id, NoteInfo noteInfo) throws AccessDeniedException{
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note","id",id));
        if(!userAuthenticationService.getCurrentUser().getUsername().equals(note.getUser().getUsername())){
            throw new AccessDeniedException("Unauthorised Request");
        }
            note.setBody(noteInfo.getBody());
        note.setTitle(noteInfo.getTitle());
        return noteRepository.save(note);
    }

    public void deleteNote(String id)  throws AccessDeniedException {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note","id",id));
        if(!userAuthenticationService.getCurrentUser().getUsername().equals(note.getUser().getUsername())){
            throw new AccessDeniedException("Unauthorised Request");
        }
        noteRepository.deleteById(id);
    }
}
