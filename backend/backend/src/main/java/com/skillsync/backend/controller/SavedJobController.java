package com.skillsync.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.entity.savedJob;
import com.skillsync.backend.service.savedJobService;

@RestController 
@RequestMapping("/api/saved-jobs")
@CrossOrigin (origins="http://localhost:5173")
public class SavedJobController 
{
    private final savedJobService ser;
    public SavedJobController(savedJobService ser)
    {
        this.ser=ser;
    } 
    
    @GetMapping
    public ResponseEntity<List<savedJob>> getAll()
    {
        return ResponseEntity.ok(ser.getall());

    }

    @PostMapping
    public ResponseEntity<savedJob> savejob(@RequestBody savedJob job)
    {
        if(job.getTitle()==null || job.getTitle().isBlank())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"job title is required");
        }
        job.setTitle(job.getTitle().trim());
        return ResponseEntity.ok(ser.savejob(job));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletebyid(@PathVariable Long id)
    {
       Optional <savedJob> r=ser.deleteid(id);
       if(r.isEmpty())
       {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Saved job not found");
       }
       else{
        return ResponseEntity.ok("Successfully deleted");
       }

    }



}
