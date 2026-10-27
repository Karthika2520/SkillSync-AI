package com.skillsync.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.entity.Resume;
import com.skillsync.backend.service.ResumeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController 
{
    private final ResumeService ser;
    public ResumeController(ResumeService ser)
    {
        this.ser=ser;
    }

    @PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resume> uploadResume(@RequestParam ("file") MultipartFile file)
    {
        if(file.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please select a pdf file");
        }
        if(file.getSize() > 5*1024*1024)
        {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"file must be 5 MB or smaller");
        }

        String filename=file.getOriginalFilename();

        if(filename==null || !filename.toLowerCase().endsWith(".pdf"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only PDF files are allowed");
        }

        try(var document=Loader.loadPDF(file.getBytes()))
        {
            String text=new PDFTextStripper().getText(document).trim();
            if(text.isBlank())
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No readable text found, Scanned PDFs are not supported yet.");
            }

            Resume res=new Resume();
            res.setFilename(filename);
            res.setContentType(MediaType.APPLICATION_PDF_VALUE);
            res.setExtractedText(text);

            
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ser.uploadcontent(res));

        }
        catch(IOException e)
        {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to read this PDF");
        }
    }
    
    @GetMapping 
    public List<Resume> getresumes()
    {
        return ser.getAllresumes();
    }
    
    @GetMapping ("/{id}")
   public ResponseEntity<Resume> getbyid(@PathVariable Long id)
   {
    Optional<Resume> r=ser.getid(id);
    if(r==null)return ResponseEntity.notFound().build();

    return ResponseEntity.ok(r.get());
   }




}
